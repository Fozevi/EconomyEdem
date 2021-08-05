package com.fozevi.economyedem.Shop;

import com.fozevi.economyedem.EconomyEdem;
import com.fozevi.economyedem.Events.MoneyEventFull;
import com.fozevi.economyedem.util.GUI;
import com.fozevi.economyedem.util.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class TradeCommand implements CommandExecutor {

    EconomyEdem plugin = EconomyEdem.getInstance;

    HashMap<Player, Player> activeTrades = new HashMap<>();
    HashMap<Player, ItemStack> activeItemTrade = new HashMap<>();
    HashMap<Player, String> activeValuteTrades = new HashMap<>();
    HashMap<Player, Integer> activeCostTrades = new HashMap<>();

    HashMap<Player, Player> activeRequestTrade = new HashMap<>();

    HashMap<String, ArrayList> activeTradesNotAccepted = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Эту команду можно использовать только от имени игрока");
            return true;
        }

        Player p = (Player) sender;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("debug")) {
                if (p.hasPermission("economy.admin")) {
                    p.sendMessage("activeTrades - " + activeTrades);
                    p.sendMessage("activeItemTrade - " + activeItemTrade);
                    p.sendMessage("activeValuteTrades - " + activeValuteTrades);
                    p.sendMessage("activeCostTrades - " + activeCostTrades);
                    p.sendMessage("activeTradesNotAccepted - " + activeTradesNotAccepted);
                    return true;
                }
            }
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("accept")) {
                accept(p.getName());
                return true;
            }
        }

        if (args.length >= 3) {
            String player = args[0];

            if (player.equalsIgnoreCase(p.getName())) {
                p.sendMessage(ChatColor.RED + "Вам что, не с кем торговать кроме как с самим собой?");
                return true;
            }

            Integer valute;
            Integer money;
            try {
                money = Integer.parseInt(args[1]);
            } catch (Exception e) {
                p.sendMessage(ChatColor.RED + "Цена должна быть в виде числа");
                return true;
            }
            try {
                valute = Integer.parseInt(args[2]);
            } catch (Exception e) {
                p.sendMessage(ChatColor.RED + "Цена должна быть в виде числа");
                return true;
            }

            String currencyName = plugin.connect.getValuteById(valute);

            if (currencyName == null) {
                p.sendMessage(ChatColor.RED + "Нет валюты с таким ID");
                return true;
            }
            if (Bukkit.getPlayer(player) == null) {
                p.sendMessage(ChatColor.RED + "Для торговли игрок должен быть на сервере");
                return true;
            }

            if (activeRequestTrade.containsKey(p)) {
                p.sendMessage(ChatColor.RED + "Вы уже отправили запрос на торговлю");
                return true;
            }

            if (!p.getItemInHand().getType().equals(Material.AIR)) {

                Bukkit.getPlayer(player).sendMessage(ChatColor.GREEN + "Игрок " + p.getName() + " предлагает вам торг, если вы согласны введите /trade accept");
                p.sendMessage(ChatColor.GREEN + "Запрос на торговлю отправлен.");

                new BukkitRunnable() {
                    String playerRun = player;
                    Player pRun = p;
                    @Override
                    public void run() {
                        if (!activeRequestTrade.containsKey(pRun)) {
                            return;
                        }
                        activeRequestTrade.remove(pRun);
                        p.sendMessage(ChatColor.RED + "Игрок " + playerRun + " не успел принять ваше предложение о торговле");
                        Bukkit.getPlayer(playerRun).sendMessage(ChatColor.RED + "Вы не успели принять предложение о торговле");
                    }
                }.runTaskLater(plugin, 20 * 60);
                ArrayList items = new ArrayList();
                items.add(p);
                items.add(money);
                items.add(currencyName);
                activeTradesNotAccepted.put(player, items);
                activeRequestTrade.put(p, Bukkit.getPlayer(player));

            } else {
                p.sendMessage(ChatColor.RED + "Нельзя быть как Китай и начать торговать воздухом. Для начала возьмите в руку предмет который хотите продать, а затем пишите команду");
            }
            return true;

        } else {
            p.sendMessage(ChatColor.YELLOW + "/trade <nick> <цена> <номер валюты>");
            return true;
        }

    }


    private void accept(String player) {
        if (!activeTradesNotAccepted.containsKey(player)) {
            Bukkit.getPlayer(player).sendMessage(ChatColor.RED + "Вам никто не предлагал торговлю");
            return;
        }
        ArrayList items = activeTradesNotAccepted.get(player);
        Player p = (Player) items.get(0);
        Integer money = (Integer) items.get(1);
        String currencyName = (String) items.get(2);
        startTrade(p, player, money, currencyName);

        p.sendMessage(ChatColor.GREEN + "Игрок " + player + " принял ваш запрос на торговлю");
        activeRequestTrade.remove(p);
        activeTradesNotAccepted.remove(player);
    }


    private void startTrade(Player p, String player, Integer money, String currencyName) {
        ItemStack tradeItem = p.getItemInHand();

        GUI tradeGui = new GUI();
        tradeGui.createMenu(9 * 6, ChatColor.DARK_PURPLE + "Торговля с " + player, this::cancelTrade);

        ItemStack chain = new ItemStack(Material.CHAIN);
        ItemStack lantern = new ItemStack(Material.LANTERN);
        ItemStack sign = new ItemStack(Material.DARK_OAK_SIGN);
        ItemStack bars = new ItemStack(Material.IRON_BARS);
        ItemStack blue_pain = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        ItemStack lime_pain = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemStack red_pain = new ItemStack(Material.RED_STAINED_GLASS_PANE);

        ArrayList<String> lore = new ArrayList();

        lore.add(ChatColor.YELLOW + "Цена: " + ChatColor.GOLD + money + " " + currencyName);

        ItemStack green_terracotta = ItemCreator.createItem(Material.LIME_TERRACOTTA, 1, (byte) 0, ChatColor.GREEN + "Принять", lore, null);

        ItemStack red_terracotta = ItemCreator.createItem(Material.RED_TERRACOTTA, 1, (byte) 0, ChatColor.RED + "Отказать", lore, null);


        tradeGui.addItem(0, sign, null);
        tradeGui.addItem(8, sign, null);
        for (int i = 1; i < 8; i++){
            tradeGui.addItem(i, bars, null);
        }

        for (int i = 9; i < 37; i += 9) {
            tradeGui.addItem(i, chain, null);
            tradeGui.addItem(i - 1, chain, null);
        }

        tradeGui.addItem(44, chain, null);

        tradeGui.addItem(45, lantern, null);
        tradeGui.addItem(53, lantern, null);


        for (int i = 1; i <= 3; i++) {
            for (int y = i * 9; y <= i * 9 + 7; y++) {
                if (y == 22) {
                    continue;
                }
                tradeGui.addItem(y, blue_pain, null);
            }
        }

        tradeGui.addItem(39, lime_pain, null);
        tradeGui.addItem(38, lime_pain, null);
        tradeGui.addItem(47, lime_pain, null);
        tradeGui.addItem(48, green_terracotta, null);
        tradeGui.addLeftClick(green_terracotta, this::acceptTrade);


        tradeGui.addItem(41, red_pain, null);
        tradeGui.addItem(42, red_pain, null);
        tradeGui.addItem(51, red_pain, null);
        tradeGui.addItem(50, red_terracotta, null);
        tradeGui.addLeftClick(red_terracotta, this::cancelTrade);


        tradeGui.addItem(22, tradeItem, null);

        tradeGui.openInv(Bukkit.getPlayer(player ));



        activeTrades.put(Bukkit.getPlayer(player), p);
        activeItemTrade.put(p, tradeItem);
        activeValuteTrades.put(p, currencyName);
        activeCostTrades.put(p, money);

        plugin.addListener(tradeGui);

        ItemStack air = new ItemStack(Material.AIR);
        p.getInventory().setItem(p.getInventory().getHeldItemSlot(), air);
    }


    private Object cancelTrade(Object obj) {
        Player p = (Player) obj;
        if (!activeTrades.containsKey(p)) {
            return p;
        }
        Player trader = activeTrades.get(p);

        trader.sendMessage(ChatColor.GOLD + "Ваше предложение о сделке отклонили");
        p.sendMessage(ChatColor.GOLD + "Вы отклонили предложение от игрока " + ChatColor.BLUE + trader.getName());

        trader.getInventory().addItem(activeItemTrade.get(trader));

        activeTrades.remove(p);
        activeItemTrade.remove(trader);
        activeCostTrades.remove(trader);
        activeValuteTrades.remove(trader);

        p.closeInventory();
        return p;
    }


    private Object acceptTrade(Object obj) {
        Player p = (Player) obj;
        if (!activeTrades.containsKey(p)) {
            return obj;
        }
        Player trader = activeTrades.get(p);

        String currencyName = activeValuteTrades.get(trader);

        Integer moneys = plugin.connect.getPlayerMoney(currencyName, p.getName());
        if (activeCostTrades.get(trader) > moneys) {
            p.sendMessage(ChatColor.RED + "У вас недостаточно денег");
            cancelTrade(p);
            return obj;
        }

        plugin.connect.transfer(p.getName(), trader.getName(), activeCostTrades.get(trader), currencyName);
        p.sendMessage(ChatColor.GREEN + "С вас было списано " + activeCostTrades.get(trader) + " " + currencyName);
        trader.sendMessage(ChatColor.GREEN + "Вам было зачисленно " + activeCostTrades.get(trader) + " " + currencyName);


        p.getInventory().addItem(activeItemTrade.get(trader));
        activeTrades.remove(p);
        activeItemTrade.remove(trader);
        activeCostTrades.remove(trader);
        activeValuteTrades.remove(trader);

        MoneyEventFull moneyEventFull = new MoneyEventFull(p.getName(), trader.getName(), "trade", moneys, currencyName, currencyName, 0, 0);
        Bukkit.getPluginManager().callEvent(moneyEventFull);
        plugin.restartNotUsed(currencyName);

        p.closeInventory();


        return obj;
    }


}
