package xyz.sophialaura.hopper.services.hopper;

import com.google.common.collect.ImmutableList;
import org.bukkit.Location;
import xyz.sophialaura.hopper.model.Hopper;

public interface HopperService {

    ImmutableList<Hopper> findAll();

    Hopper findByLocation(Location location);

    void add(Hopper hopper);

    void remove(Hopper hopper);

}
