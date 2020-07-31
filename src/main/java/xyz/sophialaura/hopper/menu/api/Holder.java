package xyz.sophialaura.hopper.menu.api;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class Holder implements InventoryHolder {

    private Menu menu;

    public Holder(Menu menu) {
        this.menu = menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public Menu getMenu() {
        return menu;
    }

    @Override
    public Inventory getInventory() {
        return menu.getInventory();
    }
}
