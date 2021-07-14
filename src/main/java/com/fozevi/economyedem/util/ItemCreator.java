package com.fozevi.economyedem.util;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;

public class ItemCreator {

    public static ItemStack createItem(Material material, int amount, byte data, String displayName, ArrayList lore, Color color) {
        ItemStack item = new ItemStack(material, amount, data);
        ItemMeta meta;
        meta = item.getItemMeta();

        if (displayName != null) {
            meta.setDisplayName(displayName);
        }
        if (color != null) {
            ((LeatherArmorMeta) meta).setColor(color);
        }

        if (lore != null) {
            meta.setLore(lore);
        }
        try {
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        } catch (Exception e) {

        }

        item.setItemMeta(meta);
        return item;
    }
}
