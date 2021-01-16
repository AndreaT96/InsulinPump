import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class Util {

    public static final int CONTROLLER_BOOT_FRESHNESS_MINUTES = 45;
    public static final int MINIMUM_DOSE = 1;

    private static DateTimeFormatter sqliteFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String dateToSqliteString(LocalDateTime date) {
        if(date == null) return  null;
        return sqliteFormatter.format(date);
    }

    public static LocalDateTime sqlIteParseDate(String string) {
        if(string == null) return  null;
        return LocalDateTime.from(sqliteFormatter.parse(string));
    }

    public static int sqlIteCreateTables(Connection conn) throws SQLException {
        Statement state = conn.createStatement();
        return state.executeUpdate("CREATE TABLE misure(id INTEGER PRIMARY KEY AUTOINCREMENT, data TIMESTAMP default CURRENT_TIMESTAMP, lettura INTEGER);");
    }

    public static boolean isDBEmpty(Connection conn) throws SQLException {
        Statement state = conn.createStatement();
        ResultSet rs = state.executeQuery("SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'misure'");
        return !rs.next();
    }

    public static int sqlIteInsertReading(Connection conn, int level) throws SQLException {
        return sqlIteInsertReading(conn, level, Clock.getTime());
    }
    public static int sqlIteInsertReading(Connection conn, int level, LocalDateTime date) throws SQLException {
        Statement state = conn.createStatement();
        return state.executeUpdate("INSERT INTO misure(data, lettura) VALUES ('" + dateToSqliteString(date)+"', '"+ String.valueOf(level) + "');");
    }
}
