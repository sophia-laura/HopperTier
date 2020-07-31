package xyz.sophialaura.hopper.menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.sophialaura.hopper.HopperTier;
import xyz.sophialaura.hopper.menu.api.Menu;
import xyz.sophialaura.hopper.model.Hopper;
import xyz.sophialaura.hopper.utils.ItemBuilder;

import java.text.DecimalFormat;

public class HopperMenu {

    public static void openInventory(Player player, Hopper hopper) {
        final HopperTier instance = HopperTier.getInstance();
        Menu menu = new Menu(instance.getHopperSettings().getTitle(), 3);

        ItemStack glass = ItemBuilder.create(Material.STAINED_GLASS_PANE).name("§a ").durability(instance.getHopperSettings()
                .getGlassPanelData()).build();

        for (int i = 0; i < 27; i++) {
            menu.setItem(i, glass);
        }

        menu.setItem(11, ItemBuilder.create(Material.HOPPER).name("§aHopper - Tier 1").lore(lore -> {
            lore.add("");
            lore.add("§7Price: §f$ " + formatPrice(instance.getHopperSettings().getTierOnePrice()));
            lore.add("");
            if (hopper.getTier() == 1) {
                lore.add(ChatColor.translateAlternateColorCodes('&', instance.getHopperSettings().getClickToUpgrade()));
            } else if(hopper.getTier() == 2) {
                lore.add(ChatColor.translateAlternateColorCodes('&', instance.getHopperSettings().getCurrentLevel()));
            } else {
                lore.add(ChatColor.translateAlternateColorCodes('&', instance.getHopperSettings().getAlreadyUpdated()));
            }
        }).build(), (clickType, stack, slot) -> {
            if (hopper.getTier() == 1) {
                if(instance.getEconomy().has(player.getName(), instance.getHopperSettings().getTierOnePrice())) {
                    instance.getEconomy().withdrawPlayer(player.getName(), instance.getHopperSettings().getTierOnePrice());
                    hopper.setTier(2);
                    instance.getHopperDao().createOrUpdate(hopper);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', instance.getHopperSettings().getSuccessfullyEvolved()));
                }
            }
            player.closeInventory();
        });

        menu.setItem(15, ItemBuilder.create(Material.HOPPER).name("§aHopper - Tier 2").lore(lore -> {
            lore.add("");
            lore.add("§7Price: §f$ " + formatPrice(instance.getHopperSettings().getTierTwoPrice()));
            lore.add("");
            if (hopper.getTier() == 1) {
                lore.add(ChatColor.translateAlternateColorCodes('&', instance.getHopperSettings().getNeedUpdateOtherTier()));
            } else if(hopper.getTier() == 2) {
                lore.add(ChatColor.translateAlternateColorCodes('&', instance.getHopperSettings().getClickToUpgrade()));
            } else if(hopper.getTier() == 3) {
                lore.add(ChatColor.translateAlternateColorCodes('&', instance.getHopperSettings().getCurrentLevel()));
            }
        }).build(), (clickType, stack, slot) -> {
            if (hopper.getTier() == 2) {
                if(instance.getEconomy().has(player.getName(), instance.getHopperSettings().getTierTwoPrice())) {
                    instance.getEconomy().withdrawPlayer(player.getName(), instance.getHopperSettings().getTierTwoPrice());
                    hopper.setTier(3);
                    instance.getHopperDao().createOrUpdate(hopper);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', instance.getHopperSettings().getSuccessfullyEvolved()));
                }
            }
            player.closeInventory();
        });

        menu.open(player);
    }

    private static String formatPrice(double value) {
        return new DecimalFormat("#.00").format(value);
    }

}
