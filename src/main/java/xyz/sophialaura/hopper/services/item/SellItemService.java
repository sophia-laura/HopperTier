package xyz.sophialaura.hopper.services.item;

import com.google.common.collect.ImmutableList;
import org.bukkit.Material;
import xyz.sophialaura.hopper.model.SellItem;

import java.util.Optional;

public interface SellItemService {

    ImmutableList<SellItem> findAll();

    Optional<SellItem> findByMaterial(Material material);

    void add(SellItem item);

    void remove(SellItem item);

}
