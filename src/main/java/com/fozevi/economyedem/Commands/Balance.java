package com.fozevi.economyedem.Commands;

import com.fozevi.economyedem.EconomyEdem;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Balance implements CommandExecutor {

    EconomyEdem plugin = EconomyEdem.getInstance;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Эту команду можно использовать только от имени игрока");
            return true;
        }

        Player p = (Player) sender;

        ArrayList<String> tables = plugin.connect.getCurrencies();
        System.out.println(tables);
        p.sendMessage(ChatColor.GREEN + "~~~~~~~~~~~"+ ChatColor.AQUA + "BALANCE" + ChatColor.GREEN + "~~~~~~~~~~~~");
        for (String table: tables) {
            p.sendMessage(ChatColor.GOLD + table + ChatColor.GREEN + " - " + ChatColor.BLUE + plugin.connect.getPlayerMoney(table, p.getName()));
        }
        p.sendMessage(ChatColor.GREEN + "~~~~~~~~~~~"+ ChatColor.AQUA + "BALANCE" + ChatColor.GREEN + "~~~~~~~~~~~~");

        return true;
    }
}
