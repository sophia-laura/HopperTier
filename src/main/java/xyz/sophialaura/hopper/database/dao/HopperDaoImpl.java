package xyz.sophialaura.hopper.database.dao;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import xyz.sophialaura.hopper.database.DatabaseConnection;
import xyz.sophialaura.hopper.model.Hopper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class HopperDaoImpl implements HopperDao {

    private final DatabaseConnection databaseConnection;

    public HopperDaoImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public ImmutableList<Hopper> findAll() {
        Set<Hopper> hoppers = new HashSet<>();

        try (PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("SELECT * FROM `hopper_tier`")) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                hoppers.add(new Hopper(UUID.fromString(rs.getString("owner")), convertStringToLocation(rs
                        .getString("location")), rs.getInt("tier")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return ImmutableList.copyOf(hoppers);
    }

    @Override
    public void createTables() {
        try (Statement statement = databaseConnection.getConnection().createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS `hopper_tier` (`location` VARCHAR(64) NOT NULL, `owner`" +
                    " VARCHAR(36) NOT NULL, `tier` INT(2) NOT NULL DEFAULT '0')");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void createOrUpdate(Hopper hopper) {
        databaseConnection.execute(() -> {
            try (PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("SELECT * FROM `hopper_tier`" +
                    " WHERE `location`=?")) {
                stmt.setString(1, convertLocationToString(hopper.getLocation()));

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement("UPDATE" +
                            " `hopper_tier` SET `tier`=? WHERE `location`=?")) {
                        preparedStatement.setInt(1, hopper.getTier());
                        preparedStatement.setString(2, convertLocationToString(hopper.getLocation()));
                        preparedStatement.executeUpdate();
                    }
                } else {
                    try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement("INSERT" +
                            " INTO `hopper_tier` (`location`, `owner`, `tier`) VALUES (?, ?, ?)")) {
                        preparedStatement.setString(1, convertLocationToString(hopper.getLocation()));
                        preparedStatement.setString(2, hopper.getOwner().toString());
                        preparedStatement.setInt(3, hopper.getTier());
                        preparedStatement.executeUpdate();
                    }
                }
                rs.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    @Override
    public void delete(Hopper hopper) {
        databaseConnection.execute(() -> {
            try (PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("DELETE FROM `hopper_tier` WHERE `location`=?")) {
                stmt.setString(1, convertLocationToString(hopper.getLocation()));
                stmt.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    private Location convertStringToLocation(String string) {
        String[] args = string.split(";");

        final int x = Integer.parseInt(args[1]);
        final int y = Integer.parseInt(args[2]);
        final int z = Integer.parseInt(args[3]);

        return new Location(Bukkit.getWorld(args[0]), x, y, z);
    }

    private String convertLocationToString(Location location) {
        return String.format("%s;%d;%d;%d", location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

}
