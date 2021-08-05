package com.fozevi.economyedem.util;

import com.fozevi.economyedem.EconomyEdem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.function.UnaryOperator;

public class GUI implements Listener {

    Inventory menu;
    HashMap<ItemStack, UnaryOperator> functionsRight = new HashMap<>();
    HashMap<ItemStack, UnaryOperator> functionsLeft = new HashMap<>();
    UnaryOperator funcClosedMenu;
    EconomyEdem plugin = EconomyEdem.getInstance;
    boolean close = false;
    boolean blockInv = true;

    public void createMenu(Integer size, String title, UnaryOperator funcClosedMenu) {
        this.menu = Bukkit.createInventory(null, size, title);
        this.funcClosedMenu = funcClosedMenu;

    }

    public void createMenu(Integer size, String title, UnaryOperator funcClosedMenu, boolean blockInv) {
        this.menu = Bukkit.createInventory(null, size, title);
        this.funcClosedMenu = funcClosedMenu;
        this.blockInv = blockInv;

    }

    public void addItem(Integer index, ItemStack item, UnaryOperator func) {
        if (this.menu.getItem(index) == null) {
            this.menu.setItem(index, item);
            this.functionsRight.put(item, func);
        }
    }

    public void addLeftClick(ItemStack item, UnaryOperator func) {
        this.functionsLeft.put(item, func);
    }

    @EventHandler
    public void closedInv(InventoryCloseEvent event) {
        if (close) {
            return;
        }
        if (event.getInventory().equals(this.menu)) {
            plugin.removeListener(this);
            if (funcClosedMenu != null) {
                funcClosedMenu.apply(event.getPlayer());
            }
            close = true;
        }
    }

    public void openInv(Player p) {
        p.openInventory(this.menu);
    }



    @EventHandler
    public void callFunc(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player p = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        if (event.getInventory().equals(this.menu)) {
            event.setCancelled(true);
            if (event.getClick().isRightClick()) {
                if (functionsRight.containsKey(item)) {
                    if (functionsRight.get(item) != null) {
                        functionsRight.get(item).apply(p);
                    }
                }
            } else {
                if (functionsLeft.containsKey(item)) {
                    if (functionsLeft.get(item) != null) {
                        functionsLeft.get(item).apply(p);
                    }
                }
            }
        }


    }


}
