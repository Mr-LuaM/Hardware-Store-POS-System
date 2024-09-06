package util;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggerUtils {

    private static final String LOG_FILE = "application.log";

    // Method to log a message with a timestamp
    public static void log(String message) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true)) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            fw.write(dtf.format(now) + " - " + message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
