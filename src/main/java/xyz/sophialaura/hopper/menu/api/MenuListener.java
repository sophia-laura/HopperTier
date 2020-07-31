package xyz.sophialaura.hopper.menu.api;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;

public class MenuListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClickListener(InventoryClickEvent event) {
        event.getInventory();
        Player p = (Player) event.getWhoClicked();
        Inventory inv = p.getOpenInventory().getTopInventory();
        if (inv.getType() != InventoryType.CHEST)
            return;
        if (inv.getHolder() == null)
            return;
        if (!(inv.getHolder() instanceof Holder))
            return;
        if (event.getClickedInventory() == null)
            return;
        if (event.getClickedInventory().equals(p.getOpenInventory().getBottomInventory())) {
            event.setCancelled(true);
            return;
        }
        if (event.getClickedInventory() != inv)
            return;
        event.setCancelled(true);
        if (event.getSlot() < 0)
            return;
        Holder holder = (Holder) inv.getHolder();
        Menu menu = holder.getMenu();
        if (menu.hasItem(event.getSlot())) {
            ClickItem item = menu.getItem(event.getSlot());

            xyz.sophialaura.hopper.menu.api.ClickType clickType;
            if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                clickType = xyz.sophialaura.hopper.menu.api.ClickType.SHIFT_CLICK;
            } else if (event.getAction() == InventoryAction.PICKUP_HALF) {
                clickType = xyz.sophialaura.hopper.menu.api.ClickType.RIGHT;
            } else {
                clickType = ClickType.LEFT;
            }

            item.getClickHandler().onClick(clickType, event.getCurrentItem(), event.getSlot());
        }
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryPutItemListener(InventoryDragEvent event) {
        event.getInventory();
        Inventory inv = event.getInventory();
        if (inv.getType() != InventoryType.CHEST)
            return;
        if (inv.getHolder() == null)
            return;
        if (!(inv.getHolder() instanceof Holder))
            return;
        if (!(event.getWhoClicked() instanceof Player))
            return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryCloseInventory(InventoryCloseEvent event) {
        if (event.getInventory() == null) {
            return;
        }
        Inventory inv = event.getInventory();
        if (inv.getType() != InventoryType.CHEST)
            return;
        if (inv.getHolder() == null)
            return;
        if (!(inv.getHolder() instanceof Holder))
            return;
        Holder holder = (Holder) inv.getHolder();
        Menu menu = holder.getMenu();
        menu.getCloseHandler().onClose();
    }
}
