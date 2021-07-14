package com.fozevi.economyedem.util;

import com.fozevi.economyedem.EconomyEdem;
import com.fozevi.economyedem.Events.MoneyEventFull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class logs implements Listener {

    EconomyEdem plugin = EconomyEdem.getInstance;

    @EventHandler
    public void onMoneyEvent(MoneyEventFull eventFull) {
        plugin.connect.addLog(eventFull.getPlayer1(), eventFull.getPlayer2(), eventFull.getAction(), eventFull.getValue(), eventFull.getCurrency1(), eventFull.getCurrency2(), eventFull.getCost2Up(), eventFull.getCost1Down());

    }

}
