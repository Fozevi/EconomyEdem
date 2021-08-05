package com.fozevi.economyedem.Shop;

import com.fozevi.economyedem.EconomyEdem;

public class ShopMain {

    EconomyEdem plugin = EconomyEdem.getInstance;

    public ShopMain() {
        plugin.getCommand("trade").setExecutor(new TradeCommand());
    }

}
