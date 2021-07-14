package com.fozevi.economyedem.machine;

import com.fozevi.economyedem.EconomyEdem;
import com.fozevi.economyedem.Events.MoneyEvent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class machineEvent {

    private static EconomyEdem plugin = EconomyEdem.getInstance;

    public static BukkitTask start(Integer minutes, Integer money, String currency) {
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
               MoneyEvent moneyEvent = new MoneyEvent("станок", "овнеры", "generation", money, currency, currency);
               Bukkit.getPluginManager().callEvent(moneyEvent);
            }
        }.runTaskTimer(plugin, 20 * 60 * minutes, 20 * 60 * minutes);

        return task;
    }

}
