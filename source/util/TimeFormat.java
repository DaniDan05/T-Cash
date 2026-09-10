package source.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

final public class TimeFormat {
    private TimeFormat() {}
    
    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String currentTimeStamp() {
        return LocalDateTime.now().format(FORMATTER);
    }
}
