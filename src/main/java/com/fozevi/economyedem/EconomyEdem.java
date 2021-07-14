package com.fozevi.economyedem;

import com.fozevi.economyedem.Commands.*;
import com.fozevi.economyedem.Curse.NotUsed;
import com.fozevi.economyedem.EventHandlers.MachineHandler;
import com.fozevi.economyedem.EventHandlers.costCurrencyHandler;
import com.fozevi.economyedem.machine.machineEvent;
import com.fozevi.economyedem.mysql.Connect;
import com.fozevi.economyedem.util.logs;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public final class EconomyEdem extends JavaPlugin {

    public static EconomyEdem getInstance;
    public Connect connect;
    public getMoneyCrafter getMoneyCrafter;
    public HashMap<String, BukkitTask> machinesTasks = new HashMap<>();

    public HashMap<String, BukkitTask> notUsed = new HashMap<>();

    public HashMap<String, BukkitTask> startDecreased = new HashMap<>();

    @Override
    public void onEnable() {
        instanceClasses();
        File config = new File(getDataFolder() + File.separator + "config.yml");
        if (!config.exists()) {
            loadConfig();
        }
        try {
            getConfig().load(config);
        } catch (IOException exception) {
            exception.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

        connect = new Connect();
        connect.mysqlSetup();



        getCommand("ecreate").setExecutor(new ECreate());
        getCommand("transfer").setExecutor(new transfer());
        getCommand("balance").setExecutor(new Balance());
        addListener(new costCurrencyHandler());
        addListener(new MachineHandler());
        addListener(new logs());
        getMoneyCrafter = new getMoneyCrafter();
        getCommand("getcrafters").setExecutor(getMoneyCrafter);
        getCommand("convert").setExecutor(new Conversion());

        startMachines();
        startNotUsed();
    }

    private void startMachines() {
        ArrayList<HashMap<String, Object>> machines = connect.getMachines();
        for (HashMap<String, Object> machine: machines) {
            if ((boolean) machine.get("machine1")) {
                BukkitTask task = machineEvent.start((Integer) machine.get("time1"), (Integer) machine.get("money1"), String.valueOf(machine.get("currency")));
                machinesTasks.put(String.valueOf(machine.get("currency")), task);
            }
        }
    }

    private void startNotUsed() {
        ArrayList<String> currencies = connect.getCurrencies();
        for (String currency: currencies) {
            NotUsed.startTimer(currency);
        }
    }


    public void restartNotUsed(String currency) {
        if (notUsed.containsKey(currency)) {
            BukkitTask task = notUsed.get(currency);
            task.cancel();
        }
        NotUsed.startTimer(currency);
    }

    public void stopDecrease(String currency) {
        if (startDecreased.containsKey(currency)) {
            BukkitTask task = startDecreased.get(currency);
            task.cancel();
        }
    }


    public void updateMachine(String currency) {
        if (machinesTasks.containsKey(currency)) {
            BukkitTask task = machinesTasks.get(currency);
            task.cancel();
            machinesTasks.remove(currency);
        }
        HashMap<String, Object> machine = connect.getMachine(currency);
        BukkitTask newTask = machineEvent.start((Integer) machine.get("time1"), (Integer) machine.get("money1"), String.valueOf(machine.get("currency")));
        machinesTasks.put(currency, newTask);
    }


    public void addListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, EconomyEdem.getInstance);
    }

    public void removeListener(Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    public void loadConfig(){
        getConfig().options().copyDefaults(true);
        saveConfig();
    }




    private void instanceClasses(){
        getInstance = this;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
