package com.insulinpump.acceptancetest;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlStrong;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static org.junit.Assert.*;

public class DisplayTest {
    private WebClient webClient;
    private final String BASEURL = "http://localhost:8080/";
    @Before
    public void prepare() {
        webClient = new WebClient();
    }

    @After
    public void close() {
        webClient.close();
    }

    private HtmlPage goToDashboard() throws IOException {
        HtmlPage page = webClient.getPage(BASEURL);
        assertEquals("Dashboard Title differs", "Insulin Pump - Dashboard", page.getTitleText());
        return page;
    }
    /**
     * This tests simulates a user that presses the Graph icon on the display and navigates the graph page.
     * The user will first press the graph on the main view (dashboard).
     * Then he will switch the type of graph a few times and only then it'll return to the main page.
     * Finally, the user will press the trash can icon to empty the database.
     */
    @Test
    public void checkGraphsViews() {
        HtmlPage page;
        try {
            page = goToDashboard();
            //Click the Graph icon via xpath
            HtmlAnchor graphLink = page.getFirstByXPath("//i[contains(@class, 'bi-graph-down')]/ancestor::a");
            assertNotNull(graphLink);
            page = graphLink.click();
            //check if we're in the correct page (graph)
            assertEquals("Graph page Title differs", "Insulin Pump - Sugar Level Graph", page.getTitleText());
            //check if a bunch of known elements exist
            //1. the graph canvas
            assertNotNull(page.getElementById("chartContainer"));
            //2. The two buttons
            HtmlAnchor nextLink = page.getFirstByXPath("//i[contains(@class, 'bi-arrow-right')]/ancestor::a");
            assertNotNull(nextLink);
            assertNotNull(page.getFirstByXPath("//i[contains(@class, 'bi-arrow-return-left')]/ancestor::a"));

            //click on the button to go to the next graph page
            page = nextLink.click();
            assertEquals("Graph page Title differs", "Insulin Pump - Injected Insulin Graph", page.getTitleText());

            //check if a bunch of known elements exist
            //1. the graph canvas
            assertNotNull(page.getElementById("chartContainer"));
            //2. The two buttons
            nextLink = page.getFirstByXPath("//i[contains(@class, 'bi-house')]/ancestor::a");
            assertNotNull(nextLink);
            assertNotNull(page.getFirstByXPath("//i[contains(@class, 'bi-arrow-return-left')]/ancestor::a"));

            //go home
            page = nextLink.click();
            assertEquals("Dashboard Title differs", "Insulin Pump - Dashboard", page.getTitleText());

            //press the trash can icon
            HtmlAnchor trashcanLink = page.getFirstByXPath("//i[contains(@class, 'bi-trash-fill')]/ancestor::a");
            assertNotNull(trashcanLink);
            page = trashcanLink.click();
            //check that it's still in the dashboard
            assertEquals("Dashboard Title differs", "Insulin Pump - Dashboard", page.getTitleText());

        } catch (IOException e) {
            fail("Failed loading webpage");
        }
    }

    /**
     * This test simulates a scenario where the sensor battery is running low, so the icon changes accordingly and the buzzer starts ringing.
     * At this point the user replaces the battery.
     */
    @Test
    public void checkBatteryIcon() {
        HtmlPage page;
        try {
            for(int i = 0; i < 5; i++) {
                webClient.getPage(BASEURL + "decreaseSensorBattery");
            }
            page = goToDashboard();
            assertNotNull("When battery is low, the buzzer should be ringing", page.getFirstByXPath("//audio"));
            assertNotNull("When the battery is low, the icon should be displayed", page.getFirstByXPath("//i[contains(@class, 'bi-exclamation-triangle-fill')]"));
            assertNotNull("When the battery is low, it should show that particular image", page.getFirstByXPath("//img[contains(@src, 'images/batterybig0.png')]"));

            //Simulate user recharging batteries
            webClient.getPage(BASEURL + "rechargeBatteries");

            page = goToDashboard();
            assertNull(page.getFirstByXPath("//audio"));
            assertNull(page.getFirstByXPath("//i[contains(@class, 'bi-exclamation-triangle-fill')]"));
            assertNull(page.getFirstByXPath("//img[contains(@src, 'images/batterybig0.png')]"));


        } catch (IOException e) {
            fail("Failed loading webpage");
        }
    }

    /**
     * This test simulates a scenario where the Pump ran out of insulin, so the warning icon appears accordingly and the buzzer starts ringing to signal the user that it's time to refill the reservoir.
     * The user refills the reservoir.
     * The warning should be gone now.
     */
    @Test
    public void lackOfInsulin() {
        HtmlPage page;
        try {
            for(int i = 0; i < 5; i++) {
                webClient.getPage(BASEURL + "reduceInsulin");
            }
            page = goToDashboard();
            assertNotNull("When there's no insulin, the buzzer should be ringing", page.getFirstByXPath("//audio"));
            assertNotNull("When there's no insulin, the icon should be displayed", page.getFirstByXPath("//i[contains(@class, 'bi-exclamation-triangle-fill')]"));
            HtmlSpan reservoirLevel = page.getFirstByXPath("//div[contains(@class, 'pReservoir')]/span");
            assertEquals("Insulin should be 0 right now", "0 u", reservoirLevel.getVisibleText());

            //Simulate user refilling the reservoir
            webClient.getPage(BASEURL + "refillInsulin");

            page = goToDashboard();
            assertNull(page.getFirstByXPath("//audio"));
            assertNull(page.getFirstByXPath("//i[contains(@class, 'bi-exclamation-triangle-fill')]"));
            reservoirLevel = page.getFirstByXPath("//div[contains(@class, 'pReservoir')]/span");
            assertNotEquals( "Insulin shouldn't be 0 right now","0 u", reservoirLevel.getVisibleText());
            int reservoirLevel_size = reservoirLevel.getVisibleText().length() - 2;
            assertTrue("Insulin should be somewhat near 300 now", Integer.parseInt(reservoirLevel.getVisibleText().substring(0, reservoirLevel_size)) > 280);


        } catch (IOException e) {
            fail("Failed loading webpage");
        }
    }


