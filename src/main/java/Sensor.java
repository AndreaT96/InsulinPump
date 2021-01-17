/**
 * This mockup class represents a basic battery powered blood sugar sensor.
 */
public class Sensor {
    
    private int sugar_level = 130;
    private Battery battery = new Battery();

    /**
     * This methods return the blood-sugar level.
     * The charge of battery is calculated by Battery() class.
     */
    public int getSugar_level(){
        //TODO pseudo random per evitare che l'insulina scenda o salga troppo
        sugar_level = sugar_level + ( (int) (Math.random() * 20) - 10 );
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

    public boolean checkStatus() {
        return battery.getChargeLevel() > 0;
    }
}