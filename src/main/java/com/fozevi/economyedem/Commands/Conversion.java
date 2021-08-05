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
            p.sendMessage(ChatColor.GOLD + "Правильное использование команды: /convert <из какой валюты (id)> <в какую валюту (id)> <значение>");
            return true;
        }

        Integer currency1Id;

        try {
            currency1Id = Integer.parseInt(args[0]);
        } catch (Exception e) {
            p.sendMessage(ChatColor.GOLD + "Правильное использование команды: /convert <из какой валюты (id)> <в какую валюту (id)> <значение>");
            return true;
        }

        Integer currency2Id;

        try {
            currency2Id = Integer.parseInt(args[1]);
        } catch (Exception e) {
            p.sendMessage(ChatColor.GOLD + "Правильное использование команды: /convert <из какой валюты (id)> <в какую валюту (id)> <значение>");
            return true;
        }
        Integer value;
        try {
            value = Integer.parseInt(args[2]);
        } catch (Exception e) {
            p.sendMessage(ChatColor.GOLD + "Правильное использование команды: /convert <из какой валюты (id)> <в какую валюту (id)> <значение>");
            return true;
        }

        String currency1 = plugin.connect.getValuteById(currency1Id);
        String currency2 = plugin.connect.getValuteById(currency2Id);

        if (currency1 == null) {
            p.sendMessage(ChatColor.RED + "Валюты с ID " + currency1Id + " не существует");
            return true;
        }
        if (currency2 == null) {
            p.sendMessage(ChatColor.RED + "Валюты с ID " + currency2Id + " не существует");
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
        } else {
            return ChatColor.RED + "На вашем счету нет " + value + " " + currency1;
        }
    }
}
