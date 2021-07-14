package com.fozevi.economyedem.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MoneyEventFull extends Event {

    public static final HandlerList handlers = new HandlerList();
    private String player1;
    private String player2;
    private String action;
    private Integer value;
    private String currency1;
    private String currency2;
    private Integer cost2Up;
    private Integer cost1Down;
    private boolean cancelled;

    public MoneyEventFull(String player1, String player2, String action, Integer value, String currency1, String currency2, Integer cost2Up, Integer cost1Down) {
        this.player1 = player1;
        this.player2 = player2;
        this.action = action;
        this.value = value;
        this.currency1 = currency1;
        this.currency2 = currency2;
        this.cost2Up = cost2Up;
        this.cost1Down = cost1Down;
    }

    public String getPlayer1() {
        return player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public String getAction() {
        return action;
    }

    public Integer getValue() {
        return value;
    }

    public String getCurrency1() {
        return currency1;
    }

    public String getCurrency2() {
        return currency2;
    }

    public Integer getCost2Up() {
        return cost2Up;
    }

    public Integer getCost1Down() {
        return cost1Down;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
