package com.fozevi.economyedem.Commands;

import com.fozevi.economyedem.EconomyEdem;
import com.fozevi.economyedem.Events.MachineUpdateEvent;
import com.fozevi.economyedem.machine.Machine1;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.UnaryOperator;

public class getMoneyCrafter implements CommandExecutor {

    EconomyEdem plugin = EconomyEdem.getInstance;
    public HashMap<Player, Integer> multiple = new HashMap<>();

    public HashMap<Player, UnaryOperator> lastMenu = new HashMap<>();
    public Machine1 machine1 = new Machine1();
    public MachineUpdateEvent machineUpdateEvent;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Эту команду можно использовать только от имени игрока");
            return false;
        }
        Player p = (Player) sender;

        createMenu(p);

        return true;
    }

    public Object createMenu (Object obj) {
        GUI gui = new GUI();
        gui.createMenu(9, ChatColor.DARK_PURPLE + "Настройка станка", null);
        Player p = (Player) obj;
        String currency = plugin.connect.getCurrencyForPlayer(p.getName());
        machineUpdateEvent = new MachineUpdateEvent(currency);

        ArrayList lore = new ArrayList();
        Integer moneyCraft1 = plugin.connect.getMoney(currency, "money1");
        Integer timeCraft1 = plugin.connect.getTime(currency, "time1");

        if (plugin.connect.getCurrencyForPlayer(p.getName()) == null) {
            p.sendMessage(ChatColor.RED + "Вы не являетесь лидером ни одной валюты");
            return true;
        }


        if (plugin.connect.purchased(currency, "purchased1")) {
            lore.add(ChatColor.GREEN + "Производительность " + moneyCraft1 + " валюты в "+ timeCraft1 +" минут(у/ы)");
            lore.add(ChatColor.GREEN + "Включить/Выключить станок - ЛКМ");
            if (plugin.connect.enableMachine(currency, "machine1")) {
                lore.add(ChatColor.YELLOW + "Включено");
            } else {
                lore.add(ChatColor.GRAY + "Выключено");
            }
            ItemStack redstoneBlock = ItemCreator.createItem(Material.REDSTONE_BLOCK, 1, (byte) 0, ChatColor.GOLD + "Станок", lore, null);
            gui.addItem(2, redstoneBlock, machine1::first);
            gui.addLeftClick(redstoneBlock, machine1::first2);

            lore.clear();
            Integer lvlNext = plugin.connect.getLvlTime(currency) + 1;
            Integer lvl = plugin.connect.getLvlTime(currency);
            if (plugin.getConfig().contains("settings.machines.update.time.lvl" + lvlNext)) {
                lore.add(ChatColor.YELLOW + "Нажмите ЛКМ для повышения уровня");

                lore.add(ChatColor.GOLD + "Стоимость повышения уровня: " + ChatColor.BLUE + plugin.getConfig().getString("settings.machines.update.time.lvl" + lvlNext + ".cost") + " x" + plugin.getConfig().getInt("settings.machines.update.time.lvl" + lvlNext + ".amount"));

                lore.add(ChatColor.YELLOW + "Нажмите ПКМ для настройки времени");

                ItemStack ironBlock = ItemCreator.createItem(Material.CLOCK, 1, (byte) 0, ChatColor.GOLD + "Время §l§6LVL " + lvl, lore, null);
                gui.addItem(4, ironBlock, machine1::settingTime);
                gui.addLeftClick(ironBlock, this::updateTime);
            } else {
                lore.add(ChatColor.GREEN + "У вас максимальный уровень прокачки времени");
                lore.add(ChatColor.YELLOW + "Нажмите ПКМ для настройки времени");
                ItemStack ironBlock = ItemCreator.createItem(Material.CLOCK, 1, (byte) 0, ChatColor.GOLD + "Время §l§6LVL " + lvl, lore, null);
                gui.addItem(4, ironBlock, machine1::settingTime);
            }

            lore.clear();
            Integer lvlNextMoney = plugin.connect.getLvlMoney(currency) + 1;
            Integer lvlMoney = plugin.connect.getLvlMoney(currency);
            if (plugin.getConfig().contains("settings.machines.update.money.lvl" + lvlNextMoney)) {
                lore.add(ChatColor.YELLOW + "Нажмите ЛКМ для повышения уровня валюты");

                lore.add(ChatColor.GOLD + "Стоимость повышения уровня: " + ChatColor.BLUE + plugin.getConfig().getString("settings.machines.update.money.lvl" + lvlNextMoney + ".cost") + " x" + plugin.getConfig().getInt("settings.machines.update.money.lvl" + lvlNextMoney + ".amount"));

                lore.add(ChatColor.YELLOW + "Нажмите ПКМ для настройки валюты");

                ItemStack goldBlock = ItemCreator.createItem(Material.GOLD_INGOT, 1, (byte) 0, ChatColor.GOLD + "Валюта §l§6LVL " + lvlMoney, lore, null);
                gui.addItem(6, goldBlock, machine1::first);
                gui.addLeftClick(goldBlock, this::updateMoney);
            } else {
                lore.add(ChatColor.GREEN + "У вас максимальный уровень валюты");
                lore.add(ChatColor.YELLOW + "Нажмите ПКМ для настройки валюты");
                ItemStack goldBlock = ItemCreator.createItem(Material.GOLD_INGOT, 1, (byte) 0, ChatColor.GOLD + "Валюта §l§6LVL " + lvlMoney, lore, null);
                gui.addItem(6, goldBlock, machine1::first);
            }

        } else {
            lore.add(ChatColor.YELLOW + "Покупка станка");
            lore.add(ChatColor.GREEN + "Левый клик - купить");
            lore.add(ChatColor.YELLOW + "Стоимость: " + ChatColor.BLUE +  plugin.getConfig().getString("settings.machines.buy.cost") + " x" + plugin.getConfig().getInt("settings.machines.buy.amount"));
            ItemStack redstoneBlock = ItemCreator.createItem(Material.REDSTONE_BLOCK, 1, (byte) 0, ChatColor.GRAY + "Покупка станка", lore, null);
            gui.addItem(2, redstoneBlock, null);
            gui.addLeftClick(redstoneBlock, this::buy);
        }


        gui.openInv(p);
        plugin.addListener(gui);
        return obj;
    }


    public void callUpdate() {
        Bukkit.getPluginManager().callEvent(machineUpdateEvent);
    }

    public Object updateTime(Object obj) {
        Player p = (Player) obj;
        String currency = plugin.connect.getCurrencyForPlayer(p.getName());
        Integer lvl = plugin.connect.getLvlTime(currency) + 1;
        if (p.getInventory().contains(Material.valueOf(plugin.getConfig().getString("settings.machines.update.time.lvl" + lvl + ".cost")), plugin.getConfig().getInt("settings.machines.update.time.lvl" + lvl + ".amount"))) {
            ItemStack items = new ItemStack(Material.valueOf(plugin.getConfig().getString("settings.machines.update.time.lvl" + lvl + ".cost")), plugin.getConfig().getInt("settings.machines.update.time.lvl" + lvl + ".amount"));
            p.getInventory().removeItem(items);
            plugin.connect.upLvlTime(currency);
            createMenu(p);
            callUpdate();
        } else {
            p.sendMessage(ChatColor.RED + "Вам не хватает ресурсов для покупки улучшения");
        }
        return obj;
    }

    public Object updateMoney(Object obj) {
        Player p = (Player) obj;
        String currency = plugin.connect.getCurrencyForPlayer(p.getName());
        Integer lvl = plugin.connect.getLvlMoney(currency) + 1;
        if (p.getInventory().contains(Material.valueOf(plugin.getConfig().getString("settings.machines.update.money.lvl" + lvl + ".cost")), plugin.getConfig().getInt("settings.machines.update.money.lvl" + lvl + ".amount"))) {
            ItemStack items = new ItemStack(Material.valueOf(plugin.getConfig().getString("settings.machines.update.money.lvl" + lvl + ".cost")), plugin.getConfig().getInt("settings.machines.update.money.lvl" + lvl + ".amount"));
            p.getInventory().removeItem(items);
            plugin.connect.upLvlMoney(currency);
            createMenu(p);
            callUpdate();
        } else {
            p.sendMessage(ChatColor.RED + "Вам не хватает ресурсов для покупки улучшения");
        }
        return obj;
    }


    public Object buy(Object obj) {
        Player p = (Player) obj;
        if (p.getInventory().contains(Material.valueOf(plugin.getConfig().getString("settings.machines.buy.cost")), plugin.getConfig().getInt("settings.machines.buy.amount"))) {
            ItemStack items = new ItemStack(Material.valueOf(plugin.getConfig().getString("settings.machines.buy.cost")), plugin.getConfig().getInt("settings.machines.buy.amount"));
            p.getInventory().removeItem(items);
            plugin.connect.buyMachine(plugin.connect.getCurrencyForPlayer(p.getName()));
            createMenu(p);
            callUpdate();
        } else {
          p.sendMessage(ChatColor.RED + "Вам не хватает ресурсов для покупки станка");
        }
        return obj;
    }


    public Object back(Object obj) {
        lastMenu.get(obj).apply(obj);
        return obj;
    }

    public Object settingMultiple(Object obj) {
        Player p = (Player) obj;
        if (lastMenu.containsKey(p)) {
            lastMenu.replace(p, machine1::first);
        } else {
            lastMenu.put(p, machine1::first);
        }
        GUI gui = new GUI();

        gui.createMenu(18, ChatColor.DARK_PURPLE + "Кратность", null);

        ArrayList<String> lore = new ArrayList<>();

        lore.add(ChatColor.YELLOW + "Сейчас: " + multiple.get(p));
        lore.add(ChatColor.YELLOW + "ЛКМ - прибавить");
        lore.add(ChatColor.YELLOW + "ПКМ - отнять");

        ItemStack terrFirst = ItemCreator.createItem(Material.WHITE_TERRACOTTA, 1, (byte) 0, ChatColor.GREEN + "+10", lore, null);
        ItemStack terrTwo = ItemCreator.createItem(Material.ORANGE_TERRACOTTA, 1, (byte) 0, ChatColor.GREEN + "+50", lore, null);
        ItemStack terrThree = ItemCreator.createItem(Material.MAGENTA_TERRACOTTA, 1, (byte) 0, ChatColor.GREEN + "+100", lore, null);
        ItemStack terrFour = ItemCreator.createItem(Material.LIGHT_BLUE_TERRACOTTA, 1, (byte) 0, ChatColor.GREEN + "+1000", lore, null);
        ItemStack terrFive = ItemCreator.createItem(Material.YELLOW_TERRACOTTA, 1, (byte) 0, ChatColor.GREEN + "+5000", lore, null);
        ItemStack terrSix = ItemCreator.createItem(Material.LIME_TERRACOTTA, 1, (byte) 0, ChatColor.GREEN + "+10000", lore, null);
        ItemStack terrSeven = ItemCreator.createItem(Material.PINK_TERRACOTTA, 1, (byte) 0, ChatColor.GREEN + "+50000", lore, null);
        ItemStack terrEight = ItemCreator.createItem(Material.GRAY_TERRACOTTA, 1, (byte) 0, ChatColor.GREEN + "+100000", lore, null);
        ItemStack terrNine = ItemCreator.createItem(Material.LIGHT_GRAY_TERRACOTTA, 1, (byte) 0, ChatColor.GREEN + "+500000", lore, null);


        gui.addItem(0, terrFirst, this::minusMultipleFirst);
        gui.addLeftClick(terrFirst, this::plusMultipleFirst);

        gui.addItem(1, terrTwo, this::minusMultipleTwo);
        gui.addLeftClick(terrTwo, this::plusMultipleTwo);

        gui.addItem(2, terrThree, this::minusMultipleThree);
        gui.addLeftClick(terrThree, this::plusMultipleThree);

        gui.addItem(3, terrFour, this::minusMultipleFour);
        gui.addLeftClick(terrFour, this::plusMultipleFour);

        gui.addItem(4, terrFive, this::minusMultipleFive);
        gui.addLeftClick(terrFive, this::plusMultipleFive);

        gui.addItem(5, terrSix, this::minusMultipleSix);
        gui.addLeftClick(terrSix, this::plusMultipleSix);

        gui.addItem(6, terrSeven, this::minusMultipleSeven);
        gui.addLeftClick(terrSeven, this::plusMultipleSeven);

        gui.addItem(7, terrEight, this::minusMultipleEight);
        gui.addLeftClick(terrEight, this::plusMultipleEight);

        gui.addItem(8, terrNine, this::minusMultipleNine);
        gui.addLeftClick(terrNine, this::plusMultipleNine);

        ItemStack backItem = ItemCreator.createItem(Material.BARRIER, 1, (byte) 0, ChatColor.RED + "Назад", null, null);
        gui.addItem(17, backItem, null);
        gui.addLeftClick(backItem, this::back);

        gui.openInv(p);
        plugin.addListener(gui);

        return obj;
    }



    public Object minusMultipleFirst(Object obj) {
        Player p = (Player) obj;
        multiple.replace(p, multiple.get(p) - 10);
        if (multiple.get(p) < 0) {
            multiple.replace(p, 0);
        }
        settingMultiple(obj);
        return obj;
    }

    public Object minusMultipleTwo(Object obj) {
        Player p = (Player) obj;
        multiple.replace(p, multiple.get(p) - 50);
        if (multiple.get(p) < 0) {
            multiple.replace(p, 0);
        }
        settingMultiple(obj);
        return obj;
    }

    public Object minusMultipleThree(Object obj) {
        Player p = (Player) obj;
        multiple.replace(p, multiple.get(p) - 100);
        if (multiple.get(p) < 0) {
            multiple.replace(p, 0);
        }
        settingMultiple(obj);
        return obj;
    }

    public Object minusMultipleFour(Object obj) {
        Player p = (Player) obj;
        multiple.replace(p, multiple.get(p) - 1000);
        if (multiple.get(p) < 0) {
            multiple.replace(p, 0);
        }
        settingMultiple(obj);
        return obj;
    }

    public Object minusMultipleFive(Object obj) {
        Player p = (Player) obj;
        multiple.replace(p, multiple.get(p) - 5000);
        if (multiple.get(p) < 0) {
            multiple.replace(p, 0);
        }
        settingMultiple(obj);
        return obj;
    }

    public Object minusMultipleSix(Object obj) {
        Player p = (Player) obj;
        multiple.replace(p, multiple.get(p) - 10000);
        if (multiple.get(p) < 0) {
            multiple.replace(p, 0);
        }
        settingMultiple(obj);
        return obj;
    }

    public Object minusMultipleSeven(Object obj) {
        Player p = (Player) obj;
        multiple.replace(p, multiple.get(p) - 50000);
        if (multiple.get(p) < 0) {
            multiple.replace(p, 0);
        }
        settingMultiple(obj);
        return obj;
    }

    public Object minusMultipleEight(Object obj) {
        Player p = (Player) obj;
        multiple.replace(p, multiple.get(p) - 100000);
        if (multiple.get(p) < 0) {
            multiple.replace(p, 0);
        }
        settingMultiple(obj);
        return obj;
    }

    public Object minusMultipleNine(Object obj) {
        Player p = (Player) obj;
        multiple.replace(p, multiple.get(p) - 500000);
        if (multiple.get(p) < 0) {
            multiple.replace(p, 0);
        }
        settingMultiple(obj);
        return obj;
    }


    public Object plusMultipleFirst(Object obj) {
        Player p = (Player) obj;
        multiple.replace(p, multiple.get(p) + 10);
        if (multiple.get(p) > 1000000000) {
            multiple.replace(p, 1000000000);
        }
        settingMultiple(obj);
        return obj;
    }

    public Object plusMultipleTwo(Object obj) {
        Player p = (Player) obj;
        multiple.replace(p, multiple.get(p) + 50);
        if (multiple.get(p) > 1000000000) {
            multiple.replace(p, 1000000000);
        }
        settingMultiple(obj);
        return obj;
    }

    public Object plusMultipleThree(Object obj) {
        Player p = (Player) obj;
        multiple.replace(p, multiple.get(p) + 100);
        if (multiple.get(p) > 1000000000) {
            multiple.replace(p, 1000000000);
        }
        settingMultiple(obj);
        return obj;
    }

    public Object plusMultipleFour(Object obj) {
        Player p = (Player) obj;
        multiple.replace(p, multiple.get(p) + 1000);
        if (multiple.get(p) > 1000000000) {
            multiple.replace(p, 1000000000);
        }
        settingMultiple(obj);
        return obj;
    }

    public Object plusMultipleFive(Object obj) {
        Player p = (Player) obj;
        multiple.replace(p, multiple.get(p) + 5000);
        if (multiple.get(p) > 1000000000) {
            multiple.replace(p, 1000000000);
        }
        settingMultiple(obj);
        return obj;
    }

    public Object plusMultipleSix(Object obj) {
        Player p = (Player) obj;
        multiple.replace(p, multiple.get(p) + 10000);
        if (multiple.get(p) > 1000000000) {
            multiple.replace(p, 1000000000);
        }
        settingMultiple(obj);
        return obj;
    }

    public Object plusMultipleSeven(Object obj) {
        Player p = (Player) obj;
        multiple.replace(p, multiple.get(p) + 50000);
        if (multiple.get(p) > 1000000000) {
            multiple.replace(p, 1000000000);
        }
        settingMultiple(obj);
        return obj;
    }

    public Object plusMultipleEight(Object obj) {
        Player p = (Player) obj;
        multiple.replace(p, multiple.get(p) + 100000);
        if (multiple.get(p) > 1000000000) {
            multiple.replace(p, 1000000000);
        }
        settingMultiple(obj);
        return obj;
    }

    public Object plusMultipleNine(Object obj) {
        Player p = (Player) obj;
        multiple.replace(p, multiple.get(p) + 500000);
        if (multiple.get(p) > 1000000000) {
            multiple.replace(p, 1000000000);
        }
        settingMultiple(obj);
        return obj;
    }





}
