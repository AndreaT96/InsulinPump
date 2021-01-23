package demo;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class DBManager {
    //@Getter private final Connection conn;

    @Getter
    private MisuraRepository misuraRepository;
    private IniezioneRepository iniezioneRepository;

    public DBManager(MisuraRepository misuraRepository, IniezioneRepository iniezioneRepository) throws SQLException {
        this.misuraRepository = misuraRepository;
        this.iniezioneRepository = iniezioneRepository;
        /*
         * Create database if not exists
         */
        /*
        conn = DriverManager.getConnection("jdbc:sqlite:InsulinPump.db");
        if(isDBEmpty()) {
            sqlIteCreateTables();
        }

         */
    }

    /**
     * Inserts a sugar reading into the "misura" table
     * @param level of sugar measured by the sensor
     * @param date of the reading
     * @return the id of the record
     * @throws IllegalArgumentException
     * @throws NullPointerException
     */
    public Long sqlIteInsertReading(int level, @NonNull LocalDateTime date) throws IllegalArgumentException, NullPointerException {
        return misuraRepository.save(new Misura(date, level)).getId();
    }

    /**
     * Inserts the amount of insulin injected in a certain date into the "iniezioni" table
     * @param amount of insulin units injected.
     * @param date of the injection
     * @return the id of the record
     * @throws IllegalArgumentException
     * @throws NullPointerException
     */
    public Long sqlIteInsertInjection(int amount, @NonNull LocalDateTime date) throws IllegalArgumentException, NullPointerException {
        return iniezioneRepository.save(new Iniezione(date, amount)).getId();
    }
/*
    private void sqlIteCreateTables() throws SQLException {
        Statement state = conn.createStatement();
        state.executeUpdate("CREATE TABLE misura(id INTEGER PRIMARY KEY AUTOINCREMENT, date TIMESTAMP default CURRENT_TIMESTAMP, lettura INTEGER);");
        state.executeUpdate("CREATE TABLE iniezione(id INTEGER PRIMARY KEY AUTOINCREMENT, date TIMESTAMP default CURRENT_TIMESTAMP, unita INTEGER);");
    }

    public boolean isDBEmpty() throws SQLException {
        Statement state = conn.createStatement();
        ResultSet rs = state.executeQuery("SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'misura'");
        return !rs.next();
    }
*/
    /**
     * Returns the latest N sensor readings. If there are less rows than N then all readings will be returned.
     * @param n the non-negative amount of readings to return.
     * @return a ResultSet containing the camps [id, date, lettura]
     */
    public List<Misura> getLastNReadings(int n){
        if(n < 0) return null;
        return misuraRepository.findNByOrderByDateDesc(n);
    }
}
