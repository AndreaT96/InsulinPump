public class Sensor {
    
    private int sugar_level = 130;
    private Battery battery = new Battery();

    /**
     * This methods return the blood-sugar level.
     * The charge of battery is calculated by Battery() class.
     */
    public int getSugar_level(){
        sugar_level = sugar_level + ( (int) (Math.random() * 10) - 5 );
        battery.decreaseLevel(1);
        return sugar_level;
    }

    /**
     * This methods return the battery level of sensor.
     * The charge of battery is calculated by Battery() class.
     */
    public double getBattery(){
        if (battery.getBatteryPercentage() == 0){
            battery = new Battery();
        }
        return battery.getBatteryPercentage();
    }

}