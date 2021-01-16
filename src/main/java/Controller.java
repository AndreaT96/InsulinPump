import lombok.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Controller {

    @Getter private Pump pump;
    @Getter private Sensor sensor;
    @Getter private Display display_one;
    @Getter private Display display_two;
    @Getter private Connection conn;
    @Getter private Clock clock;

    private int[] last_measurements = new int[3];

    public Controller(){
        sensor = new Sensor();
        pump = new Pump();
        display_one = display_two = new Display();
        clock = new Clock();

        try {
            /*
             * Create database if not exists
             */
            conn = DriverManager.getConnection("jdbc:sqlite:InsulinPump.db");
            if(Util.isDBEmpty(conn)) {
                Util.sqlIteCreateTables(conn);
            }

            restoreLastMeasurements();
            /*
            for(int i : last_measurements) {
                System.out.println(i);
            }
            */

            /*
            state.executeUpdate("INSERT INTO misure(lettura) VALUES ('100');");
            rs = state.executeQuery("SELECT count(*) AS prova FROM misure");
            while (rs.next()) {
                System.out.println(rs.getInt("prova"));
            }
            */

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    /**
     * Calculates the insulin dose based on the last 3 readings.
     * @return the insulin dose.
     */
    public int computeInsulinDose() {
        if(last_measurements[2] <= last_measurements[1]) return 0;
        if(last_measurements[2] - last_measurements[1] < last_measurements[1] - last_measurements[0]) return 0;
        int compDose = (last_measurements[2] - last_measurements[1]) / 4;
        if(compDose == 0) compDose = Util.MINIMUM_DOSE;
        return compDose;
    }

    /**
     * Loads into "last_measurements" array the two most recent sugar level measurements from the Database, as long as they're not older than 45 minutes.
     * Otherwise it'll use a newly made reading from the sensor.
     * @throws SQLException whenever there's a problem with the local DB
     */
    private void restoreLastMeasurements() throws SQLException {
        //Do a measurement as soon as the controller boots up. this will be our R2
        last_measurements[2] = sensor.getSugar_level();

        //Fill preemptively the other 2 array cells with the same value. If suitable readings will be found in the DB, those ones will be used instead.
        last_measurements[1] = last_measurements[2];
        last_measurements[0] = last_measurements[2];

        //Read up to last 2 measurements from the DB. If they're fresh, they'll become our R1 and R0
        Statement state = conn.createStatement();
        ResultSet rs = state.executeQuery("SELECT data, lettura FROM misure ORDER BY data DESC LIMIT 2;");
        int i = 1;
        while(rs.next() && i >= 0) {
            LocalDateTime timestamp = Util.sqlIteParseDate(rs.getString("data"));
            if(timestamp != null) {
                long minutes = ChronoUnit.MINUTES.between(Clock.getTime(), timestamp);
                if(minutes < 0) minutes = - minutes;
                if (minutes <= Util.CONTROLLER_BOOT_FRESHNESS_MINUTES) {
                    last_measurements[i--] = rs.getInt("lettura");
                }
            }
        }
        //add latest reading (new) to DB
        Util.sqlIteInsertReading(conn, last_measurements[2]);
    }

    /**
     * Reads the sugar level from the sensors then stores it in the DB.
     * @return the result code of the DB insertion
     * @throws SQLException
     */
    private int readAndSaveReading() throws SQLException {
        last_measurements[0] = last_measurements[1];
        last_measurements[1] = last_measurements[2];
        last_measurements[2] = sensor.getSugar_level();
        return Util.sqlIteInsertReading(conn,last_measurements[2]);
    }

}

