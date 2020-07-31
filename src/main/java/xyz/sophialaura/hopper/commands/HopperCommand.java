package xyz.sophialaura.hopper.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.sophialaura.hopper.HopperTier;
import xyz.sophialaura.hopper.utils.ItemBuilder;

public class HopperCommand implements CommandExecutor {

    private final HopperTier hopperTier;

    public HopperCommand(HopperTier hopperTier) {
        this.hopperTier = hopperTier;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player && sender.hasPermission("ehopper.admin")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                hopperTier.getHopperSettings().loadConfigurations();
                sender.sendMessage("§aSuccessfully reload");
            } else if (args.length == 1 && args[0].equalsIgnoreCase("stats")) {
                sender.sendMessage("§aThere are a total of §f" + hopperTier.getHopperService().findAll().size() + " " +
                        "§aactive hoppers.");
            } else if (args.length == 4) {
                if (args[0].equalsIgnoreCase("give")) {
                    try {
                        int tier = Integer.parseInt(args[1]);
                        int amount = Integer.parseInt(args[2]);
                        String target = args[3];

                        if (tier < 1 || tier > 3) {
                            sender.sendMessage("§cThe tier must be a number between 1 and 3");
                            return false;
                        }

                        Player targetPlayer = Bukkit.getPlayer(target);

                        if (targetPlayer == null) {
                            sender.sendMessage("§cPlayer must be online.");
                            return false;
                        }

                        targetPlayer.getInventory().addItem(ItemBuilder.create(Material.HOPPER).name("§aHopper").lore(
                                "§fTier: §7" + tier
                        ).amount(amount).build());
                        sender.sendMessage("§aItems sent successfully.");
                    } catch (NumberFormatException e) {
                        sender.sendMessage("§cTier and amount must be numbers.");
                    }
                }
            } else {
                sender.sendMessage("§aHopper commands:");
                sender.sendMessage("§7 - /ehopper give <tier> <amount> <player> ");
                sender.sendMessage("§7 - /ehopper reload");
                sender.sendMessage("§7 - /ehopper stats");
            }
        }
        return false;
    }
}
