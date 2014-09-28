/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.Hand.Sites.Commands;

import com.Hand.CSAPI.ConstructionSite;
import com.Hand.Sites.Core.CSCountTask;
import com.Hand.Sites.Core.URLServices.DLC;
import com.Hand.Sites.Main.Main;
import com.Hand.Sites.Main.Prefs;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.Hand.Sites.Main.Main.Prefix;

/**
 *
 * @author Robert
 */
public class ConstructCmd implements CommandExecutor
{
    public static Main plugin = Main.plugin;
    
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) 
    {
        if(cmd.getName().equalsIgnoreCase("construct") && sender instanceof Player)
        {
            List<String> list = (List<String>) plugin.getConfig().getList("CS.Names");
            if(list == null)
            {
                List<String> empty = new ArrayList<>();
                list = empty;
                plugin.getConfig().set("CS.Names", empty);
                plugin.saveConfig();
            }
            
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
                                    
                                    p.sendMessage(Prefix + "§aSuccessfully added \"§b" + args[2].toLowerCase() + "§a.\"");
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
                if(args[0].equalsIgnoreCase("scan") && getAllowedBuildSite(p, args[1].toLowerCase()))
                {
                    ConstructionSite.scanBuildArea(p.getLocation(), args[1], p);
                }
                else if(args[0].equalsIgnoreCase("scan"))
                {
                    p.sendMessage(Prefix + "§cYou can't scan for this area!");
                }
                if(args[0].equalsIgnoreCase("build"))
                {
                    if(args.length >= 2)
                    {
                        ConstructionSite s = new ConstructionSite(p.getLocation(), p.getUniqueId(), args[1], Prefix, false);
                    }
                    else
                    {
                        p.sendMessage("§ePlease specify what type of building you want to make. §eAllowed sites:");
                        for (String site : list) 
                        {
                            if(getAllowedBuildSite(p, site))
                            {
                                p.sendMessage("§6- " + site + ", §a$" + plugin.getConfig().getDouble("CS." + site + ".Cost"));
                            }
                        }

                    }
                }
                else if(args[0].equalsIgnoreCase("install"))
                {
                    if(p.hasPermission("csites.admin") && args.length >= 1)
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
                                p.sendMessage(Prefix + "§cError ocurred. No CSPack by that name found!.");
                            }
                        }
                    }
                }
            }
            else
            {
                p.sendMessage(Prefix + "§4Too few arguments. §6/construct build§e, §6/construct admin§e, §6/construct install§e.");
            }
        }
        return false;
    }
    
    public static boolean getAllowedBuildSite(Player p, String name)
    {
        return plugin.getConfig().contains("CS." + name.toLowerCase()) && p.hasPermission("csites.build." + name.toLowerCase());
    }
    
    public static void pasteSchematic(final World world, final File file, final Vector origin, final Player p, Sign b) //throws DataException, IOException, MaxChangedBlocksException
    {
            try //throws DataException, IOException, MaxChangedBlocksException
                {
                    //p.sendMessage("§7DEBUG: §eTrying to paste.");
                
                    EditSession es = new EditSession(new BukkitWorld(world), 999999999);
                    SchematicFormat schematic = SchematicFormat.getFormat(file);

                    CuboidClipboard cc = schematic.load(file);                   
                    
/*                    if(b != null)
                    {
                        cc.rotate2D((int) b.getLocation().getYaw());                        
                    }
                    else
                    {
                        cc.rotate2D((int) p.getLocation().getYaw());
                    }*/
                    // BaseBlock data = cc.getBlock(origin);
                    boolean resolved = false;

                    int x = (cc.getWidth() + 1) / 2;
                    int y = (cc.getHeight() + 1) / 2;
                    int z = (cc.getLength() + 1) / 2;

                    int px = p.getLocation().getBlockX();
                    int py = p.getLocation().getBlockY();   
                    int pz = p.getLocation().getBlockZ();
                    //p.sendMessage("§7DEBUG: §eFor loop.");


                    for(Location loc : scanForArea(p.getLocation().getBlock().getLocation(), x, y, z, Prefs.bsox + cc.getOffset().getBlockX(), Prefs.bsoy + cc.getOffset().getBlockY(), Prefs.bsoz + + cc.getOffset().getBlockZ()))
                    {
                        //p.sendMessage("§7DEBUG: §aScanning area: §6" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ".");
                        if(!locationCanBuild(loc, p) && !resolved)
                        {
                                p.sendMessage(Prefix + "§4ERROR: §6Could not create Construction site. It overlaps a region you don't have access to.");
                                resolved = true;
                        }
                        else if(locationCanBuild(loc, p) && !resolved)
                        {
                            p.sendMessage(Prefix + "§aBuilding created.");

                            cc.place(es, origin, false);
                            resolved = true;
                        }
                    }
                } catch (DataException | IOException | MaxChangedBlocksException ex) {
                    System.out.println(Prefix + "An error ocurred when player " + p.getName() + "attempted to create a construction site. Exception: " + ex.getMessage());
                    p.sendMessage("Exception! Oh noes: " + ex.getMessage());
                }
        }

    public static boolean locationCanBuild(Location loc, Player p)
    {
        boolean whether = true;
        if(Main.getWorldGuard() != null && Main.getWorldGuard().getRegionManager(loc.getWorld()).getApplicableRegions(loc) != null)
        {
            Boolean set = Main.getWorldGuard().getRegionManager(loc.getWorld()).getApplicableRegions(loc).canBuild(Main.getWorldGuard().wrapPlayer(p));
        
            if(!set)
            {
                whether = false;
            }
        }
        
        return whether;
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
        BukkitTask task = new CSCountTask(Main.getProvidingPlugin(Main.class), loc, uuid).runTaskTimer(ConstructCmd.plugin, 20, 20);
    }
}
