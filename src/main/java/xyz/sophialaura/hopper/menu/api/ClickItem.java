package xyz.sophialaura.hopper.menu.api;

import org.bukkit.inventory.ItemStack;

public class ClickItem {

    private final ItemStack stack;
    private final Menu.ClickHandler clickHandler;

    public ClickItem(ItemStack stack, Menu.ClickHandler clickHandler) {
        this.stack = stack;
        this.clickHandler = clickHandler;
    }

    public ClickItem(ItemStack stack) {
        this.stack = stack;
        clickHandler = (clickType, stack1, slot) -> {

        };
    }

    public ItemStack getStack() {
        return stack;
    }

    public Menu.ClickHandler getClickHandler() {
        return clickHandler;
    }
}