package com.insulinpump.component;

import com.insulinpump.Util;
import lombok.*;
/**
 * This mockup class represents the pump component of the insulin pump. It contains a reservoir of insulin and is capable to inject it into the patient. It's powered by a battery.
 */
public class Pump {
    @Getter
    @Setter
    private int reservoir;

    @Getter
    private Battery battery;
    public final int MINIMUM_RESERVOIR_LEVEL = 3;
    public  Pump() {
        battery = new Battery();
        reservoir = Util.RESERVOIR_CAPACITY;
    }

    /**
     * Checks the status of the pump
     * @return true if the pump is working properly, else if a mechanical problem has been detected.
     */
    public boolean checkStatus() {
        return !Util.DEBUG_PUMP_CHECK && reservoir >= MINIMUM_RESERVOIR_LEVEL && battery.getChargeLevel() > 0;
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

    /**
     * Refills the reservoir
     */
    public void refill(){
        reservoir = Util.RESERVOIR_CAPACITY;
    }
}