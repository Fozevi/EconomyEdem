package com.fozevi.economyedem.Curse;

import com.fozevi.economyedem.EconomyEdem;
import com.fozevi.economyedem.Events.MoneyEventFull;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class NotUsed {

    static EconomyEdem plugin = EconomyEdem.getInstance;



    public static void startTimer(String currency) {
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                startDecrease(currency);
                plugin.notUsed.remove(currency);
            }
        }.runTaskLater(plugin, 20 * 60 * 60 * 24);

        plugin.notUsed.put(currency, task);
    }


    private static void startDecrease(String currency) {
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                Integer cost = plugin.connect.getCost(currency);
                Integer newCost = (int) Math.ceil(cost - cost * 0.05);
                if (newCost <= 0) {
                    plugin.connect.changeCost(currency, 1);
                    MoneyEventFull moneyEventFull = new MoneyEventFull("system", "system", "decrease", 0, currency, currency, 0, cost - 1);
                    Bukkit.getPluginManager().callEvent(moneyEventFull);
                } else {
                    MoneyEventFull moneyEventFull = new MoneyEventFull("system", "system", "decrease", 0, currency, currency, 0, (int) ( cost * 0.05));
                    Bukkit.getPluginManager().callEvent(moneyEventFull);
                    plugin.connect.changeCost(currency, newCost);
                }
            }
        }.runTaskTimer(plugin, 0, 20 * 60 * 60);
        plugin.startDecreased.put(currency, task);
    }
}
