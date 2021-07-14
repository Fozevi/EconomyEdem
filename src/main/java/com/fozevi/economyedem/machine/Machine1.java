package com.fozevi.economyedem.machine;

import com.fozevi.economyedem.EconomyEdem;
import com.fozevi.economyedem.Events.MachineUpdateEvent;
import com.fozevi.economyedem.util.GUI;
import com.fozevi.economyedem.util.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class Machine1 {

    EconomyEdem plugin = EconomyEdem.getInstance;

    public Object first(Object obj) {
        Player p = (Player) obj;
        if (plugin.getMoneyCrafter.lastMenu.containsKey(p)) {
            plugin.getMoneyCrafter.lastMenu.replace(p, plugin.getMoneyCrafter::createMenu);
        } else {
            plugin.getMoneyCrafter.lastMenu.put(p, plugin.getMoneyCrafter::createMenu);
        }
        GUI gui = new GUI();
        if (!plugin.getMoneyCrafter.multiple.containsKey(p)) {
            plugin.getMoneyCrafter.multiple.put(p, 100);
        }
        gui.createMenu(9, ChatColor.AQUA + "Настройки станка", plugin.getMoneyCrafter.lastMenu.get(p));
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.YELLOW + "Нажмите ЛКМ,");
        lore.add(ChatColor.YELLOW + "чтобы убрать " + plugin.getMoneyCrafter.multiple.get(p));
        lore.add(ChatColor.YELLOW + "от производительности станка");
        lore.add(ChatColor.GREEN + "Сейчас производит: " + plugin.connect.getMoney(plugin.connect.getCurrencyForPlayer(p.getName()), "money1"));
        ItemStack item = ItemCreator.createItem(Material.RED_WOOL, 1, (byte) 0, ChatColor.RED + "Минус " + plugin.getMoneyCrafter.multiple.get(p), lore, null);
        gui.addItem(2, item, null);
        gui.addLeftClick(item, this::minusMoney1);

        lore.clear();

        lore.add(ChatColor.YELLOW + "Нажмите ЛКМ,");
        lore.add(ChatColor.YELLOW + "чтобы добавить " + plugin.getMoneyCrafter.multiple.get(p));
        lore.add(ChatColor.YELLOW + "к производительности станка");
        lore.add(ChatColor.GREEN + "Сейчас производит: " + plugin.connect.getMoney(plugin.connect.getCurrencyForPlayer(p.getName()), "money1"));
        ItemStack woolGreen = ItemCreator.createItem(Material.GREEN_WOOL, 1, (byte) 0, ChatColor.GREEN + "Плюс " + plugin.getMoneyCrafter.multiple.get(p), lore, null);
        gui.addItem(6, woolGreen, null);
        gui.addLeftClick(woolGreen, this::plusMoney1);

        lore.clear();
        lore.add(ChatColor.GREEN + "Кратность сейчас - " + plugin.getMoneyCrafter.multiple.get(p));
        ItemStack woolYellow = ItemCreator.createItem(Material.YELLOW_WOOL, 1, (byte) 0, ChatColor.GOLD + "Настроить кратность", lore, null);
        gui.addItem(4, woolYellow, null);
        gui.addLeftClick(woolYellow, plugin.getMoneyCrafter::settingMultiple);


        ItemStack backItem = ItemCreator.createItem(Material.BARRIER, 1, (byte) 0, ChatColor.RED + "Назад", null, null);
        gui.addItem(8, backItem, null);
        gui.addLeftClick(backItem, plugin.getMoneyCrafter::back);

        gui.openInv(p);
        plugin.addListener(gui);

        return p;
    }

    public Object settingTime(Object obj) {
        Player p = (Player) obj;
        if (plugin.getMoneyCrafter.lastMenu.containsKey(p)) {
            plugin.getMoneyCrafter.lastMenu.replace(p, plugin.getMoneyCrafter::createMenu);
        } else {
            plugin.getMoneyCrafter.lastMenu.put(p, plugin.getMoneyCrafter::createMenu);
        }
        GUI gui = new GUI();

        gui.createMenu(9, ChatColor.AQUA + "Время", plugin.getMoneyCrafter.lastMenu.get(p));

        ArrayList<String> lore = new ArrayList<>();

        lore.add(ChatColor.YELLOW + "Время сейчас - " + plugin.connect.getTime(plugin.connect.getCurrencyForPlayer(p.getName()), "time1"));
        ItemStack redWool = ItemCreator.createItem(Material.RED_WOOL, 1, (byte) 0, ChatColor.RED + "Минус 1 минута", lore, null);
        ItemStack greenWool = ItemCreator.createItem(Material.GREEN_WOOL, 1, (byte) 0, ChatColor.GREEN + "Плюс 1 минута", lore, null);
        ItemStack backItem = ItemCreator.createItem(Material.BARRIER, 1, (byte) 0, ChatColor.RED + "Назад", null, null);

        gui.addItem(2, redWool, null);
        gui.addLeftClick(redWool, this::minusTime1);

        gui.addItem(6, greenWool, null);
        gui.addLeftClick(greenWool, this::plusTime1);

        gui.addItem(8, backItem, null);
        gui.addLeftClick(backItem, plugin.getMoneyCrafter::back);

        gui.openInv(p);
        plugin.addListener(gui);
        return obj;
    }


    public Object minusTime1(Object obj) {
        Player p = (Player) obj;
        String msg = plugin.connect.minusTime(plugin.connect.getCurrencyForPlayer(p.getName()), "time1");
        if (!msg.equalsIgnoreCase("")) {
            p.sendMessage(msg);
        }
        settingTime(p);
        plugin.getMoneyCrafter.callUpdate();
        return obj;
    }

    public Object plusTime1(Object obj) {
        Player p = (Player) obj;
        String currency = plugin.connect.getCurrencyForPlayer(p.getName());


        String msg = plugin.connect.plusTime(currency, "time1");
        if (!msg.equalsIgnoreCase("")) {
            p.sendMessage(msg);
        }
        plugin.getMoneyCrafter.callUpdate();
        settingTime(p);
        return obj;
    }


    public Object minusMoney1(Object obj) {
        Player p = (Player) obj;
        plugin.connect.minusMoney(plugin.connect.getCurrencyForPlayer(p.getName()), "money1", plugin.getMoneyCrafter.multiple.get(p));
        first(p);
        plugin.getMoneyCrafter.callUpdate();
        return obj;
    }
    public Object plusMoney1(Object obj) {
        Player p = (Player) obj;
        String msg = plugin.connect.plusMoney(plugin.connect.getCurrencyForPlayer(p.getName()), "money1", plugin.getMoneyCrafter.multiple.get(p));
        if (!msg.equalsIgnoreCase("")) {
            p.sendMessage(msg);
        }
        plugin.getMoneyCrafter.callUpdate();
        first(p);
        return obj;
    }

    public Object first2(Object obj) {
        Player p = (Player) obj;
        plugin.connect.switchMachine(plugin.connect.getCurrencyForPlayer(p.getName()), "machine1");
        plugin.getMoneyCrafter.createMenu(p);
        plugin.getMoneyCrafter.callUpdate();
        return obj;
    }

}
