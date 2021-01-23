package demo;

/**
 * This mockup class represents a basic battery powered blood sugar sensor.
 */
public class Sensor {
    
    private int sugar_level = 130;
    private Battery battery = new Battery();

    /**
     * This methods return the blood-sugar level.
     * The charge of battery is calculated by demo.Battery() class.
     */
    public int getSugar_level(){
        if(sugar_level > 180) {
            sugar_level += (int) (Math.random() * 10) - 10;
        } if(sugar_level < 50) {
            sugar_level += (int) (Math.random() * 20);
        } else {
            sugar_level += (int) (Math.random() * 20) - 9;
        }
        battery.decreaseLevel(1);
        return sugar_level;
    }

    /**
     * This methods return the battery level of sensor.
     * The charge of battery is calculated by demo.Battery() class.
     */
    public double getBattery(){
        if (battery.getBatteryPercentage() == 0){
            battery = new Battery();
        }
        return battery.getBatteryPercentage();
    }

    public boolean checkStatus() {
        return battery.getChargeLevel() > 0;
    }
}