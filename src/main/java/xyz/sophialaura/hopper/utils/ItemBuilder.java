package xyz.sophialaura.hopper.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class ItemBuilder {
    private ItemStack item;

    private ItemBuilder(ItemStack item) {
        this.item = item;
    }

    public static ItemBuilder create(Material material) {
        return new ItemBuilder(new ItemStack(material));
    }

    public ItemBuilder changeItem(Consumer<ItemStack> consumer) {
        consumer.accept(this.item);
        return this;
    }

    public ItemBuilder changeMeta(Consumer<ItemMeta> consumer) {
        ItemMeta itemMeta = this.item.getItemMeta();
        if (itemMeta != null) {
            consumer.accept(itemMeta);
            this.item.setItemMeta(itemMeta);
        }
        return this;
    }

    public ItemBuilder name(String displayName) {
        return this.changeMeta(itemMeta -> itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName)));
    }

    public ItemBuilder lore(String... lore) {
        return this.changeMeta(itemMeta -> itemMeta.setLore(Arrays.asList(lore)));
    }

    public ItemBuilder lore(Consumer<List<String>> consumer) {
        List<String> lore = new ArrayList();
        consumer.accept(lore);
        return this.changeMeta(itemMeta -> itemMeta.setLore(lore));
    }

    public ItemBuilder amount(int amount) {
        return this.changeItem(itemStack -> itemStack.setAmount(amount));
    }

    public ItemBuilder durability(int durability) {
        return this.changeItem(itemStack -> itemStack.setDurability((short) durability));
    }

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        return this.changeItem(itemStack -> itemStack.addEnchantment(enchantment, level));
    }

    public ItemBuilder removeEnchant(Enchantment enchantment) {
        return this.changeItem(itemStack -> itemStack.removeEnchantment(enchantment));
    }

    public ItemBuilder flags(ItemFlag... flags) {
        return this.changeMeta(itemMeta -> itemMeta.addItemFlags(flags));
    }

    public ItemBuilder removeFlags(ItemFlag... flags) {
        return this.changeMeta(itemMeta -> itemMeta.removeItemFlags(flags));
    }

    public ItemBuilder unbreakable(boolean unbreakable) {
        return changeMeta(itemMeta -> itemMeta.spigot().setUnbreakable(unbreakable));
    }

    public ItemStack build() {
        return this.item;
    }
}
