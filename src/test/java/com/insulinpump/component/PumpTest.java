package com.insulinpump.component;
import com.insulinpump.Util;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.*;
public class PumpTest {
    private Pump pump;

    @Before
    public void init() {
        pump = new Pump();
    }

    @Test
    public void testSensorStatus() {
        Util.DEBUG_PUMP_CHECK = true;
        assertFalse("Pump should be forced to signal a malfunction", pump.checkStatus());
        Util.DEBUG_PUMP_CHECK = false;
    }

    @Test
    public void testBatteryStatus() {
        pump.getBattery().setChargeLevel(0);
        assertFalse("Sensor should malfunction with low battery", pump.checkStatus());
        pump.getBattery().rechargeBattery();
        assertTrue("Sensor shouldn't malfunction with full battery", pump.checkStatus());
    }

    @Test
    public void testReservoirStatus() {
        pump.setReservoir(0);
        assertFalse("Sensor should malfunction with low insulin level in the reservoir", pump.checkStatus());
        pump.setReservoir(pump.MINIMUM_RESERVOIR_LEVEL);
        pump.getBattery().rechargeBattery();
        assertTrue("Sensor shouldn't malfunction with the just enough insulin to supply MAX_DOSE",pump.checkStatus());
        pump.setReservoir(pump.MINIMUM_RESERVOIR_LEVEL - 1);
        assertFalse("Sensor should malfunction without enough insulin to supply MAX_DOSE", pump.checkStatus());
    }

    @Test
    public void testReservoirRefill() {
        pump.setReservoir(0);
        assertEquals("Pump's reservoir should be empty", 0, pump.getReservoir());
        pump.refill();
        assertEquals("Pump's reservoir should be full", Util.RESERVOIR_CAPACITY, pump.getReservoir());
    }

    @Test
    public void testInsulinInjection() {
        pump.refill();
        pump.injectInsulin(1);
        assertEquals("Insulin Reservoir level should've been lowered by one", Util.RESERVOIR_CAPACITY - 1, pump.getReservoir());
    }
}
