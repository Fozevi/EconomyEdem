package com.fozevi.economyedem.Commands;

import com.fozevi.economyedem.EconomyEdem;
import com.fozevi.economyedem.Events.MoneyEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Conversion implements CommandExecutor {


    EconomyEdem plugin = EconomyEdem.getInstance;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Эту команду можно использовать только от имени игрока");
            return true;
        }

        Player p = (Player) sender;
        if (args.length < 3) {
            p.sendMessage(ChatColor.GOLD + "Правильное использование команды: /convert <из какой валюты> <в какую валюту> <значение>");
            return true;
        }

        String currency1 = args[0];
        String currency2 = args[1];
        Integer value;
        try {
            value = Integer.parseInt(args[2]);
        } catch (Exception e) {
            p.sendMessage(ChatColor.GOLD + "Правильное использование команды: /convert <из какой валюты> <в какую валюту> <значение>");
            return true;
        }

        String msg = convert(p.getName(), currency1, currency2, value);
        if (msg != "") {
            p.sendMessage(msg);
        }
        return true;
    }


    private String convert(String player, String currency1, String currency2, Integer value) {
        Integer moneyPlayer = plugin.connect.getPlayerMoney(currency1, player);
        if (moneyPlayer >= value) {
            MoneyEvent moneyEvent = new MoneyEvent(player, player, "convert", value, currency1, currency2);
            Bukkit.getPluginManager().callEvent(moneyEvent);
            return ChatColor.GREEN + String.valueOf(value) + " " + currency1 + " было конвентировано в " + currency2;
        }
        return "";
    }
}
