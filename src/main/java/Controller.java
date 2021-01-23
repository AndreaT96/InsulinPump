import lombok.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Controller {

    @Getter private Pump pump;
    @Getter private Sensor sensor;
    @Getter private Display display_one;
    @Getter private Display display_two;
    @Getter private Clock clock;
    @Getter private DBManager dbManager;

    private int[] last_measurements = new int[3];

    public Controller(){
        sensor = new Sensor();
        pump = new Pump();
        display_one = display_two = new Display(); //TODO parte grafica
        clock = new Clock(this);

        try {
            /*
             * System initialization
             */
            dbManager = new DBManager();
            restoreLastMeasurements();
            clock.startTasks();



        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public final Runnable insulinTask = () -> {
        try {
            readAndSaveReading();
            /* reading >= safe zone min, so if dose = 0 no call injectInsuline */
            int dose;

            if (last_measurements[2] >= Util.SAFE_ZONE_MIN && last_measurements[2] <= Util.SAFE_ZONE_MAX) {
                dose = computeSafeInsulinDose();
            } else if (last_measurements[2] < Util.SAFE_ZONE_MIN) {
                dose = 0;
            } else {
                dose = computeUnsafeInsulinDose();
            }
            System.out.println("Dose = " + dose);
            if(dose != 0) {
                pump.injectInsulin(dose);
            }
            dbManager.sqlIteInsertInjection(dose, clock.getTime());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    };

    //TODO bippaggi vari
    public final Runnable systemCheckTask = () -> {
        if(pump.checkStatus()) {
            //System.out.println("La pompa funziona");
        } else {
            System.out.println("La pompa non funziona");
        }
        if(sensor.checkStatus()) {
            //System.out.println("Il sensore funziona");
        } else {
            System.out.println("Il sensore non funziona");
        }
    };

    /**
     * Calculates the insulin dose based on the last 3 readings when the sugar level is in the safe zone.
     * @return the insulin dose.
     */
    public int computeSafeInsulinDose() {
        if(last_measurements[2] <= last_measurements[1]) return 0;
        if(last_measurements[2] - last_measurements[1] < last_measurements[1] - last_measurements[0]) return 0;
        int compDose = (last_measurements[2] - last_measurements[1]) / (4*18); //1 mmol/L = 18 mg/dL
        if(compDose == 0) compDose = Util.MINIMUM_DOSE;
        return compDose;
    }

    /**
     * Calculates the insulin dose based on the last 3 readings when the sugar level is not in the safe zone.
     * @return the insulin dose.
     */
    public int computeUnsafeInsulinDose() {
        // without specific requirements
        if(last_measurements[2] > Util.DANGEROUS_ZONE)
            return 1 + 2 * computeSafeInsulinDose();
        else
            return 2 * computeSafeInsulinDose();
    }

    /**
     * Loads into "last_measurements" array the two most recent sugar level measurements from the Database, as long as they're not older than 45 minutes.
     * Otherwise it'll use a newly made reading from the sensor.
     * @throws SQLException whenever there's a problem with the local DB
     */
    private void restoreLastMeasurements() throws SQLException {
        //Do a measurement as soon as the controller boots up. this will be our R2
        last_measurements[2] = sensor.getSugar_level();

        //Fill preemptively the other 2 array cells with the same value. If suitable readings will be found in the DB, those ones will be used instead.
        last_measurements[1] = last_measurements[2];
        last_measurements[0] = last_measurements[2];

        //Read up to last 2 measurements from the DB. If they're fresh, they'll become our R1 and R0
        ResultSet rs = dbManager.getLastNReadings(2);
        int i = 1;
        while(rs.next() && i >= 0) {
            LocalDateTime timestamp = Util.sqlIteParseDate(rs.getString("data"));
            if(timestamp != null) {
                long minutes = ChronoUnit.MINUTES.between(clock.getTime(), timestamp);
                if(minutes < 0) minutes = - minutes;
                if (minutes <= Util.CONTROLLER_BOOT_FRESHNESS_MINUTES) {
                    last_measurements[i--] = rs.getInt("lettura");
                }
            }
        }
        //add latest reading (new) to DB
        dbManager.sqlIteInsertReading(last_measurements[2], clock.getTime());
    }

    /**
     * Reads the sugar level from the sensors then stores it in the DB.
     * @return the result code of the DB insertion
     * @throws SQLException whenever a DB error occurs
     */
    private int readAndSaveReading() throws SQLException {
        last_measurements[0] = last_measurements[1];
        last_measurements[1] = last_measurements[2];
        last_measurements[2] = sensor.getSugar_level();
        return dbManager.sqlIteInsertReading(last_measurements[2], clock.getTime());
    }
}

