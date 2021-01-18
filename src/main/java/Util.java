import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class Util {

    public static final int CONTROLLER_BOOT_FRESHNESS_MINUTES = 45;
    public static final int MINIMUM_DOSE = 1;
    public static final int SAFE_ZONE_MIN = 3;
    public static final int SAFE_ZONE_MAX = 7;
    public static int SPEED = 100;

    private static DateTimeFormatter sqliteFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String dateToSqliteString(LocalDateTime date) {
        if(date == null) return  null;
        return sqliteFormatter.format(date);
    }

    public static LocalDateTime sqlIteParseDate(String string) {
        if(string == null) return  null;
        return LocalDateTime.from(sqliteFormatter.parse(string));
    }
}
