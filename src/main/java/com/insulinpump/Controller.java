package com.insulinpump;

import com.insulinpump.component.*;
import com.insulinpump.entity.Misura;
import com.insulinpump.repository.IniezioneRepository;
import com.insulinpump.repository.MisuraRepository;
import lombok.Getter;
import lombok.Synchronized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerArray;

@Service
public class Controller {

    @Getter
    private Pump pump;
    @Getter
    private Sensor sensor;
    @Getter
    private Display display_one;
    @Getter
    private Display display_two;
    @Getter
    private Clock clock;
    @Getter
    private Buzzer buzzer;
    @Getter
    private DBManager dbManager;

    private final AtomicIntegerArray last_measurements = new AtomicIntegerArray(3);
    private final boolean[] last_check = new boolean[2];

    @Autowired
    public Controller(MisuraRepository misuraRepository, IniezioneRepository iniezioneRepository){
        sensor = new Sensor();
        pump = new Pump();
        display_one = display_two = new Display();
        clock = new Clock(this);
        buzzer = new Buzzer();

        dbManager = new DBManager(misuraRepository, iniezioneRepository);
        restoreLastMeasurements();

    }

    public final Runnable insulinTask = () -> {
        long result = readAndSaveReading();
        if(result == (long)sensor.SENSOR_ERROR) {
            //An error has occurred while reading from sensor. The system will alert the user
            last_check[0] = true;
            buzzer.start();
            display_one.setCheck_sensor(true);
            display_two.setCheck_sensor(true);
            return;
        }
        int dose = computeDose();
        if(dose != 0) {
            if(!pump.injectInsulin(dose)) {
                //An error has occurred while injecting insulin. The system will alert the user
                last_check[1] = true;
                buzzer.start();
                display_one.setCheck_pump(true);
                display_two.setCheck_pump(true);
                return;
            }
        }

        //Insert injection into the DB for logging purposes
        dbManager.sqlIteInsertInjection(dose, clock.getTime());

    };

    public final Runnable systemCheckTask = () -> {
        /*
         First array posix indicates error for sensor
         Second array posix indicates error for pump
         */
        if(!pump.checkStatus()) {
            if (last_check[1]){
                buzzer.start();
                display_one.setCheck_pump(true);
                display_two.setCheck_pump(true);
            } else {
                //First time error occurs, will check again in one minute before emitting a sound signal
                last_check[1] = true;
            }
        } else {
            last_check[1] = false;
            buzzer.stop();
            display_one.setCheck_pump(false);
            display_two.setCheck_pump(false);
        }
        if(!sensor.checkStatus()) {
            if(last_check[0]) {
                buzzer.start();
                display_one.setCheck_sensor(true);
                display_two.setCheck_sensor(true);
            } else {
                last_check[0] = true;
            }
        } else {
            last_check[0] = false;
            buzzer.stop();
            display_one.setCheck_sensor(false);
            display_two.setCheck_sensor(false);
        }
    };


    /**
     * Refreshes the information sent to the 2 displays
     */
    public final Runnable refreshDisplays = () -> {
        // Set sugar level on display
        display_one.setSugarLevel(last_measurements.get(2));
        display_two.setSugarLevel(last_measurements.get(2));

        //Update Reservoir
        display_one.setPumpReservoir(pump.getReservoir());
        display_two.setPumpReservoir(pump.getReservoir());

        //Update batteries
        display_one.setPumpBatteryLevel(pump.getBattery().getBatteryPercentage());
        display_two.setPumpBatteryLevel(pump.getBattery().getBatteryPercentage());
        display_one.setSensorBatteryLevel(sensor.getBattery().getBatteryPercentage());
        display_two.setSensorBatteryLevel(sensor.getBattery().getBatteryPercentage());
    };
    /**
     * Calculates the correct insulin dose based on the last 3 readings.
     * @return the insulin dose.
     */
    @Synchronized("last_measurements")
    public int computeDose() {
        /* reading >= safe zone min, so if dose = 0 no call injectInsuline */
        int dose;
        int lastMeasured = last_measurements.get(2);
        if (lastMeasured >= Util.SAFE_ZONE_MIN && lastMeasured <= Util.SAFE_ZONE_MAX) {
            dose = computeSafeInsulinDose();
        } else if (lastMeasured < Util.SAFE_ZONE_MIN) {
            dose = 0;
        } else {
            dose = computeUnsafeInsulinDose();
        }
        System.out.println("Dose = " + dose);
        return dose;
    }

