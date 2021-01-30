package com.insulinpump.component;

import com.insulinpump.Util;
import lombok.Getter;
import lombok.Setter;

/**
 * This mockup class represents a basic battery powered blood sugar sensor.
 */
public class Sensor {
    
    private int sugar_level = 130;

    @Getter
    @Setter
    private Battery battery = new Battery();

    /**
     * This methods return the blood-sugar level.
     * The charge of battery is calculated by com.insulinpump.component.Battery() class.
     */
    public int getSugar_level(){
        if(sugar_level > 180) {
            sugar_level += (int) (Math.random() * 10) - 10;
        } if(sugar_level < 50) {
            sugar_level += (int) (Math.random() * 20);
        } else {
            sugar_level += (int) (Math.random() * 20) - 9;
        }
        battery.decreaseLevel(1);
        return sugar_level;
    }

    /**
     * This methods return the battery level of sensor.
     * The charge of battery is calculated by com.insulinpump.component.Battery() class.
     */
    public int getBatteryPercentage(){
        if (battery.getBatteryPercentage() == 0){
            battery = new Battery();
        }
        return battery.getBatteryPercentage();
    }

    public boolean checkStatus() {
        return !Util.DEBUG_SENSOR_CHECK && battery.getChargeLevel() > 0;
    }
}