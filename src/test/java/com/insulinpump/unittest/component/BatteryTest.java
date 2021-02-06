package com.insulinpump.component;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class BatteryTest {

    private Battery battery;

    @Before
    public void init(){
        battery = new Battery();
    }

    @Test
    public void testDecreaseLevel(){
        assertEquals("Battery level should be to 10000", 10000, battery.decreaseLevel(80));
    }

    @Test
    public void testBatteryPercentage(){
        int percentage = battery.getBatteryPercentage();
        assertTrue("Battery percentage should be between 0 and 100", percentage > -1 && percentage < 101);
        battery.decreaseLevel(11000);
        assertEquals("Battery percentage should be 0", 0, battery.getBatteryPercentage());
    }

    @Test
    public void testRechargeBattery(){
        battery.rechargeBattery();
        assertEquals(60*24*7, battery.getChargeLevel());
    }
}
