/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.Hand.Sites.Commands;

import com.Hand.Sites.Main.Main;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

/**
 *
 * @author Robert
 */
public class ConstructTabComplete implements TabCompleter 
{
    public static Main plugin = Main.plugin;
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {

    if(cmd.getName().equalsIgnoreCase("construct") && args.length >= 2 && args[0].equalsIgnoreCase("build")) 
    {
        if(sender instanceof Player)
        {
            Player p = (Player) sender;
            
            List<String> list = (List<String>) plugin.getConfig().getList("CS.Names");
            if(list == null)
            {
                List<String> empty = new ArrayList<>();
                list = empty;
            }
            
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

    return null;
    }
    
}
