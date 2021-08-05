package com.fozevi.economyedem.Shop;

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

import java.util.ArrayList;
import java.util.HashMap;

public class TargetShopCommand implements CommandExecutor {



    EconomyEdem plugin = EconomyEdem.getInstance;



    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        TargetShop targetShop = TargetShop.getInstance;
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Эту команду можно использовать только от имени игрока");
            return true;
        }

        Player p = (Player) sender;

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("additem")) {
                if (args.length >= 3) {
                    Integer money;
                    try {
                        money = Integer.parseInt(args[1]);
                    } catch (Exception e) {
                        p.sendMessage(ChatColor.YELLOW + "/shop additem <цена>");
                        return true;
                    }

                    if (targetShop.shops.containsKey(p)) {
                        HashMap<ItemStack, Integer> items = targetShop.shops.get(p);
                        items.put(p.getItemInHand(), money);
                        targetShop.shops.replace(p, items);
                    } else {
                        HashMap<ItemStack, Integer> items = new HashMap<>();
                        items.put(p.getItemInHand(), money);
                        targetShop.shops.put(p, items);
                    }

                }
            } else if (args[0].equalsIgnoreCase("openshop")) {
                if (args.length >= 2) {
                    String shopName = args[1];
                    System.out.println(targetShop);
                    if (targetShop.shops.containsKey(p)) {
                        GUI tradeGui = new GUI();
                        tradeGui.createMenu(9 * 6, ChatColor.DARK_PURPLE + "Магазин " + shopName, null);
                        ItemStack painting = new ItemStack(Material.PAINTING);
                        ItemStack vines = new ItemStack(Material.TWISTING_VINES);
                        ItemStack blue_pane = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
                        ItemStack gray_pane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                        ItemStack purple_pane = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);

                        ItemStack green_terracotta = ItemCreator.createItem(Material.LIME_TERRACOTTA, 1, (byte) 0, ChatColor.GREEN + "Принять", null, null);

                        tradeGui.addItem(0, painting, null);
                        tradeGui.addItem(8, painting, null);
                        for (int i = 1; i < 8; i++){
                            tradeGui.addItem(i, gray_pane, null);
                        }

                        for (int i = 9; i < 37; i += 9) {
                            tradeGui.addItem(i, vines, null);
                            tradeGui.addItem(i - 1, vines, null);
                        }

                        tradeGui.addItem(44, vines, null);



                        for (int i = 1; i <= 3; i++) {
                            for (int y = i * 9; y <= i * 9 + 7; y++) {
                                if (y == 22) {
                                    continue;
                                }
                                tradeGui.addItem(y, blue_pane, null);
                            }
                        }
                        tradeGui.openInv(p);
                        plugin.addListener(tradeGui);
                    }
                }


            }
        }

        return true;
    }
}
