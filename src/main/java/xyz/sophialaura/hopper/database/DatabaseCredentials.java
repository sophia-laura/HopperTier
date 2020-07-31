package xyz.sophialaura.hopper.database;

import java.util.function.Consumer;

public class DatabaseCredentials {

    private boolean fileSave;

    private String hostname;
    private String username;
    private String password;
    private String database;

    private int port;

    public DatabaseCredentials() {}

    public boolean isFileSave() {
        return fileSave;
    }

    public String getHostname() {
        return hostname;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabase() {
        return database;
    }

    public int getPort() {
        return port;
    }

    public DatabaseCredentials with(Consumer<DatabaseCredentials> consumer) {
        consumer.accept(this);
        return this;
    }

}
