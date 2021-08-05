package com.fozevi.economyedem.Curse;

import com.fozevi.economyedem.EconomyEdem;
import com.fozevi.economyedem.util.GUI;
import com.fozevi.economyedem.util.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class ControlPanel implements CommandExecutor {


    EconomyEdem plugin = EconomyEdem.getInstance;

    HashMap<Player, ArrayList<Player>> selectPlayer = new HashMap<>();


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Эту команду можно использовать только от имени игрока");
            return true;
        }

        Player p = (Player) sender;

        String currency = plugin.connect.getCurrencyForPlayer(p.getName());

        if (currency.equals(null)) {
            p.sendMessage(ChatColor.RED + "Вы не являетесь лидером ни одной валюты");
            return true;
        }

        GUI gui = new GUI();

        gui.createMenu(9, ChatColor.DARK_PURPLE + "Управление валютой", null);

        ArrayList infoLore = new ArrayList();

        ResultSet resultSet = plugin.connect.getPlayersForCurrency(currency);
        String owner1;
        String owner2;
        String owner3;
        try {
            owner1 = resultSet.getString(1);
            owner2 = resultSet.getString(2);
            owner3 = resultSet.getString(3);
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }

        infoLore.add(ChatColor.GOLD + "Валюта: " + currency);
        infoLore.add(ChatColor.GREEN + "Владельцы: " + ChatColor.YELLOW + owner1 + ChatColor.GREEN + ", " + owner2 + ", " + owner3);
        infoLore.add(ChatColor.BLUE + "Цена по отнош. к другим валютам: " + plugin.connect.getCost(currency));
        infoLore.add(ChatColor.DARK_AQUA + "Денег в обороте: "+ plugin.connect.getMoneysInCurrency(currency));
        ItemStack infoItem = ItemCreator.createItem(Material.BOOK, 1, (byte) 0, ChatColor.LIGHT_PURPLE + "Информация", infoLore, null);

        ArrayList infoCurse = new ArrayList();

        infoCurse.add(ChatColor.YELLOW + "Нажмите ЛКМ для открытия меню просмотра");


        ItemStack curse = ItemCreator.createItem(Material.GOLD_INGOT, 1, (byte) 0, ChatColor.DARK_GREEN + "Финансовая информация", infoCurse, null);

        ArrayList<String> lore = new ArrayList<>();

        lore.add(ChatColor.YELLOW + "Нажмите ЛКМ для открытия меню станков");


        ItemStack machines = ItemCreator.createItem(Material.DISPENSER, 1, (byte) 0, ChatColor.BLUE + "Станки", lore, null);

        ArrayList<String> loreChange = new ArrayList<>();

        lore.add(ChatColor.YELLOW + "Нажмите ЛКМ для открытия меню действий");

        ItemStack changeLeader = ItemCreator.createItem(Material.END_CRYSTAL, 1, (byte) 0, ChatColor.DARK_RED + "Смена лидеров", loreChange, null);



        gui.addItem(0, infoItem, null);

        gui.addItem(2, curse, null);
        gui.addLeftClick(curse, this::openLogsMenu);

        gui.addItem(4, machines, null);
        gui.addLeftClick(machines, this::openMachines);

