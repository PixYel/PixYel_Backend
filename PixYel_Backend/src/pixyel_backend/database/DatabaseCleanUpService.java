package pixyel_backend.database;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Timer;
import java.util.TimerTask;
import pixyel_backend.Log;

/**
 * @author Yannick
 */
public class DatabaseCleanUpService implements Runnable {

    /**
     * Deletes all all users from the database that are registerd for at least 3
     * days but didn't finished the account validation
     */
    public void CleanUnregistratedUsers() {
        Log.logInfo("Cleaning usertable", DatabaseCleanUpService.class);
        try {
            try (Statement sta = MysqlConnector.getConnection().createStatement()) {
                Instant instant = Instant.now().minus(3, ChronoUnit.DAYS);
                Timestamp currentTimestamp = Timestamp.from(instant);
                sta.executeLargeUpdate("DELETE FROM Users WHERE status = 0 AND reg_date < '" + currentTimestamp + "'");
            }
        } catch (SQLException ex) {
            Log.logWarning("Could not clean up unregistered users - root cause: " + ex, DatabaseCleanUpService.class);
        }
    }

    public static void start() {
        DatabaseCleanUpService dbService = new DatabaseCleanUpService();
        dbService.run();
    }

    @Override
    public void run() {
        Timer timer = new Timer();
        TimerTask dalyTask = new TimerTask() {
            @Override
            public void run() {
                Log.logInfo("Starting DatabaseCleanupService", DatabaseCleanUpService.class);
                CleanUnregistratedUsers();
            }
        };
        timer.schedule(dalyTask, 0l, 86400000);//3 days
    }
}