    /**
     * Calculates the insulin dose based on the last 3 readings when the sugar level is in the safe zone.
     * @return the insulin dose.
     */
    @Synchronized("last_measurements")
    public int computeSafeInsulinDose() {
        int r2 = last_measurements.get(2);
        int r1 = last_measurements.get(1);
        int r0 = last_measurements.get(0);
        if(r2 <= r1) return 0;
        if(r2 - r1 < r1 - r0) return 0;
        int compDose = (r2 - r1) / (4*18); //1 mmol/L = 18 mg/dL
        if(compDose == 0) compDose = Util.MINIMUM_DOSE;
        return compDose;
    }

    /**
     * Calculates the insulin dose based on the last 3 readings when the sugar level is not in the safe zone.
     * @return the insulin dose.
     */
    @Synchronized("last_measurements")
    public int computeUnsafeInsulinDose() {
        // without specific requirements
        if(last_measurements.get(2) > Util.DANGEROUS_ZONE)
            return 1 + 2 * computeSafeInsulinDose();
        else
            return 2 * computeSafeInsulinDose();
    }

    /**
     * Loads into "last_measurements" array the two most recent sugar level measurements from the Database, as long as they're not older than 45 minutes.
     * Otherwise it'll use a newly made reading from the sensor.
     */
    @Synchronized("last_measurements")
    private void restoreLastMeasurements() {
        //Do a measurement as soon as the controller boots up. this will be our R2
        //Fill preemptively the other 2 array cells with the same value. If suitable readings will be found in the DB, those ones will be used instead.
        int currentReading = sensor.getSugar_level();
        forceLastReadings(currentReading, currentReading, currentReading);


        //Read up to last 2 measurements from the DB. If they're fresh, they'll become our R1 and R0
        List<Misura> rs = dbManager.getLastNReadings(2);

        int i = 1;
        for(Misura m : rs) {
            if(i < 0) break;
            LocalDateTime timestamp = m.getDate();
            if(timestamp != null) {
                long minutes = ChronoUnit.MINUTES.between(clock.getTime(), timestamp);
                if(minutes < 0) minutes = - minutes;
                if (minutes <= Util.CONTROLLER_BOOT_FRESHNESS_MINUTES) {
                    last_measurements.set(i--, m.getLettura());
                }
            }
        }
        //add latest reading (new) to DB
        dbManager.sqlIteInsertReading(last_measurements.get(2), clock.getTime());
    }

    /**
     * Reads the sugar level from the sensors then stores it in the DB.
     * @return the result code of the DB insertion
     */
    @Synchronized("last_measurements")
    private long readAndSaveReading() {
        int newSugarLevel = sensor.SENSOR_ERROR;
        for(int i = 0; i < 3 && newSugarLevel == sensor.SENSOR_ERROR; i++) {
            //if a network problem occurs, try again
            newSugarLevel = sensor.getSugar_level();
        }
        if(newSugarLevel == sensor.SENSOR_ERROR) return (long)newSugarLevel;

        last_measurements.set(0 ,last_measurements.get(1));
        last_measurements.set(1, last_measurements.get(2));
        last_measurements.set(2, newSugarLevel);
        return dbManager.sqlIteInsertReading(newSugarLevel, clock.getTime());
    }

    /**
     *
     */
    @Synchronized("last_measurements")
    public void forceLastReadings(int r0, int r1, int r2) {
        last_measurements.set(0, r0);
        last_measurements.set(1, r1);
        last_measurements.set(2, r2);
    }

}
