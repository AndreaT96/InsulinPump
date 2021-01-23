package demo;

import lombok.*;
/**
 * This mockup class represents the pump component of the insulin pump. It contains a reservoir of insulin and is capable to inject it into the patient. It's powered by a battery.
 */
public class Pump {
    @Getter @Setter int reservoir;
    private Battery battery;
    private final int MINIMUM_RESERVOIR_LEVEL = 7;
    public  Pump() {
        battery = new Battery();
        reservoir = 300;
    }

    /**
     * Checks the status of the pump
     * @return true if the pump is working properly, else if a mechanical problem has been detected.
     */
    public boolean checkStatus() {
        return reservoir >= MINIMUM_RESERVOIR_LEVEL && battery.getChargeLevel() > 0;
    }

    /**
     * Injects the desired amount of insulin to the patient
     * @param amount quantity of insulin units to be injected
     * @return true if the insulin was successfully injected, false otherwise
     */
    public boolean injectInsulin(int amount) {
        //amount is used as "battery consumption" as well.
        if(amount < 0 || reservoir < amount || battery.getChargeLevel() < amount) return false;
        try {
            Thread.sleep(amount* 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        reservoir -= amount;
        battery.decreaseLevel(amount);
        if(battery.getBatteryPercentage() < MINIMUM_RESERVOIR_LEVEL)
            battery = new Battery();
        return true;
    }
}