//        gui.addItem(6, changeLeader, null);
//        gui.addLeftClick(changeLeader, this::openChangeLeaderMenu);
        gui.openInv(p);
        plugin.addListener(gui);


        return true;
    }


    private Object openChangeLeaderMenu(Object object) {
        Player player = (Player) object;

        String currencyName = plugin.connect.getCurrencyForPlayer(player.getName());
        String owner1 = plugin.connect.getOwner1(currencyName);

        if (!owner1.equalsIgnoreCase(player.getName())) {
            player.sendMessage(ChatColor.RED + "Это меню может открыть только главный лидер");
            return object;
        }

        GUI gui = new GUI();

        ResultSet resultSet = plugin.connect.getPlayersForCurrency(currencyName);
        String owner2;
        String owner3;
        try {
            owner2 = resultSet.getString(2);
            owner3 = resultSet.getString(3);
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }

        gui.createMenu(9, ChatColor.DARK_PURPLE + "Смена лидера", null);
        ArrayList<String> lore = new ArrayList<>();

        lore.add(ChatColor.YELLOW + "Нажмите ЛКМ для выбора нового лидера");
        ItemStack head1 = ItemCreator.createItem(Material.SKELETON_SKULL, 1, (byte) 0, ChatColor.BLUE + owner2, lore, null);
        ItemStack head2 = ItemCreator.createItem(Material.ZOMBIE_HEAD, 1, (byte) 0, ChatColor.BLUE + owner3, lore, null);

        gui.addItem(2, head1, null);
        gui.addLeftClick(head1, this::changeFirst);

        return object;
    }


    private Object changeFirst(Object object) {

        Player player = (Player) object;

        String currencyName = plugin.connect.getCurrencyForPlayer(player.getName());

        ResultSet resultSet = plugin.connect.getPlayersForCurrency(currencyName);
        String owner1;
        String owner2;
        String owner3;
        try {
            owner1 = resultSet.getString(1);
            owner2 = resultSet.getString(2);
            owner3 = resultSet.getString(3);
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }

        GUI gui = new GUI();

        gui.createMenu(9 * 8, ChatColor.DARK_PURPLE + "Смена лидера " + ChatColor.YELLOW + owner2, null);

        Integer index = 0;
        for (Player playerOnline: Bukkit.getOnlinePlayers()) {
            ItemStack playerItem = ItemCreator.createItem(Material.PLAYER_HEAD, 1, (byte) 0, ChatColor.GREEN + playerOnline.getName(), null, null);
            gui.addItem(index, playerItem, null);
            gui.addLeftClick(playerItem, this::change);
            index += 1;
        }
        gui.openInv(player);
        plugin.addListener(gui);

        return object;
    }

    private Object changeSecond(Object object) {

        Player player = (Player) object;

        String currencyName = plugin.connect.getCurrencyForPlayer(player.getName());

        ResultSet resultSet = plugin.connect.getPlayersForCurrency(currencyName);
        String owner1;
        String owner2;
        String owner3;
        try {
            owner1 = resultSet.getString(1);
            owner2 = resultSet.getString(2);
            owner3 = resultSet.getString(3);
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }

        GUI gui = new GUI();

        gui.createMenu(9 * 8, ChatColor.DARK_PURPLE + "Смена лидера " + ChatColor.YELLOW + owner2, null);

        Integer index = 0;
        for (Player playerOnline: Bukkit.getOnlinePlayers()) {
            ItemStack playerItem = ItemCreator.createItem(Material.PLAYER_HEAD, 1, (byte) 0, ChatColor.GREEN + playerOnline.getName(), null, null);
            gui.addItem(index, playerItem, null);
            gui.addLeftClick(playerItem, this::change);
            index += 1;
        }
        gui.openInv(player);
        plugin.addListener(gui);

        return object;
    }


    private Object change(Object obj) {

        Player p = (Player) obj;

        String currencyName = plugin.connect.getCurrencyForPlayer(p.getName());

        ResultSet resultSet = plugin.connect.getPlayersForCurrency(currencyName);
        String owner2;
        String owner3;
        try {
            owner2 = resultSet.getString(2);
            owner3 = resultSet.getString(3);
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }

        ArrayList<Player> playersChange = selectPlayer.get(p);

        if (owner2.equalsIgnoreCase(playersChange.get(0).getName())) {
            plugin.connect.replaceLeader(currencyName, "owner2", playersChange.get(1).getName());
        } else if (owner3.equalsIgnoreCase(playersChange.get(0).getName())) {
            plugin.connect.replaceLeader(currencyName, "owner3", playersChange.get(1).getName());
        }

        p.sendMessage(ChatColor.GREEN + "Лидер успешно изменен");

        p.closeInventory();

        return obj;
    }

    private Object openMachines(Object object) {
        plugin.getMoneyCrafter.createMenu(object);
        return object;
    }


    private Object openLogsMenu(Object object) {

        Player p = (Player) object;

        GUI gui = new GUI();

        gui.createMenu(9 * 6, ChatColor.DARK_PURPLE + "Последние 54 действия", null);
        String currencyName = plugin.connect.getCurrencyForPlayer(p.getName());
        ArrayList<HashMap<String, Object>> logs = plugin.connect.getLogs(currencyName, 54);

        Integer index = 0;
        for (HashMap<String, Object> log: logs) {

            String action = "error";

            if (String.valueOf(log.get("action")).equalsIgnoreCase("convert")) {
                action = "конвертации";
            } else if (String.valueOf(log.get("action")).equalsIgnoreCase("generation")) {
                action = "генерации";
            } else if (String.valueOf(log.get("action")).equalsIgnoreCase("decrease")) {
                action = "неиспользование";
            }

            if ((int) log.get("cost2UP") > 0 && String.valueOf(log.get("currency2")).equalsIgnoreCase(currencyName)) {

                ArrayList lore = new ArrayList();

                lore.add(ChatColor.GREEN + "Повышение цены на " + log.get("cost2UP") + " из-за " + action);

                ItemStack itemGreen = ItemCreator.createItem(Material.GREEN_CONCRETE, 1, (byte) 0, ChatColor.GREEN + "Повышение цены", lore, null);

                gui.addItem(index, itemGreen, null);
                index += 1;

            }
            if ((int) log.get("cost1Down") != 0 && String.valueOf(log.get("currency1")).equalsIgnoreCase(currencyName)) {

                ArrayList lore = new ArrayList();

                lore.add(ChatColor.RED + "Понижение цены на " + log.get("cost1Down") + " из-за " + action);

                ItemStack itemRed = ItemCreator.createItem(Material.RED_CONCRETE, 1, (byte) 0, ChatColor.RED + "Понижение", lore, null);

                gui.addItem(index, itemRed, null);
                index += 1;

            }


        }


        gui.openInv(p);

        plugin.addListener(gui);

        return object;
    }
}