    /**
     * This test simulates a scenario where the pump battery is running low, so the icon changes accordingly and the buzzer starts ringing.
     * At this point the user replaces the battery.
     */
    @Test
    public void checkPumpBatteryIcon() {
        HtmlPage page;
        try {
            for(int i = 0; i < 5; i++) {
                webClient.getPage(BASEURL + "decreasePumpBattery");
            }
            page = goToDashboard();
            assertNotNull("When battery is low, the buzzer should be ringing", page.getFirstByXPath("//audio"));
            assertNotNull("When the battery is low, the icon should be displayed", page.getFirstByXPath("//i[contains(@class, 'bi-exclamation-triangle-fill')]"));
            assertNotNull("When the battery is low, it should show that particular image", page.getFirstByXPath("//img[contains(@src, 'images/batterybig0.png')]"));

            //Simulate user recharging batteries
            webClient.getPage(BASEURL + "rechargeBatteries");

            page = goToDashboard();
            assertNull(page.getFirstByXPath("//audio"));
            assertNull(page.getFirstByXPath("//i[contains(@class, 'bi-exclamation-triangle-fill')]"));
            assertNull(page.getFirstByXPath("//img[contains(@src, 'images/batterybig0.png')]"));

        } catch (IOException e) {
            fail("Failed loading webpage");
        }
    }

    /**
     * This test simulates a scenario where a human checks that the display components are being displayed correctly without any format error.
     */
    @Test
    public void correctPageTest() {
        HtmlPage page;
        try {
            page = goToDashboard();
            // Check date
            HtmlSpan dateDisplayed = page.getFirstByXPath("//div[contains(@class, 'Data')]/span");
            try {
                LocalDate.parse(dateDisplayed.getVisibleText(), DateTimeFormatter.ISO_LOCAL_DATE);
            } catch(DateTimeParseException e) {
                fail("Date displayed in a wrong format --> " + dateDisplayed.getVisibleText());
            }
            //Check time
            HtmlSpan timeDisplayed = page.getFirstByXPath("//div[contains(@class, 'Ora')]/span");
            try {
                LocalTime.parse(timeDisplayed.getVisibleText(), DateTimeFormatter.ofPattern("HH:mm"));
            } catch(DateTimeParseException e) {
                fail("Time displayed in a wrong format --> " + timeDisplayed.getVisibleText());
            }
            //Check pump reservoir
            HtmlSpan pumpReservoir = page.getFirstByXPath("//div[contains(@class, 'pReservoir')]/span");
            int pumpReservoirStringLen;
            int pumpReservoirValue;

            try {
                pumpReservoirStringLen = pumpReservoir.getVisibleText().length();
                assertTrue(pumpReservoirStringLen > 2);
                pumpReservoirStringLen -= 2;
                pumpReservoirValue = Integer.parseInt(pumpReservoir.getVisibleText().substring(0, pumpReservoirStringLen));
                assertTrue("Pump reservoir should be between 0 and 300",  pumpReservoirValue > 0 && pumpReservoirValue <= 300);
            } catch(Exception e) {
                fail("Pump reservoir level displayed in a wrong format --> " + pumpReservoir.getVisibleText());
            }

            //Check pump battery
            HtmlSpan pumpBattery = page.getFirstByXPath("// div[contains(@class, 'pBattery')]//span");
            int pumpBatteryStringLen;
            int pumpBatteryValue;
            try {
                pumpBatteryStringLen = pumpBattery.getVisibleText().length();
                assertTrue(pumpBatteryStringLen > 1);
                pumpBatteryStringLen -= 1;
                pumpBatteryValue = Integer.parseInt(pumpBattery.getVisibleText().substring(0, pumpBatteryStringLen));
                assertTrue("Pump battery should be between 0 and 100",  pumpBatteryValue > -1 && pumpBatteryValue <= 100);
            } catch(Exception e) {
                fail("Pump battery level displayed in a wrong format --> " + pumpBattery.getVisibleText());
            }

            //Check sensor battery
            HtmlSpan sensorBattery = page.getFirstByXPath("//div[contains(@class, 'sBattery')]//span");
            try {
                int sensorBatteryStringLen = sensorBattery.getVisibleText().length();
                assertTrue(sensorBatteryStringLen > 0);
                int sensorBatteryPercentage = Integer.parseInt(sensorBattery.getVisibleText().substring(0, sensorBatteryStringLen - 1));
                assertTrue("Sensor battery should be between 0 and 100", sensorBatteryPercentage > -1 && sensorBatteryPercentage <= 100);
            } catch(Exception e) {
                fail("Sensory battery percentage displayed in a wrong format --> " + sensorBattery.getVisibleText());
            }

            //Check Sugar Level
            HtmlStrong sugarLevelByPage = page.getFirstByXPath("//div[contains(@class, 'sugar')]//strong");
            try {
                int sugarLevel = Integer.parseInt(sugarLevelByPage.getVisibleText());
                assertTrue("Sugar Level be between 0 and 200", sugarLevel > -1 && sugarLevel <= 200);
            } catch(Exception e) {
                fail("Sugar level displayed in a wrong format --> " + sugarLevelByPage.getVisibleText());
            }

        } catch (IOException e) {
            fail("Failed loading webpage");
        }
    }

}
