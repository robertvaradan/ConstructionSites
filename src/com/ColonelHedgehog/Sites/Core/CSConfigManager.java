/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ColonelHedgehog.Sites.Core;

import com.ColonelHedgehog.Sites.Commands.ConstructCmd;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.UUID;

/**
 * @author Robert
 */
public class CSConfigManager
{
    private static ConstructionSites plugin = ConstructionSites.plugin;

    // Resumes all paused build processes.
    public static void resumeBuildProcesses()
    {
        cleanProcesses();
        FileConfiguration config = plugin.getConfig();
        ArrayList<String> removelist = new ArrayList<>();

        if (config.getList("Processes") != null && !config.getList("Processes").isEmpty())
        {
            for (Object process : config.getList("Processes"))
            {
                String title = process.toString();
                String[] split = title.split(",");
                //System.out.println("Resuming for coords & world: " + split[0] + " - " + split[1] + ", " + split[2] + ", " + split[3] + ".");

                try
                {
                    String worldname = split[0];


                    int x = Integer.parseInt(split[1]);
                    int y = Integer.parseInt(split[2]);
                    int z = Integer.parseInt(split[3]);
                    Location loc = new Location(Bukkit.getWorld(worldname), x, y, z);
                    //System.out.println("MESSAGE FOR YOU! " + split[4]);

                    ConstructCmd.signCountDown(loc, UUID.fromString(split[4]));
                }
                catch (ArrayIndexOutOfBoundsException | NumberFormatException e)
                {
                    removelist.add(title);
                }
            }
        }
        for (String s : removelist)
        {
            config.getList("Processes").remove(s);
            plugin.saveConfig();
        }
    }

    // Removes unused processes from config.
    public static void cleanProcesses()
    {
        //System.out.println("Cleaning processes.");
        FileConfiguration config = plugin.getConfig();
        ArrayList<String> removelist = new ArrayList<>();

        if (config.getList("Processes") != null && !config.getList("Processes").isEmpty())
        {
            //System.out.println("Processes are not null!");
            for (Object process : config.getList("Processes"))
            {
                String title = process.toString();
                String[] split = title.split(",");
                //System.out.println("Cleaning for coords & world & UUID: " + split[0] + " - " + split[1] + ", " + split[2] + ", " + split[3] + ".");

                String worldname = split[0];

                int x = Integer.parseInt(split[1]);
                int y = Integer.parseInt(split[2]);
                int z = Integer.parseInt(split[3]);
                Location loc = new Location(Bukkit.getWorld(worldname), x, y, z);

                if ((loc.getBlock().getType() == Material.SIGN || loc.getBlock().getType() == Material.SIGN_POST || loc.getBlock().getType() == Material.WALL_SIGN) && !((Sign) loc.getBlock().getState()).getLine(2).contains("§aComplete"))
                {

                }
                else
                {
                    removelist.add(title);
                }
            }
        }

        for (String s : removelist)
        {
            config.getList("Processes").remove(s);
            plugin.saveConfig();
        }

    }
}
