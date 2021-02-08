package com.insulinpump.component;

import lombok.*;
public class Battery {
    @Getter
    @Setter
    private int chargeLevel;
    @Getter
    private int originalCapacity;

    public Battery() {
        this.chargeLevel = 60*24*7; //7 days
        this.originalCapacity = chargeLevel;
    }

    /**
     * Constructs a com.insulinpump.component.Battery object with a customized capacity. The battery is FULLY charged.
     * @param capacity this parameter is just a mockup. The bigger the number the longer it lasts
     */
    public Battery(int capacity) {
        this();
        this.chargeLevel = capacity;
        this.originalCapacity = chargeLevel;
    }

    /**
     * Decreases the desired level of charge level from the battery.
     * @param amount to decrease. It does NOT represent the PERCENTAGE to decrease.
     * @return the updated charge level
     */
    public int decreaseLevel(int amount) {
        chargeLevel -= amount;
        if (chargeLevel < 0) chargeLevel = 0;
        return chargeLevel;
    }

    /**
     * The amount of charge left in the battery expressed as a %
     * @return a value between 0 and 100
     */
    public int getBatteryPercentage() {
        return chargeLevel*100/originalCapacity;
    }

    /**
     * Fully recharges the battery.
     */
    public void recharge() {
        chargeLevel = originalCapacity;
    }

}
