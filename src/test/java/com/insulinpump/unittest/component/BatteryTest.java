package com.insulinpump.unittest.component;

import com.insulinpump.component.Battery;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BatteryTest {

    private Battery battery;

    @Before
    public void init(){
        battery = new Battery();
    }

    /**
     * This test aims to check the correct functionality of the decreaseLevel() method
     */
    @Test
    public void testDecreaseLevel(){
        assertEquals("Battery level should be to 10000", 10000, battery.decreaseLevel(80));
    }

    /**
     * this test aims to check the correct functionality of the getBatteryPercentage() method
     */
    @Test
    public void testBatteryPercentage(){
        int percentage = battery.getBatteryPercentage();
        assertTrue("Battery percentage should be between 0 and 100", percentage > -1 && percentage < 101);
        battery.decreaseLevel(11000);
        assertEquals("Battery percentage should be 0", 0, battery.getBatteryPercentage());
    }

    /**
     * This test aims to check the correct functionality of the getChargeLevel() method
     */
    @Test
    public void testRechargeBattery(){
        battery.recharge();
        assertEquals(60*24*7, battery.getChargeLevel());
    }

    /**
     * this test aims to check the correct functionality of the constructor Battery(int capacity)
     */
    @Test
    public void testBatteryWithCustomCapacity() {
        battery = new Battery(100);
        assertEquals("Battery should have capacity 100",100, battery.getOriginalCapacity());
    }
}
