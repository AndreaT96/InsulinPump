package com.insulinpump.unittest.component;

import com.insulinpump.Util;
import com.insulinpump.component.Sensor;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.Repeat;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        sensor.getBattery().recharge();
        assertTrue("Sensor shouldn't malfunction with full battery", sensor.checkStatus());
    }

    @Test
    @Repeat(10000)
    public void testSugarLevel(){
        int sugar_level = sensor.getSugar_level();
        assertTrue("Sugar level shouldn't be more than 200 or less than 50",sugar_level == sensor.SENSOR_ERROR || (sugar_level > 50 && sugar_level < 200));
    }

}
