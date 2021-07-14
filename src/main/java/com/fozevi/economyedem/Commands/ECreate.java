package com.fozevi.economyedem.Commands;

import com.fozevi.economyedem.EconomyEdem;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ECreate implements CommandExecutor {

    EconomyEdem plugin = EconomyEdem.getInstance;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("economy.create")) {
            sender.sendMessage(ChatColor.RED + "Эта команда доступна только администраторам сервера");
            return true;
        }

        if (args.length < 6) {
            sender.sendMessage(ChatColor.YELLOW + "Правильное использование команды: /ecreate <название валюты> <владелец1> <владелец2> <владалец3> <начальная стоимость> <изначальная сумма денег>");
            return true;
        }

        Integer cost;
        try {
            cost = Integer.parseInt(args[4]);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Стоимость валюты должна быть в виде числа (5 аргумент)");
            return true;
        }

        Integer money;
        try {
            money = Integer.parseInt(args[5]);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Начальная сумма денег должна быть в виде числа (6 аргумент)");
            return true;
        }

        Integer cash = Math.round(money / 3);

        String currencyName = args[0];
        String owner1 = args[1];
        String owner2 = args[2];
        String owner3 = args[3];

        if (owner1.equalsIgnoreCase(owner2) || owner1.equalsIgnoreCase(owner3) || owner2.equalsIgnoreCase(owner3)) {
            sender.sendMessage(ChatColor.RED + "Все владельцы должны быть разными людьми");
            return true;
        }



        String nick = sender.getName();

        boolean create = plugin.connect.createNewCurrency(currencyName, nick, owner1, owner2, owner3, cost, cash);

        if (create) {
            sender.sendMessage(ChatColor.GREEN + "Новая валюта " + currencyName + " создана. Ее владельцы: " + ChatColor.GOLD + owner1 + ChatColor.GREEN + ", " + ChatColor.GOLD + owner2 + ChatColor.GREEN + " и " + ChatColor.GOLD + owner3 + ChatColor.GREEN + ". Ее стоимость: " + ChatColor.AQUA + cost + ChatColor.GREEN + ". Изначальная сумма: " + ChatColor.BLUE + money + ChatColor.GREEN + " была разделена между овнерами по " + ChatColor.BLUE + cash);

        } else {
            sender.sendMessage(ChatColor.RED + "По какой-то причине не удалось создать новую валюту");
        }


        return true;
    }
}
