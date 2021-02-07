package com.insulinpump.unittest;

import com.insulinpump.unittest.component.BatteryTest;
import com.insulinpump.unittest.component.DisplayTest;
import com.insulinpump.unittest.component.PumpTest;
import com.insulinpump.unittest.component.SensorTest;
import com.insulinpump.unittest.repository.IniezioneRepositoryTest;
import com.insulinpump.unittest.repository.MisuraRepositoryTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ControllerTest.class, SensorTest.class, PumpTest.class, BatteryTest.class, IniezioneRepositoryTest.class, MisuraRepositoryTest.class, DisplayTest.class})
public class UnitTestSuite {
}
