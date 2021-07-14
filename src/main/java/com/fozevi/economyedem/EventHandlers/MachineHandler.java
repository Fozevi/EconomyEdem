package com.fozevi.economyedem.EventHandlers;

import com.fozevi.economyedem.EconomyEdem;
import com.fozevi.economyedem.Events.MachineUpdateEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MachineHandler implements Listener {

    EconomyEdem plugin = EconomyEdem.getInstance;

    @EventHandler
    public void onMachineUpdate(MachineUpdateEvent event) {
        plugin.updateMachine(event.getCurrency());
    }

}
