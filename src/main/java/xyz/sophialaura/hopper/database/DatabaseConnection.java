package xyz.sophialaura.hopper.database;

import java.sql.Connection;
import java.util.concurrent.ExecutorService;

public interface DatabaseConnection {

    ExecutorService getExecutor();

    Connection getConnection();

    boolean isConnected();

    void setupConnection();

    default void execute(Runnable runnable) {
        getExecutor().execute(runnable);
    }

    default void shutdown() {
        getExecutor().shutdown();
    }

}
