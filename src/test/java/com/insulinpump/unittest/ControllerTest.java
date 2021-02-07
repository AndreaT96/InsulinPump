package com.insulinpump.unittest;

import com.insulinpump.Controller;
import com.insulinpump.Util;
import com.insulinpump.entity.Iniezione;
import com.insulinpump.entity.Misura;
import com.insulinpump.repository.IniezioneRepository;
import com.insulinpump.repository.MisuraRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class ControllerTest {

    private Controller controller;
    @Mock
    MisuraRepository misuraRepository;
    @Mock
    IniezioneRepository iniezioneRepository;

    @Before
    public void init(){
        assertTrue(misuraRepository != null && iniezioneRepository != null);

        Mockito.lenient().when(misuraRepository.save(any(Misura.class))).thenAnswer(i -> {
            Misura m = (Misura) i.getArguments()[0];
            m.setId(1L);
            return m;
        });
        Mockito.lenient().when(iniezioneRepository.save(any(Iniezione.class))).thenAnswer(i -> {
            Iniezione m = (Iniezione) i.getArguments()[0];
            m.setId(1L);
            return m;
        });


        controller = Mockito.spy(new Controller(misuraRepository, iniezioneRepository));
    }

    @Test
    public void testGettersNonNull() {
        assertNotNull("Pump shouldn't be null", controller.getPump());
        assertNotNull("Clock shouldn't be null", controller.getClock());
        assertNotNull("Sensor shouldn't be null", controller.getSensor());
        assertNotNull("Display_one shouldn't be null", controller.getDisplay_one());
        assertNotNull("Display_two shouldn't be null", controller.getDisplay_two());
        assertNotNull("DB manager shouldn't be null", controller.getDbManager());
        assertNotNull("Buzzer shouldn't be null", controller.getBuzzer());
    }

    @Test
    public void testComputeInsulineZone() {
        assertTrue(controller.computeUnsafeInsulinDose() >= controller.computeSafeInsulinDose());
    }

    @Test
    public void testCorrectInsulinComputationCall() {
        Mockito.reset(controller);
        //Restore some values
        controller.forceLastReadings(Util.SAFE_ZONE_MAX - 2, Util.SAFE_ZONE_MAX - 1, Util.SAFE_ZONE_MAX);
        //chiamo computedose normale
        controller.computeDose();
        //--> verifico che ha chiamato computesafe
        Mockito.verify(controller, Mockito.times(1)).computeSafeInsulinDose();
        Mockito.verify(controller, Mockito.times(0)).computeUnsafeInsulinDose();
        //Restore some values
        Mockito.reset(controller);
        controller.forceLastReadings(Util.SAFE_ZONE_MAX + 1, Util.SAFE_ZONE_MAX + 2, Util.DANGEROUS_ZONE);
        controller.computeDose();
        Mockito.verify(controller, Mockito.times(1)).computeUnsafeInsulinDose();
        Mockito.verify(controller, Mockito.times(1)).computeSafeInsulinDose();
    }

    @Test
    public void testCorrectInsulinComputation() {
        controller.forceLastReadings(150, 130, 140);
        assertEquals("Dose should be 2", 2,  controller.computeDose());
        controller.forceLastReadings(110, 100, 80);
        assertEquals("Dose should be 0", 0,  controller.computeDose());
        controller.forceLastReadings(110, 115, 120);
        assertEquals("Dose should be 1", 1,  controller.computeDose());
    }

    @Test
    public void testBuzzerActivation() {
        Util.DEBUG_SENSOR_CHECK = Util.DEBUG_PUMP_CHECK = true;
        assertFalse(controller.getDisplay_one().isCheck_pump());
        assertFalse(controller.getDisplay_one().isCheck_sensor());
        controller.systemCheckTask.run();
        assertFalse(controller.getDisplay_one().isCheck_pump());
        assertFalse(controller.getDisplay_one().isCheck_sensor());
        controller.systemCheckTask.run();
        assertTrue(controller.getDisplay_one().isCheck_pump());
        assertTrue(controller.getDisplay_one().isCheck_sensor());
        Util.DEBUG_PUMP_CHECK = Util.DEBUG_SENSOR_CHECK = false;
        controller.systemCheckTask.run();
        assertFalse(controller.getDisplay_one().isCheck_pump());
        assertFalse(controller.getDisplay_one().isCheck_sensor());
    }

    @Test
    public void testInjectionTaskErrorSENSOR() {
        Util.DEBUG_FORCE_NETWORK_ERROR = true;
        Mockito.reset(controller);
        controller.insulinTask.run();
        Mockito.verify(controller, Mockito.times(0)).computeDose();
    }

    @Test
    public void testInjectionTaskErrorPUMP() {
        Util.DEBUG_FORCE_PUMP_ERROR = true;
        Mockito.reset(controller);
        controller.insulinTask.run();
        Mockito.verify(controller, Mockito.times(0)).computeDose();
    }

    @Test
    public void testInjectionTaskSuccess() {
        controller.insulinTask.run();
        Mockito.verify(iniezioneRepository, Mockito.times(1)).save(any());
    }

    @Test
    public void testDisplayRefresh() {
        controller.getDisplay_one().setSensorBatteryLevel(-1);
        controller.getDisplay_one().setPumpReservoir(-1);
        controller.getDisplay_one().setSugarLevel(-1);
        controller.getDisplay_one().setPumpBatteryLevel(-1);

        controller.refreshDisplays.run();
        assertNotEquals("Displayed value should've changed", -1, controller.getDisplay_one().getPumpBatteryLevel());
        assertNotEquals("Displayed value should've changed", -1, controller.getDisplay_one().getSensorBatteryLevel());
        assertNotEquals("Displayed value should've changed", -1, controller.getDisplay_one().getSugarLevel());
        assertNotEquals("Displayed value should've changed", -1, controller.getDisplay_one().getPumpReservoir());

    }

    @After
    public void cleanUp() {
        Util.DEBUG_FORCE_PUMP_ERROR = false;
        Util.DEBUG_FORCE_NETWORK_ERROR = false;
        Util.DEBUG_PUMP_CHECK = Util.DEBUG_SENSOR_CHECK = false;
    }

}
