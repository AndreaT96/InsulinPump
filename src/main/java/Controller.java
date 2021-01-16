import lombok.*;
public class Controller {

    private Pump pump;
    private Sensor sensor;
    private Display display_one;
    private Display display_two;
    private Connection conn;

    public Controller(){

        conn = DriverManager.getConnection("jdbc:sqlite:InsulinPump.db");


    }

}

