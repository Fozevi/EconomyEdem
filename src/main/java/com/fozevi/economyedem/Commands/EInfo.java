package com.fozevi.economyedem.Commands;

import com.fozevi.economyedem.EconomyEdem;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class EInfo implements CommandExecutor {

    EconomyEdem plugin = EconomyEdem.getInstance;



    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        sender.sendMessage(ChatColor.DARK_PURPLE + "ID валют: ");

        for (Map.Entry entry: plugin.connect.getCurrenciesId().entrySet()) {
            sender.sendMessage(ChatColor.YELLOW + String.valueOf(entry.getKey()) + ChatColor.WHITE + " - " + ChatColor.AQUA + entry.getValue());
        }

        return true;
    }
}
