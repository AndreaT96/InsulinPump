package com.insulinpump.component;

import lombok.Getter;
import lombok.Synchronized;

/**
 * This class mocks the sound buzzer component.
 */
public class Buzzer {
    @Getter(onMethod_ = {@Synchronized})
    private boolean started = false;

    /**
     * Starts the sound buzzer (stub)
     */
    @Synchronized
    public void start() {
        started = true;
    }

    /**
     * Stops the sound buzzer (stub)
     */
    @Synchronized
    public void stop() {
        started = false;
    }

}
