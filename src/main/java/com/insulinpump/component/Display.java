package com.insulinpump.component;

import lombok.Data;

@Data
public class Display {

    private int sugarLevel;
    private int pumpReservoir;
    private int sensorBatteryLevel;
    private int pumpBatteryLevel;
    private boolean check_sensor;
    private boolean check_pump;
}