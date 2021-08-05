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

        if (tables.size() == 0) {
            p.sendMessage(ChatColor.YELLOW + "У вас нет никаких денег :(");
            return true;
        }
        p.sendMessage(ChatColor.LIGHT_PURPLE + "Ваш баланс: ");
        for (String table: tables) {
            p.sendMessage(ChatColor.YELLOW + table + ChatColor.WHITE + " - " + ChatColor.AQUA + plugin.connect.getPlayerMoney(table, p.getName()));
        }

        return true;
    }
}
