package demo;

import lombok.*;
public class Battery {
    private boolean bCharging;
    @Getter @Setter int chargeLevel;
    @Getter private int originalCapacity;

    public Battery() {
        this.chargeLevel = 60*24*7; //7 days
        this.originalCapacity = chargeLevel;
    }

    /**
     * Constructs a demo.Battery object with a customized capacity. The battery is FULLY charged.
     * @param capacity this parameter is just a mockup. The bigger the number the longer it lasts
     */
    public Battery(int capacity) {
        this();
        this.chargeLevel = capacity;
        this.originalCapacity = chargeLevel;
    }

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

    public void rechargeBattery() {
        chargeLevel = originalCapacity;
    }

}
