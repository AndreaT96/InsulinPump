package com.insulinpump;

/**
 * Static class with configurable fields and utility methods.
 */
public class Util {

    public static final int CONTROLLER_BOOT_FRESHNESS_MINUTES = 45;
    public static final int MINIMUM_DOSE = 1;
    public static final int SAFE_ZONE_MIN = 3*18;
    public static final int SAFE_ZONE_MAX = 7*18;
    public static final int DANGEROUS_ZONE = 8*18;
    public static final int RESERVOIR_CAPACITY = 300;
    public static int SPEED = 100;

    public static final double PERCENTAGE_SIMULATE_ERROR = 0.001;

    public static boolean DEBUG_PUMP_CHECK = false;
    public static boolean DEBUG_SENSOR_CHECK = false;
    public static boolean DEBUG_FORCE_NETWORK_ERROR = false;
    public static boolean DEBUG_FORCE_PUMP_ERROR = false;

}
