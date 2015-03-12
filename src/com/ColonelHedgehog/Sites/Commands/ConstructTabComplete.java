/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ColonelHedgehog.Sites.Commands;

import com.ColonelHedgehog.Sites.Core.ConstructionSites;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Robert
 */
public class ConstructTabComplete implements TabCompleter 
{
    private static ConstructionSites plugin = ConstructionSites.plugin;
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {

    if(cmd.getName().equalsIgnoreCase("construct") && args.length >= 2) 
    {
        if(args[0].equalsIgnoreCase("build"))
        {
            if(sender instanceof Player)
            {
                Player p = (Player) sender;

                Set<String> list = plugin.getConfig().getConfigurationSection("CS").getKeys(false);
                list.remove("Prefs");
                list.remove("Names");

                
                List<String> newList = new ArrayList<>();
                
                for (String site : list)
                {
                    if(ConstructCmd.getAllowedBuildSite(p, site))
                    {
                        newList.add(site);
                    }
                }
                
                return newList;
            }
        }
        else if(args[0].equalsIgnoreCase("admin"))
        {
            if(args[1].equalsIgnoreCase("addsite"))
            {
                File f = new File(plugin.getDataFolder().getParent() + "/WorldEdit/schematics");
                
                List<String> list = new ArrayList<>();

                File[] files = f.listFiles();
                if (files != null)
                {
                    for (File file : files)
                    {
                        if(args.length > 2)
                        {
                            if (file.getName().startsWith(args[2]))
                            {
                                list.add(file.getName().replace(".schematic", ""));
                            }
                        }
                        else
                        {
                            list.add(file.getName().replace(".schematic", ""));
                        }
                    }
                    return list;
                }
            }
        }
    }

    return null;
    }
    
}
