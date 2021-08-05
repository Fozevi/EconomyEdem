package com.fozevi.economyedem.Commands;

import com.fozevi.economyedem.EconomyEdem;
import com.fozevi.economyedem.Events.MoneyEventFull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class transfer implements CommandExecutor {


    EconomyEdem plugin = EconomyEdem.getInstance;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Эту команду можно использовать только от имени игрока");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Правильное использование команды: /transfer <название валюты> <количество коинов> <кому перевести>");
            return true;
        }

        Player p = (Player) sender;

        Integer currencyId;
        try {
            currencyId = Integer.parseInt(args[0]);
        }catch (Exception e) {
            p.sendMessage(ChatColor.RED + "Правильное использование команды: /transfer <id валюты> <количество коинов> <кому перевести>");
            return true;
        }

        String currencyName = plugin.connect.getValuteById(currencyId);

        if (currencyName == null) {
            p.sendMessage(ChatColor.RED + "Валюты с ID " + currencyId + " не существует");
            return true;
        }

        Integer value;
        try {
            value = Integer.parseInt(args[1]);
        }catch (Exception e) {
            p.sendMessage(ChatColor.RED + "Правильное использование команды: /transfer <id валюты> <количество коинов> <кому перевести>");
            return true;
        }
        String nick = args[2];


        boolean trans = plugin.connect.transfer(p.getName(), nick, value, currencyName);

        if (trans) {
            MoneyEventFull moneyEvent = new MoneyEventFull(p.getName(), nick, "transfer", value, currencyName, currencyName, 0, 0);
            Bukkit.getPluginManager().callEvent(moneyEvent);
            plugin.stopDecrease(currencyName);
            plugin.restartNotUsed(currencyName);
            p.sendMessage(ChatColor.GREEN + "Перевод успешно выполнен");
        } else {
            p.sendMessage(ChatColor.GOLD + "Не удалось выполнить перевод");
        }

        return true;
    }
}
