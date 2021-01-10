public class Sensor {
    
    private int sugar_level = 130;
    private Battery battery = new Battery();

    /**
     *  Metodo che restituisce il livello di zucchero nel sangue.
     *  La durata della batteria è data dalla classe Battery().
     */
    public int getSugar_level(){
        sugar_level = sugar_level + ( (int) (Math.random() * 10) - 5 );
        battery.decreaseLevel(1);
        return sugar_level;
    }

    /**
     *  Metodo che restituisce il livello di batteria del sensore.
     *  La durata della batteria è data dalla classe Battery().
     */
    public double getBattery(){
        if (battery.getBatteryPercentage() == 0){
            battery = new Battery();
        }
        return battery.getBatteryPercentage();
    }

}