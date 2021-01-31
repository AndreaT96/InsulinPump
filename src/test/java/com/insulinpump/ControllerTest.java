package com.insulinpump;

import com.insulinpump.Util;
import com.insulinpump.entity.Misura;
import com.insulinpump.repository.IniezioneRepository;
import com.insulinpump.repository.MisuraRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class ControllerTest {

    //@Mock
    private Controller controller;

    @Mock
    MisuraRepository misuraRepository;
    @Mock
    IniezioneRepository iniezioneRepository;

    @Before
    public void init(){
        Mockito.when(misuraRepository.save(Mockito.any(Misura.class))).thenAnswer(i -> i.getArguments()[0]);
        assertTrue(misuraRepository != null && iniezioneRepository != null );
        controller = Mockito.spy(new Controller(misuraRepository, iniezioneRepository));
    }

    @Test
    public void testGettersNonNull() {
        assertTrue("Pump shouldn't be null", controller.getPump() != null);
        assertTrue("Clock shouldn't be null", controller.getClock() != null);
        assertTrue("Sensor shouldn't be null", controller.getSensor() != null);
        assertTrue("Display_one shouldn't be null", controller.getDisplay_one() != null);
        assertTrue("Display_two shouldn't be null", controller.getDisplay_two() != null);
        assertTrue("DB manager shouldn't be null", controller.getDbManager() != null);
        assertTrue("Buzzer shouldn't be null", controller.getBuzzer() != null);

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

}
