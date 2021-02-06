package com.insulinpump.acceptancetest;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

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
     */
    @Test
    public void checkGraphsViews() {
        HtmlPage page = null;
        try {
            page = goToDashboard();
            //Click the Graph icon via xpath
            HtmlAnchor graphLink = (HtmlAnchor)page.getFirstByXPath("//i[contains(@class, 'bi-graph-down')]/ancestor::a");
            assertTrue(null != graphLink);
            page = graphLink.click();
            //check if we're in the correct page (graph)
            assertEquals("Graph page Title differs", "Insulin Pump - Sugar Level Graph", page.getTitleText());
            //check if a bunch of known elements exist
            //1. the graph canvas
            assertTrue(null != page.getElementById("chartContainer"));
            //2. The two buttons
            HtmlAnchor nextLink = (HtmlAnchor)page.getFirstByXPath("//i[contains(@class, 'bi-arrow-right')]/ancestor::a");
            assertTrue(null != nextLink);
            assertTrue(null != page.getFirstByXPath("//i[contains(@class, 'bi-arrow-return-left')]/ancestor::a"));

            //click on the button to go to the next graph page
            page = nextLink.click();
            assertEquals("Graph page Title differs", "Insulin Pump - Injected Insulin Graph", page.getTitleText());

            //check if a bunch of known elements exist
            //1. the graph canvas
            assertTrue(null != page.getElementById("chartContainer"));
            //2. The two buttons
            nextLink = (HtmlAnchor)page.getFirstByXPath("//i[contains(@class, 'bi-house-door')]/ancestor::a");
            assertTrue(null != nextLink);
            assertTrue(null != page.getFirstByXPath("//i[contains(@class, 'bi-arrow-return-left')]/ancestor::a"));

            //go home
            page = nextLink.click();
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
        HtmlPage page = null;
        try {
            for(int i = 0; i < 5; i++) {
                webClient.getPage(BASEURL + "decreaseSensorBattery");
            }
            page = goToDashboard();
            assertTrue("When battery is low, the buzzer should be ringing", null != page.getFirstByXPath("//audio"));
            assertTrue("When the battery is low, the icon should be displayed",null != page.getFirstByXPath("//i[contains(@class, 'bi-exclamation-triangle-fill')]"));
            assertTrue("When the battery is low, it should show that particular image", null != page.getFirstByXPath("//img[contains(@src, 'images/batterybig0.png')]"));
            webClient.getPage(BASEURL + "rechargeBatteries");
            page = goToDashboard();
            assertTrue(null == page.getFirstByXPath("//audio"));
            assertTrue(null == page.getFirstByXPath("//i[contains(@class, 'bi-exclamation-triangle-fill')]"));
            assertTrue(null == page.getFirstByXPath("//img[contains(@src, 'images/batterybig0.png')]"));


        } catch (IOException e) {
            fail ("Failed loading webpage");
        }
    }
}
