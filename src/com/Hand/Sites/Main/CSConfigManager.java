/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.Hand.Sites.Main;

import com.Hand.Sites.Commands.ConstructCmd;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author Robert
 */
public class CSConfigManager 
{
    public static Main plugin = Main.plugin;

    public static void resumeBuildProcesses() 
    {
        FileConfiguration config = plugin.getConfig();
        
        if(config.getList("Processes") != null && !config.getList("Processes").isEmpty())
        {
            for (Object process : config.getList("Processes")) 
            {
                String  title = process.toString();
                String[] split = title.split(",");
                //System.out.println("Resuming for coords & world: " + split[0] + " - " + split[1] + ", " + split[2] + ", " + split[3] + ".");

                String worldname = split[0];
                                
                int x = Integer.parseInt(split[1]);
                int y = Integer.parseInt(split[2]);
                int z = Integer.parseInt(split[3]);
                Location loc = new Location(Bukkit.getWorld(worldname), x, y, z);
                ConstructCmd.signCountDown(loc);
            }
        }
    }
}
