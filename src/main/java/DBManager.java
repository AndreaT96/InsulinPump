import lombok.*;

import java.sql.*;
import java.time.LocalDateTime;

public class DBManager {
    @Getter private final Connection conn;

    public DBManager() throws SQLException {
        /*
         * Create database if not exists
         */
        conn = DriverManager.getConnection("jdbc:sqlite:InsulinPump.db");
        if(isDBEmpty()) {
            sqlIteCreateTables();
        }
    }

    /**
     * Inserts a sugar reading into the "misure" table
     * @param level of sugar measured by the sensor
     * @param date of the reading
     * @return sql insertion status code
     * @throws SQLException whenever there's a problem with the local DB
     */
    public int sqlIteInsertReading(int level, @NonNull LocalDateTime date) throws SQLException {
        Statement state = conn.createStatement();
        return state.executeUpdate("INSERT INTO misure(data, lettura) VALUES ('" + Util.dateToSqliteString(date)+"', '" + level + "');");
    }

    /**
     * Inserts the amount of insulin injected in a certain date into the "iniezioni" table
     * @param amount of insulin units injected.
     * @param date of the injection
     * @return sql insertion status code
     * @throws SQLException whenever there's a problem with the local DB
     */
    public int sqlIteInsertInjection(int amount, @NonNull LocalDateTime date) throws SQLException {
        Statement state = conn.createStatement();
        return state.executeUpdate("INSERT INTO iniezioni(data, unita) VALUES ('" + Util.dateToSqliteString(date) + "', '" + amount + "');");
    }

    private void sqlIteCreateTables() throws SQLException {
        Statement state = conn.createStatement();
        state.executeUpdate("CREATE TABLE misure(id INTEGER PRIMARY KEY AUTOINCREMENT, data TIMESTAMP default CURRENT_TIMESTAMP, lettura INTEGER);");
        state.executeUpdate("CREATE TABLE iniezioni(id INTEGER PRIMARY KEY AUTOINCREMENT, data TIMESTAMP default CURRENT_TIMESTAMP, unita INTEGER);");
    }

    public boolean isDBEmpty() throws SQLException {
        Statement state = conn.createStatement();
        ResultSet rs = state.executeQuery("SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'misure'");
        return !rs.next();
    }

    /**
     * Returns the latest N sensor readings. If there are less rows than N then all readings will be returned.
     * @param n the non-negative amount of readings to return.
     * @return a ResultSet containing the camps [id, data, lettura]
     * @throws SQLException whenever there's a problem with the local DB
     */
    public ResultSet getLastNReadings(int n) throws SQLException{
        if(n < 0) return null;
        Statement state = conn.createStatement();
        return state.executeQuery("SELECT id, data, lettura FROM misure ORDER BY data DESC LIMIT " + n + ";");
    }
}
