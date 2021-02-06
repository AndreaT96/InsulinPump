package com.insulinpump.component;

import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;

@Getter(onMethod_={@Synchronized})
@Setter(onMethod_={@Synchronized})
public class Display {

    private int sugarLevel;
    private int pumpReservoir;
    private int sensorBatteryLevel;
    private int pumpBatteryLevel;
    private boolean check_sensor;
    private boolean check_pump;
}