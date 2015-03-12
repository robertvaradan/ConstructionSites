/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ColonelHedgehog.Sites.Commands;

import com.ColonelHedgehog.CSAPI.ConstructionSite;
import com.ColonelHedgehog.Sites.Core.ConstructionSites;
import com.ColonelHedgehog.Sites.Services.CSCountTask;
import com.ColonelHedgehog.Sites.Services.CommandMenu;
import com.ColonelHedgehog.Sites.Services.URLServices.DLC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.ColonelHedgehog.Sites.Core.ConstructionSites.Prefix;

/**
 *
 * @author Robert
 */
public class ConstructCmd implements CommandExecutor
{
    private static ConstructionSites plugin = ConstructionSites.plugin;
    
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) 
    {
        if(sender instanceof Player)
        {
            Set<String> list = plugin.getConfig().getConfigurationSection("CS").getKeys(false);
            list.remove("Prefs");
            list.remove("Names");
            
            final Player p = (Player) sender;
            
            if(args.length >= 1)
            {
                if(args[0].equalsIgnoreCase("admin") && p.hasPermission("csites.admin"))
                {
                    if(args.length >= 2)
                    {
                        if(args[1].equalsIgnoreCase("addsite"))
                        {
                            if(args.length >= 5)
                            {
                                if(args[3].contains(":") && args[4].contains("."))
                                {
                                    if(!list.contains(args[2].toLowerCase()))
                                    {
                                    list.add(args[2].toLowerCase());
                                    }

                                    p.sendMessage(Prefix + "§aSuccessfully added \"§b" + args[2].toLowerCase() + "§a.\" §e Cost: §a$" + args[4] + "§e. Time: §a" + args[3] + "§e.");
                                    plugin.getConfig().set("CS." + args[2].toLowerCase() + ".Time", args[3]);
                                    plugin.getConfig().set("CS." + args[2].toLowerCase() + ".Cost", Double.parseDouble(args[4].replace("$", "")));
                                    plugin.saveConfig();
                                }
                                else
                                {
                                    p.sendMessage(Prefix + "§4ERROR: §cInvalid setup! Usage: §6/construct admin addsite (site name) (hours:minutes:seconds) (dollars.cents)");
                                }
                            }
                            else
                            {
                                p.sendMessage(Prefix + "§cPlease choose a site to add. §6/construct admin addsite §4<Name> <hours:minutes:seconds> <Cost: dollars.cents>");
                            }
                        }
                        else if(args[1].equalsIgnoreCase("delsite"))
                        {
                            if(args.length >= 3)
                            {
                                if(list.contains(args[2].toLowerCase()))
                                {
                                    list.remove(args[2].toLowerCase());
                                    plugin.getConfig().set("CS." + args[2].toLowerCase(), null);
                                    plugin.saveConfig();
                                    p.sendMessage(Prefix+ "§aSuccessfully removed \"§b" + args[2] + "§a.\"");
                                }
                                else
                                {
                                    p.sendMessage(Prefix + "§cCould not remove \"§b" + args[2] + "§a.\" Site does not exist.");
                                }
                            }
                            else
                            {
                                p.sendMessage(Prefix + "§cPlease choose a site to remove. §6/construct admin delsite §4<Name>");
                            }
                        }
                    }
                    else
                    {
                        p.sendMessage(Prefix + "§cPlease choose an action.");
                    }
                }
                else if (args[0].equalsIgnoreCase("scan"))
                {
                    if(p.hasPermission("csites.scan"))
                    {
                        if(args.length > 1)
                        {
                            if (getAllowedBuildSite(p, args[1].toLowerCase()))
                            {
                                p.sendMessage(ConstructionSites.Prefix + "§aScanning for site \"§e" + args[1] + "§a.\"");
                                ConstructionSite.scanBuildArea(p.getLocation(), args[1], p);
                            }
                            else if (args[0].equalsIgnoreCase("scan"))
                            {
                                p.sendMessage(Prefix + "§cYou can't scan for this area!");
                            }
                        }
                        else
                        {
                            p.sendMessage(ConstructionSites.Prefix + "§cToo few arguments! Usage: §e/construct scan §a<site>");
                        }
                    }
                    else
                    {
                        p.sendMessage(ConstructionSites.Prefix + "§cInsufficient permissions!");
                    }
                }
                else if(args[0].equalsIgnoreCase("build"))
                {
                    if(args.length >= 2)
                    {
                        ConstructionSite s = new ConstructionSite(p.getLocation(), p.getUniqueId(), args[1].toLowerCase(), Prefix, false);
                    }
                    else
                    {
                        p.sendMessage("§ePlease specify what type of building you want to make. §eAllowed sites:");
                        for (String site : list) 
                        {
                            if(getAllowedBuildSite(p, site))
                            {
                                p.sendMessage("§6- §a" + site + "§6, §a$" + plugin.getConfig().getDouble("CS." + site + ".Cost") + "§6, §a" + plugin.getConfig().getString("CS." + site + ".Time"));
                            }
                        }

                    }
                }
                else if(args[0].equalsIgnoreCase("install"))
                {
                    if(p.hasPermission("csites.admin"))
                    {
                        if(args.length > 1)
                        {
                            File pack = new File(plugin.getDataFolder().getParent() + "/" + args[1]);

                            if(pack.exists())
                            {
                                p.sendMessage(Prefix + "Installing package...");

                                if(DLC.installPackage(pack))
                                {
                                    p.sendMessage(Prefix + "§aSuccess!");
                                }
                                else
                                {
                                    p.sendMessage(Prefix + "§cAn error occurred. Check your console for details.");
                                }
                            }
                            else
                            {
                                p.sendMessage(Prefix + "§cError occurred. No CSPack by that name found!");
                            }
                        }
                        else
                        {
                            p.sendMessage(ConstructionSites.Prefix + "§cToo few arguments! §eUsage: §a/construct install [package]");
                        }
                    }
                    else
                    {
                        p.sendMessage(ConstructionSites.Prefix + "§cInsufficient permissions!");
                    }
                }
                else if(args[0].equalsIgnoreCase("reload"))
                {
                    if(p.hasPermission("csites.admin"))
                    {
                        plugin.reloadConfig();
                        p.sendMessage(ConstructionSites.Prefix + "§aReloaded.");
                    }
                }
                else if(args[0].equalsIgnoreCase("help"))
                {
                    p.sendMessage(ConstructionSites.Prefix + "§e========== §6/construct - Command Usage §e==========");
                    p.sendMessage("§8» §e/construct §7- Displays an inventory menu with all available options.");
                    p.sendMessage("§4§lADMIN §eCommands:");
                    p.sendMessage("§8» §e/construct §6admin addsite [site name] [hours:minutes:seconds] [cost.tobuild] §7- Adds a construction site by the name of its corresponding schematic: name, time, cost.");
                    p.sendMessage("§8» §e/construct §6admin delsite [site name] §7- Deletes a site.");
                    p.sendMessage("§8» §e/construct reload §7- Reloads the config.");
                    p.sendMessage("§b§lPLAYER §eCommands:");
                    p.sendMessage("§8» §e/construct §6build §7- Displays all buildings you can create.");
                    p.sendMessage("§8» §e/construct §6build [site name] §7- Creates a construction site matching the given name.");
                    p.sendMessage("§8» §e/construct §6scan [site name] §7- Scans an area to see if you're allowed to build in it based on the given site name.");

                }
                else
                {
                    p.sendMessage(ConstructionSites.Prefix + "§cNo subcommand could be matched to \"§6" + args[0] + "§c\". §cUse §e/construct help §cfor help.");
                }
            }
            else
            {
                CommandMenu menu = new CommandMenu(p);
                menu.createCommandMenu();
                //p.sendMessage(Prefix + "§4Too few arguments. §6/construct build§e, §6/construct admin§e, §6/construct install§e.");
            }
        }
        return false;
    }
    
    public static boolean getAllowedBuildSite(Player p, String name)
    {
        return plugin.getConfig().contains("CS." + name.toLowerCase()) && p.hasPermission("csites.build." + name.toLowerCase());
    }


    public static boolean locationCanBuild(Location loc, Player p)
    {
        /*boolean whether = true;
        if(ConstructionSites.getWorldGuard() != null)
        {
            Boolean set = ConstructionSites.getWorldGuard().canBuild(p, loc.getBlock());

            if(!set)
            {
                whether = false;
            }
        }

        if(plugin.getServer().getPluginManager().getPlugin("GriefPrevention") != null)
        {
            if (GriefPrevention.instance.dataStore.getClaimAt(loc, true, null) != null)
            {
                if (GriefPrevention.instance.dataStore.getClaimAt(loc, true, null).getOwnerName().equalsIgnoreCase(p.getName()))
                {

                }
                else
                {
                    whether = false;
                }
            }
        }*/

        BlockBreakEvent bbe = new BlockBreakEvent(loc.getBlock(), p.getPlayer());
        Bukkit.getPluginManager().callEvent(bbe);
        return !bbe.isCancelled();
    }

    public static List<Location> scanForArea(Location start, int gx, int gy, int gz, int offx, int offy, int offz)
    {
        List<Location> Return = new ArrayList<>();
        
        for (int x = -(gx + offx); x <= (gx + offx); x ++)
        {
            for (int y = -(gy + offy); y <= (gy + offy); y ++)
            {
                for (int z = -(gz + offz); z <= (gz + offz); z ++)
                {
                    Location loc = new Location(start.getWorld(), (start.getBlockX() + x), (start.getBlockY() + y), (start.getBlockZ() + z));
                    Return.add(loc);
                }
            }
        }
        
        return Return;
    }

    public BlockFace getFaceFromFloat(float yaw, boolean reverse)
    {
        BlockFace bf = null;
        
        if(yaw > 0 && yaw < 90)
        {
            bf = BlockFace.SOUTH;
        }
        if(yaw > 90 && yaw < 180)
        {
            bf = BlockFace.WEST;
        }
        if(yaw > 180 && yaw < 270)
        {
            bf = BlockFace.NORTH;
        }
        if(yaw > 270 && yaw < 360)
        {
            bf = BlockFace.EAST;
        }

        if(reverse)
        {
            if(yaw > 0 && yaw < 90)
            {
                bf = BlockFace.NORTH;
            }
            if(yaw > 90 && yaw < 180)
            {
                bf = BlockFace.EAST;
            }
            if(yaw > 180 && yaw < 270)
            {
                bf = BlockFace.SOUTH;
            }
            if(yaw > 270 && yaw < 360)
            {
                bf = BlockFace.WEST;
            }
        }
        
        
        return bf;
    }

    public static void signCountDown(Location loc, UUID uuid) 
    {
        BukkitTask task = new CSCountTask(ConstructionSites.getProvidingPlugin(ConstructionSites.class), loc, uuid).runTaskTimer(ConstructCmd.plugin, 20, 20);
    }
}
