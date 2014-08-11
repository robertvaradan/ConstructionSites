/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.Hand.Sites.Main;

import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author Robert
 */
public class Prefs 
{
    public static Main plugin = Main.plugin;

    public static double ac;
    public static int bsox;
    public static int bsoy;
    public static int bsoz;
    
    public static void configTasks()
    {
        FileConfiguration c = plugin.getConfig();
        
        // Defaults. 
        c.addDefault("CS.Prefs.Advance", 0.25);

        c.addDefault("CS.Prefs.Pasting.Offset.X", 2);
        c.addDefault("CS.Prefs.Pasting.Offset.Y", 2);
        c.addDefault("CS.Prefs.Pasting.Offset.Z", 2);
        
        File file = new File(plugin.getDataFolder() + File.separator + "config.yml");
        
        if(!file.exists())
        {

        plugin.getLogger().info("Generating config file for CS...");

        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();
        plugin.getLogger().info("Done.");

        }

        // Variable setting.
        ac = c.getDouble("CS.Prefs.Advance");
        bsox = c.getInt("CS.Prefs.Pasting.Offset.X");
        bsoy = c.getInt("CS.Prefs.Pasting.Offset.Y");
        bsoz = c.getInt("CS.Prefs.Pasting.Offset.Z");
    }
}
