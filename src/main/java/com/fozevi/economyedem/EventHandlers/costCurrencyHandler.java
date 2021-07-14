package com.fozevi.economyedem.EventHandlers;

import com.fozevi.economyedem.EconomyEdem;
import com.fozevi.economyedem.Events.MoneyEvent;
import com.fozevi.economyedem.Events.MoneyEventFull;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import java.util.ArrayList;

public class costCurrencyHandler implements Listener {

    EconomyEdem plugin = EconomyEdem.getInstance;

    @EventHandler
    public void onMoneyChange(MoneyEvent event) {
        if (event.getAction().equalsIgnoreCase("generation")) {
            Integer value = event.getValue();
            Integer cost = plugin.connect.getCost(event.getCurrency1());
            Double costDown = -1.0;
            Integer newCost = 0;

            if (value <= plugin.getConfig().getInt("settings.machines.costDown.1.max") && plugin.getConfig().getInt("settings.machines.costDown.1.min") <= value) {
                costDown = (value * plugin.getConfig().getDouble("settings.machines.costDown.1.value"));
            } else if (value <= plugin.getConfig().getInt("settings.machines.costDown.2.max") && plugin.getConfig().getInt("settings.machines.costDown.2.min") <= value) {
                costDown = (value * plugin.getConfig().getDouble("settings.machines.costDown.2.value"));
            } else if (value <= plugin.getConfig().getInt("settings.machines.costDown.3.max") && plugin.getConfig().getInt("settings.machines.costDown.3.min") <= value) {
                costDown = (value * plugin.getConfig().getDouble("settings.machines.costDown.3.value"));
            } else if (value <= plugin.getConfig().getInt("settings.machines.costDown.4.max") && plugin.getConfig().getInt("settings.machines.costDown.4.min") <= value) {
                costDown = (value * plugin.getConfig().getDouble("settings.machines.costDown.4.value"));
            } else if (value <= plugin.getConfig().getInt("settings.machines.costDown.set.max") && plugin.getConfig().getInt("settings.machines.costDown.set.min") <= value) {
                newCost = plugin.getConfig().getInt("settings.machines.costDown.set.value");
            }
            if (costDown != -1.0) {
                newCost = (int) Math.round(cost - Math.ceil(costDown));
            }
            plugin.connect.changeCost(event.getCurrency1(), newCost);
            Integer value3 = Math.round(value / 3);
            ArrayList<String> owners = plugin.connect.getOwners(event.getCurrency1());
            for (String owner: owners) {
                plugin.connect.giveMoney(event.getCurrency1(), owner, value3);
            }
            MoneyEventFull moneyEventFull = new MoneyEventFull(event.getPlayer1(), event.getPlayer2(), event.getAction(), value, event.getCurrency1(), event.getCurrency2(), 0, (int) Math.ceil(costDown));
            Bukkit.getPluginManager().callEvent(moneyEventFull);
        } else if (event.getAction().equalsIgnoreCase("convert")) {
            Integer value = event.getValue();
            String currency1 = event.getCurrency1();
            String currency2 = event.getCurrency2();
            Integer cost1 = plugin.connect.getCost(currency1);
            Integer cost2 = plugin.connect.getCost(currency2);
            String player = event.getPlayer1();

            Integer moneys1 = plugin.connect.getMoneysInCurrency(currency1);
            Integer moneys2 = plugin.connect.getMoneysInCurrency(currency2);

            float percent1 = (float) value / (float) moneys1;
            Integer cost1Down = (int) Math.ceil(cost1 * percent1);
            Integer newCost1 = cost1 - cost1Down;

            Integer moneyIn2 = Math.round((float) value * ((float) cost1 / (float) cost2));
            float percent2 = (float) moneyIn2 / (float) moneys2;
            Integer cost2Up = (int) Math.ceil(cost2 * percent2);
            Integer newCost2 = cost2 + cost2Up;

            plugin.connect.changeCost(currency1, newCost1);
            plugin.connect.changeCost(currency2, newCost2);
            plugin.connect.removeMoneyFromPlayer(player, currency1, value);
            plugin.connect.giveMoney(currency2, player, moneyIn2);

            MoneyEventFull moneyEventFull = new MoneyEventFull(player, player, "convert", value, currency1, currency2, cost2Up, cost1Down);
            Bukkit.getPluginManager().callEvent(moneyEventFull);

            plugin.restartNotUsed(currency2);
            plugin.stopDecrease(currency2);
        }
    }

}
