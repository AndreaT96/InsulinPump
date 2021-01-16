import lombok.*;

import java.sql.*;

public class Controller {

    @Getter private Pump pump;
    @Getter private Sensor sensor;
    @Getter private Display display_one;
    @Getter private Display display_two;
    @Getter private Connection conn;


    public Controller(){

        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:InsulinPump.db");
            Statement state = conn.createStatement();
            ResultSet rs = state.executeQuery("SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'misure'");

            if(!rs.next()){
                state.executeUpdate("CREATE TABLE misure(id INTEGER PRIMARY KEY AUTOINCREMENT, data TIMESTAMP default CURRENT_TIMESTAMP, lettura INTEGER);");
            }
            state.executeUpdate("INSERT INTO misure(lettura) VALUES ('100');");
            rs = state.executeQuery("SELECT count(*) AS prova FROM misure");
            while (rs.next()) {
                System.out.println(rs.getInt("prova"));
            }

        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }


    }

}

