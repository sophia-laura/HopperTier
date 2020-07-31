package xyz.sophialaura.hopper.model;

import org.bukkit.Material;

public class SellItem {

    private final Material material;
    private final double sellPrice;

    public SellItem(Material material, double sellPrice) {
        this.material = material;
        this.sellPrice = sellPrice;
    }

    public Material getMaterial() {
        return material;
    }

    public double getSellPrice() {
        return sellPrice;
    }
}
