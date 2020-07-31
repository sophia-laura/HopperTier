package xyz.sophialaura.hopper.services.hopper;

import com.google.common.collect.ImmutableList;
import org.bukkit.Location;
import xyz.sophialaura.hopper.model.Hopper;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class HopperServiceImpl implements HopperService {

    private final Set<Hopper> hoppers;

    public HopperServiceImpl() {
        hoppers = new HashSet<>();
    }

    @Override
    public ImmutableList<Hopper> findAll() {
        return ImmutableList.copyOf(hoppers);
    }

    @Override
    public Hopper findByLocation(Location location) {
        return hoppers.stream().filter(hopper -> hopper.getLocation().getWorld().equals(location.getWorld()) && hopper
                .getLocation().getBlockX() == location.getBlockX() && hopper.getLocation().getBlockY() == location
                .getBlockY() && hopper.getLocation().getBlockZ() == location.getBlockZ()).findAny().orElse(null);
    }

    @Override
    public void add(Hopper hopper) {
        Objects.requireNonNull(hopper, "hopper can't be null.");
        this.hoppers.add(hopper);
    }

    @Override
    public void remove(Hopper hopper) {
        Objects.requireNonNull(hopper, "hopper can't be null.");
        this.hoppers.remove(hopper);
    }
}
