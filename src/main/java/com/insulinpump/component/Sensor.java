package com.insulinpump.component;

import com.insulinpump.Util;
import lombok.Getter;

/**
 * This mockup class represents a basic battery powered blood sugar sensor.
 */
public class Sensor {
    
    private int sugar_level = 130;

    @Getter
    private Battery battery = new Battery();

    public final int SENSOR_ERROR = -1;
    /**
     * This methods return the blood-sugar level.
     * The charge of battery is calculated by com.insulinpump.component.Battery() class.
     * @return SENSOR_ERROR if an error occurred, the sugar level otherwise
     */
    public int getSugar_level(){
        //Simulates network interferences
        if(getBattery().getChargeLevel() < 1 || Math.random() <= Util.PERCENTAGE_SIMULATE_ERROR || Util.DEBUG_FORCE_NETWORK_ERROR) return SENSOR_ERROR;


        if(sugar_level > 180) {
            sugar_level += (int) (Math.random() * 10) - 10;
        } else if(sugar_level < 80) {
            sugar_level += (int) (Math.random() * 20);
        } else if (sugar_level < 130) {
            sugar_level += (int) (Math.random() * 20) - 9;
        } else {
            sugar_level += (int) (Math.random() * 10) - 5;
        }
        battery.decreaseLevel(1);
        return sugar_level;
    }

    /**
     * Checks if the sensor is working properly.
     * It's part of the self diagnostic of the project
     * @return true if it's working as intended, false if a problem was found
     */
    public boolean checkStatus() {
        return !Util.DEBUG_SENSOR_CHECK && battery.getBatteryPercentage() > 5;
    }
}