package pixyel_backend.database;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Yannick
 */
public class DatabaseCleanUpService implements Runnable {

    /**
     * Deletes all all users from the database that are registerd for at least 3
     * days but didn't finished the account validation
     */
    public void CleanUnregistratedUsers() {
        System.out.println("Cleaning usertable");
        try {
            Connection conn = MysqlConnector.connectToDatabaseUsingPropertiesFile();
            Statement sta = conn.createStatement();
            Instant instant = Instant.now().minus(3, ChronoUnit.DAYS);
            Timestamp currentTimestamp = Timestamp.from(instant);
            sta.executeLargeUpdate("DELETE FROM Users WHERE status = 0 AND reg_date < '" + currentTimestamp + "'");
        } catch (Exception ex) {
            System.out.println("Could not clean up unregistered users - root cause: " + ex);
        }
    }

    @Override
    public void run() {
        Timer timer = new Timer();
        TimerTask dalyTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Starting DatabaseCleanupService");
                CleanUnregistratedUsers();
            }
        };
        timer.schedule(dalyTask, 0l, 86400000);
    }
}
