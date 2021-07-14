package com.fozevi.economyedem.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MachineUpdateEvent extends Event {

    public static final HandlerList handlers = new HandlerList();
    private String currency;


    public MachineUpdateEvent(String currency) {
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
