package com.fozevi.economyedem.Holo;

import com.fozevi.economyedem.EconomyEdem;
import com.fozevi.economyedem.Holo.Commands.HoloCreate;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.List;
import java.util.Set;

public class Holographic {

    EconomyEdem plugin = EconomyEdem.getInstance;
    HoloCreate holoCreate = new HoloCreate();

    public Holographic() {
        if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            plugin.getLogger().severe("Плагин HolographicDisplays не работает, поэтому команды для создания голограмм не будут рабочими");
            return;
        }
        plugin.getCommand("eholo").setExecutor(holoCreate);
    }

    public void init() {
        if (!plugin.getHoloCfg().isConfigurationSection("holograms")) {
            return;
        }
        Set<String> holograms = plugin.getHoloCfg().getConfigurationSection("holograms").getKeys(false);

        for (String name: holograms) {
            String worldName = plugin.getHoloCfg().getString("holograms." + name + ".world");
            double x = plugin.getHoloCfg().getDouble("holograms." + name + ".x");
            double y = plugin.getHoloCfg().getDouble("holograms." + name + ".y");
            double z = plugin.getHoloCfg().getDouble("holograms." + name + ".z");
            float pitch = (float) plugin.getHoloCfg().getDouble("holograms." + name + ".pitch");
            float yaw = (float) plugin.getHoloCfg().getDouble("holograms." + name + ".yaw");

            Location location = new Location(Bukkit.getWorld(worldName), x, y, z, pitch, yaw);
            holoCreate.create(name, location);

        }

    }
}
