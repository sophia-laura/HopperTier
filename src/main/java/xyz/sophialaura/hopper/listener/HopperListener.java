package xyz.sophialaura.hopper.listener;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.sophialaura.hopper.HopperTier;
import xyz.sophialaura.hopper.menu.HopperMenu;
import xyz.sophialaura.hopper.model.Hopper;
import xyz.sophialaura.hopper.model.SellItem;
import xyz.sophialaura.hopper.utils.ItemBuilder;

import java.util.List;

public class HopperListener implements Listener {

    private final HopperTier hopperTier;

    public HopperListener(HopperTier hopperTier) {
        this.hopperTier = hopperTier;
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        final Player player = event.getPlayer();

        if (event.getBlockPlaced().getType() == Material.HOPPER) {
            final Location location = event.getBlockPlaced().getLocation();
            final Chunk chunk = location.getChunk();
            int x = chunk.getX() << 4;
            int z = chunk.getZ() << 4;

            World world = chunk.getWorld();

            boolean check = false;

            for (int xx = x; xx < x + 16; xx++) {
                for (int zz = z; zz < z + 16; zz++) {
                    for (int yy = 0; yy < 256; yy++) {
                        Block block = world.getBlockAt(xx, yy, zz);

                        if (block.getType() == Material.HOPPER && !block.getLocation().equals(location)) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', hopperTier.getHopperSettings().getAlreadyHasHopper()));
                            check = true;
                            break;
                        }
                    }
                }
            }

            if (!check) {

                final ItemStack itemInHand = event.getItemInHand();
                Hopper hopper;

                if (itemInHand.hasItemMeta() && itemInHand.getItemMeta().hasLore()) {
                    final List<String> lore = itemInHand.getItemMeta().getLore();
                    if (lore.get(0).startsWith("§fTier: §7")) {
                        int tier = Integer.parseInt(lore.get(0).replace("§fTier: §7", ""));
                        hopper = new Hopper(player.getUniqueId(), location, tier);
                        hopperTier.getHopperDao().createOrUpdate(hopper);
                        hopperTier.getHopperService().add(hopper);
                        return;
                    }
                }

                hopper = new Hopper(player.getUniqueId(), location, 1);
                hopperTier.getHopperDao().createOrUpdate(hopper);
                hopperTier.getHopperService().add(hopper);

            } else {
                event.setBuild(false);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlock();
        if (!event.isCancelled() && block.getType() == Material.HOPPER) {
            org.bukkit.block.Hopper hopperBlock = (org.bukkit.block.Hopper) event.getBlock().getState();
            final Hopper hopper = hopperTier.getHopperService().findByLocation(block.getLocation());

            if (hopper != null) {
                event.setCancelled(true);

                final Location location = event.getBlock().getLocation();
                final World world = location.getWorld();
                for (ItemStack content : hopperBlock.getInventory().getContents()) {
                    if (content != null) {
                        world.dropItemNaturally(location, content);
                    }
                }

                event.getBlock().setType(Material.AIR);
                world.dropItemNaturally(location, ItemBuilder.create(Material.HOPPER).name("§aHopper").lore(
                        "§fTier: §7" + hopper.getTier()
                ).build());

                hopperTier.getHopperDao().delete(hopper);
                hopperTier.getHopperService().remove(hopper);
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {

                final Location location = event.getBlock().getLocation();
                final Chunk chunk = location.getChunk();
                int x = chunk.getX() << 4;
                int z = chunk.getZ() << 4;

                World world = chunk.getWorld();

                org.bukkit.block.Hopper hopperBlock = null;
                Hopper hopper = null;

                for (int xx = x; xx < x + 16; xx++) {
                    for (int zz = z; zz < z + 16; zz++) {
                        for (int yy = 0; yy < 256; yy++) {
                            Block block = world.getBlockAt(xx, yy, zz);

                            if (block.getType() == Material.HOPPER) {
                                hopperBlock = (org.bukkit.block.Hopper) block.getState();
                                hopper = hopperTier.getHopperService().findByLocation(block.getLocation());
                                break;
                            }
                        }
                    }
                }

                if (hopper != null) {
                    for (Entity entity : event.getBlock().getLocation().getChunk().getEntities()) {
                        if (entity instanceof Item) {
                            Item item = (Item) entity;

                            final SellItem sellItem = hopperTier.getSellItemService().findByMaterial(item.getItemStack()
                                    .getType()).orElse(null);
                            if (sellItem != null) {
                                if (hopper.getTier() == 2) {
                                    final Inventory inventory = hopperBlock.getInventory();
                                    inventory.addItem(item.getItemStack());
                                    item.remove();
                                } else if (hopper.getTier() == 3) {
                                    double price = sellItem.getSellPrice() * item.getItemStack().getAmount();
                                    hopperTier.getEconomy().bankDeposit(Bukkit.getOfflinePlayer(hopper.getOwner()).getName(), price);
                                    item.remove();
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskLater(hopperTier, 5L);
    }

    @EventHandler
    public void onBlockExplodeEvent(BlockExplodeEvent event) {
        event.blockList().forEach(block -> {
            if (!event.isCancelled() && block.getType() == Material.HOPPER) {
                org.bukkit.block.Hopper hopperBlock = (org.bukkit.block.Hopper) event.getBlock().getState();
                final Hopper hopper = hopperTier.getHopperService().findByLocation(block.getLocation());

                if (hopper != null) {
                    final Location location = event.getBlock().getLocation();
                    final World world = location.getWorld();
                    hopperBlock.getInventory().forEach(stack -> {
                        world.dropItemNaturally(location, stack);
                    });


                    event.getBlock().setType(Material.AIR);
                    world.dropItemNaturally(location, ItemBuilder.create(Material.HOPPER).name("§aHopper").lore(
                            "§fTier: §7" + hopper.getTier()
                    ).build());

                    hopperTier.getHopperDao().delete(hopper);
                    hopperTier.getHopperService().remove(hopper);
                }
            }
        });
    }

    @EventHandler
    public void onBlockGrowEvent(BlockGrowEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {

                final Location location = event.getBlock().getLocation();
                final Chunk chunk = location.getChunk();
                int x = chunk.getX() << 4;
                int z = chunk.getZ() << 4;

                World world = chunk.getWorld();

                org.bukkit.block.Hopper hopperBlock = null;
                Hopper hopper = null;

                for (int xx = x; xx < x + 16; xx++) {
                    for (int zz = z; zz < z + 16; zz++) {
                        for (int yy = 0; yy < 256; yy++) {
                            Block block = world.getBlockAt(xx, yy, zz);

                            if (block.getType() == Material.HOPPER) {
                                hopperBlock = (org.bukkit.block.Hopper) block.getState();
                                hopper = hopperTier.getHopperService().findByLocation(block.getLocation());
                                break;
                            }
                        }
                    }
                }

                if (hopper != null) {
                    for (Entity entity : event.getBlock().getLocation().getChunk().getEntities()) {
                        if (entity instanceof Item) {
                            Item item = (Item) entity;

                            final SellItem sellItem = hopperTier.getSellItemService().findByMaterial(item.getItemStack()
                                    .getType()).orElse(null);
                            if (sellItem != null) {
                                if (hopper.getTier() == 2) {
                                    final Inventory inventory = hopperBlock.getInventory();
                                    inventory.addItem(item.getItemStack());
                                    item.remove();
                                } else if (hopper.getTier() == 3) {
                                    double price = sellItem.getSellPrice() * item.getItemStack().getAmount();
                                    hopperTier.getEconomy().bankDeposit(Bukkit.getOfflinePlayer(hopper.getOwner()).getName(), price);
                                    item.remove();
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskLater(hopperTier, 5L);
    }

    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {

                final Location location = event.getItemDrop().getLocation();
                final Chunk chunk = location.getChunk();
                int x = chunk.getX() << 4;
                int z = chunk.getZ() << 4;

                World world = chunk.getWorld();

                org.bukkit.block.Hopper hopperBlock = null;
                Hopper hopper = null;

                for (int xx = x; xx < x + 16; xx++) {
                    for (int zz = z; zz < z + 16; zz++) {
                        for (int yy = 0; yy < 256; yy++) {
                            Block block = world.getBlockAt(xx, yy, zz);

                            if (block.getType() == Material.HOPPER) {
                                hopperBlock = (org.bukkit.block.Hopper) block.getState();
                                hopper = hopperTier.getHopperService().findByLocation(block.getLocation());
                                break;
                            }
                        }
                    }
                }

                if (hopper != null) {
                    for (Entity entity : event.getItemDrop().getLocation().getChunk().getEntities()) {
                        if (entity instanceof Item) {
                            Item item = (Item) entity;
                            final SellItem sellItem = hopperTier.getSellItemService().findByMaterial(item.getItemStack()
                                    .getType()).orElse(null);
                            if (sellItem != null) {
                                if (hopper.getTier() == 2) {

                                    final Inventory inventory = hopperBlock.getInventory();
                                    inventory.addItem(item.getItemStack());
                                    item.remove();
                                } else if (hopper.getTier() == 3) {
                                    double price = sellItem.getSellPrice() * item.getItemStack().getAmount();
                                    hopperTier.getEconomy().bankDeposit(Bukkit.getOfflinePlayer(hopper.getOwner()).getName(), price);
                                    item.remove();
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskLater(hopperTier, 5L);
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getAction().name().contains("RIGHT")) {
            final Material type = event.getClickedBlock().getType();
            if (type == Material.HOPPER) {
                final Hopper hopper = hopperTier.getHopperService().findByLocation(event.getClickedBlock().getLocation());
                if (hopper != null) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            HopperMenu.openInventory(event.getPlayer(), hopper);
                        }
                    }.runTaskLater(hopperTier, 10L);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryOpenEvent(InventoryOpenEvent event) {
        if (event.getInventory().getType() == InventoryType.HOPPER) {
            event.setCancelled(true);
        }
    }

    public HopperTier getHopperTier() {
        return hopperTier;
    }
}