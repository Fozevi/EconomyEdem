package com.fozevi.economyedem.Shop;

import com.fozevi.economyedem.EconomyEdem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class TargetShop {

    public HashMap<Player, HashMap<ItemStack, Integer>> shops = new HashMap<>();

    public static TargetShop getInstance;

    EconomyEdem plugin = EconomyEdem.getInstance;


    public TargetShop() {

        plugin.getCommand("shop").setExecutor(new TargetShopCommand());

        setInstance();
    }

    public void setInstance() {
        getInstance = this;
    }
}
