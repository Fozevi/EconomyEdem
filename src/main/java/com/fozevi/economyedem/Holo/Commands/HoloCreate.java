package com.fozevi.economyedem.Holo.Commands;

import com.fozevi.economyedem.EconomyEdem;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;

public class HoloCreate implements CommandExecutor {

    EconomyEdem plugin = EconomyEdem.getInstance;

    HashMap<String, Hologram> holograms = new HashMap<>();

    boolean startUpdate = false;

    HashMap<String, Integer> currenciesCostsLast;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Эту команду можно использовать только от имени игрока");
            return true;
        }

        Player p = (Player) sender;

        if (!p.hasPermission("eco.holo")) {
            p.sendMessage(ChatColor.RED + "У вас нет прав для использования этой команды");
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                p.sendMessage(ChatColor.DARK_PURPLE + "Список голограмм: ");
                for (String holo: holograms.keySet()) {
                    p.sendMessage(ChatColor.GOLD + holo);
                }
                return true;
            }

        }

        if (args.length > 1) {

            String action = args[0];
            if (action.equalsIgnoreCase("create")) {
                String name = args[1];
                if (holograms.containsKey(name)) {
                    p.sendMessage(ChatColor.RED + "Голограмма с таким названием уже существует");
                    return true;
                }
                Location loc = p.getLocation();
                create(name, loc);
            } else if (action.equalsIgnoreCase("delete")) {
                String name = args[1];
                if (!holograms.containsKey(name)) {
                    p.sendMessage(ChatColor.RED + "Нет голограммы с таким названием");
                    return true;
                }
                Hologram hologram = holograms.get(name);
                hologram.delete();
                plugin.getHoloCfg().set("holograms." + name, null);
                plugin.saveHoloCfg();
                holograms.remove(name);
            }

        } else {
            p.sendMessage(ChatColor.YELLOW + "/eholo create/delete <название>");
            return true;
        }

        return true;
    }

    public void create(String name, Location location) {
        Hologram hologram = HologramsAPI.createHologram(plugin, location);

        holograms.put(name, hologram);

        plugin.getHoloCfg().set("holograms." + name + ".world", location.getWorld().getName());
        plugin.getHoloCfg().set("holograms." + name + ".x", location.getX());
        plugin.getHoloCfg().set("holograms." + name + ".y", location.getY());
        plugin.getHoloCfg().set("holograms." + name + ".z", location.getZ());
        plugin.getHoloCfg().set("holograms." + name + ".pitch", location.getPitch());
        plugin.getHoloCfg().set("holograms." + name + ".yaw", location.getYaw());

        plugin.saveHoloCfg();
        reloadAllHolo();

        if (!startUpdate) {
            StartUpdate();
            startUpdate = true;
        }
    }


    public void StartUpdate() {
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                reloadAllHolo();
            }
        }.runTaskTimer(plugin, 0, 20 * 60 * 5);


    }

    public void reloadAllHolo() {
        ArrayList<String> currencies = plugin.connect.getCurrencies();

        HashMap<String, Integer> currenciesCosts = new HashMap<>();
        for (String currency: currencies) {
            Integer cost = plugin.connect.getCost(currency);
            currenciesCosts.put(currency, cost);
        }
        HashMap<String, Integer> sortCurrencies = currenciesCosts.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors
                        .toMap(Map.Entry::getKey,
                                Map.Entry::getValue,
                                (e1, e2) -> e1,
                                LinkedHashMap::new));

        for (Hologram hologram: holograms.values()) {
            for (int i = hologram.size(); i != 0; i--) {
                hologram.removeLine(i - 1);
            }
            hologram.appendTextLine(ChatColor.YELLOW + "Валюты" + ChatColor.DARK_PURPLE + " Эдема");
            hologram.appendTextLine(ChatColor.LIGHT_PURPLE + "Название - Стоимость");
        }


        for (Map.Entry<String, Integer> entry: sortCurrencies.entrySet()) {
            for (Hologram hologram: holograms.values()) {
                if (currenciesCostsLast != null && currenciesCostsLast.get(entry.getKey()) != null) {
                    Integer lastCost = currenciesCostsLast.get(entry.getKey());
                    if (lastCost > entry.getValue()) {
                        hologram.appendTextLine(ChatColor.WHITE + entry.getKey() + ChatColor.GOLD + " - " + ChatColor.RED + entry.getValue() + " ↓");
                    } else if (lastCost < entry.getValue()) {
                        hologram.appendTextLine(ChatColor.WHITE + entry.getKey() + ChatColor.GOLD + " - " + ChatColor.GREEN + entry.getValue() + " ↑");
                    } else if (lastCost.equals(entry.getValue())) {
                        hologram.appendTextLine(ChatColor.WHITE + entry.getKey() + ChatColor.GOLD + " - " + ChatColor.AQUA + entry.getValue());
                    }
                } else {
                    hologram.appendTextLine(ChatColor.WHITE + entry.getKey() + ChatColor.GOLD + " - " + ChatColor.AQUA + entry.getValue());
                }
            }
        }
        currenciesCostsLast = sortCurrencies;
        for (Hologram hologram: holograms.values()) {
            hologram.appendTextLine(ChatColor.DARK_GREEN + "Обновление таблицы происходит каждые 5 минут");
        }
    }
}
