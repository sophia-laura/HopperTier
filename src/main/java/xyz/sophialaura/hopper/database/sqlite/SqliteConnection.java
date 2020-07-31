package xyz.sophialaura.hopper.database.sqlite;

import xyz.sophialaura.hopper.database.DatabaseConnection;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SqliteConnection implements DatabaseConnection {

    private static final ExecutorService POOL = Executors.newFixedThreadPool(2);

    private final File file;
    private Connection connection;

    public SqliteConnection(File file) {
        this.file = file;
    }

    @Override
    public ExecutorService getExecutor() {
        return POOL;
    }

    @Override
    public Connection getConnection() {
        if (!isConnected()) {
            setupConnection();
        }
        return connection;
    }

    @Override
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException ignored) {
        }
        return false;
    }

    @Override
    public void setupConnection() {
        try {
            Class.forName("org.sqlite.JDBC").newInstance();
            String url =
                    "jdbc:"
                            + "sqlite"
                            + ":"
                            + file;
            this.connection = DriverManager.getConnection(url);
        } catch (ClassNotFoundException | SQLException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }
}
