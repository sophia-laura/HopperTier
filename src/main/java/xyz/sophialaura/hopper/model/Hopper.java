package xyz.sophialaura.hopper.model;

import org.bukkit.Location;

import java.util.UUID;

public class Hopper {

    private final UUID owner;
    private final Location location;
    private int tier;

    public Hopper(UUID owner, Location location, int tier) {
        this.owner = owner;
        this.location = location;
        this.tier = tier;
    }

    public UUID getOwner() {
        return owner;
    }

    public Location getLocation() {
        return location;
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }
}
