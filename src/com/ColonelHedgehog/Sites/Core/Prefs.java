/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ColonelHedgehog.Sites.Core;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

/**
 * @author Robert
 */
public class Prefs
{
    private static ConstructionSites plugin = ConstructionSites.plugin;

    public static double ac = 0.25;
    public static int bsox = 2;
    public static int bsoy = 2;
    public static int bsoz = 2;
    public static boolean particles = false;
    public static boolean ve = true;
    public static int vto = 100;

    public static void configTasks()
    {
        FileConfiguration c = plugin.getConfig();

        // Defaults.

        File file = new File(plugin.getDataFolder() + File.separator + "config.yml");

        if (!file.exists())
        {

            plugin.getLogger().info("Generating config file for CS...");

            plugin.getConfig().options().copyDefaults(true);
            plugin.saveConfig();
            plugin.getLogger().info("Done.");

        }

        // Variable setting.
        ac = c.getDouble("CS.Advance");
        bsox = c.getInt("CS.Prefs.Pasting.Offset.X");
        bsoy = c.getInt("CS.Prefs.Pasting.Offset.Y");
        bsoz = c.getInt("CS.Prefs.Pasting.Offset.Z");
        ve = c.getBoolean("CS.Prefs.ScanColors.Enabled");
        vto = c.getInt("CS.Prefs.ScanColors.Timeout");
        particles = c.getBoolean("CS.Prefs.PrePasteParticles");
    }
}
