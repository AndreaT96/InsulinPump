package com.insulinpump.component;

import com.insulinpump.Util;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.*;

public class SensorTest {
    private Sensor sensor;

    @Before
    public void init() {
        sensor = new Sensor();
    }

    @Test
    public void testSensorStatus() {
        Util.DEBUG_SENSOR_CHECK = true;
        assertFalse("Sensor should be forced to signal a malfunction", sensor.checkStatus());
        Util.DEBUG_SENSOR_CHECK = false;
    }

    @Test
    public void testBatteryStatus() {
        sensor.getBattery().setChargeLevel(0);
        assertFalse("Sensor should malfunction with low battery", sensor.checkStatus());
        sensor.getBattery().rechargeBattery();
        assertTrue("Sensor shouldn't malfunction with full battery", sensor.checkStatus());
    }

    @Test
    public void testSugarLevel(){
        int sugar_level = sensor.getSugar_level();
        assertTrue("Sugar level shouldn't be more than 200 or less than 50",sugar_level > 50 && sugar_level < 200);
    }

}
