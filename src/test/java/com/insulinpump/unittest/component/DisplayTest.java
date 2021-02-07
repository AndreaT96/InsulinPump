package com.insulinpump.unittest.component;

import com.insulinpump.component.Display;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DisplayTest {

    private Display display;

    @Before
    public void init() {
        display = new Display();
    }

    /**
     * This simple test aims to check the correctness of all setters and getters.
     */
    @Test
    public void testGetterSetters() {
        display.setPumpBatteryLevel(1);
        display.setSugarLevel(2);
        display.setPumpReservoir(3);
        display.setSensorBatteryLevel(4);
        display.setCheck_pump(true);
        display.setCheck_sensor(true);

        assertTrue("check_pump should be True", display.isCheck_pump());
        assertTrue("check sensor should be True", display.isCheck_sensor());
        assertEquals("Pump battery should be 1", 1, display.getPumpBatteryLevel());
        assertEquals("Sugar level should be 2", 2, display.getSugarLevel());
        assertEquals("Pump reservoir should be 3", 3, display.getPumpReservoir());
        assertEquals("Sensor battery should be 4", 4, display.getSensorBatteryLevel());

    }
}
