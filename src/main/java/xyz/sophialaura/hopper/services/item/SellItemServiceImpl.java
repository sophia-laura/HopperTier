package xyz.sophialaura.hopper.services.item;

import com.google.common.collect.ImmutableList;
import org.bukkit.Material;
import xyz.sophialaura.hopper.model.SellItem;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class SellItemServiceImpl implements SellItemService {

    private final Set<SellItem> items;

    public SellItemServiceImpl() {
        items = new HashSet<>();
    }

    @Override
    public ImmutableList<SellItem> findAll() {
        return ImmutableList.copyOf(items);
    }

    @Override
    public Optional<SellItem> findByMaterial(Material material) {
        return items.stream().filter(item -> item.getMaterial().equals(material)).findAny();
    }

    @Override
    public void add(SellItem item) {
        Objects.requireNonNull(item, "item can't be null.");
        this.items.add(item);
    }

    @Override
    public void remove(SellItem item) {
        Objects.requireNonNull(item, "item can't be null.");
        this.items.remove(item);
    }
}
