/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.Hand.Sites.Commands;

import com.Hand.Sites.Main.BuildSounds;
import com.Hand.Sites.Main.BuildSounds.BuildSound;
import com.Hand.Sites.Main.CSCountTask;
import com.Hand.Sites.Main.CSTime;
import com.Hand.Sites.Main.Main;
import static com.Hand.Sites.Main.Main.Prefix;
import com.Hand.Sites.Main.Prefs;
import com.Hand.Sites.Main.TerrainManager;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.FilenameException;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

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
                                p.sendMessage(Prefix + "§cPlease choose a site to add. §6/construct admin addsite §4<Name> <Construction Time> <hours:minutes:seconds> <Cost: dollars.cents>");
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
                if(args[0].equalsIgnoreCase("build"))
                {
                    if(args.length >= 2)
                    {
                        if(getAllowedBuildSite(p, args[1].toLowerCase()))
                        {
                            //p.sendMessage("§e§oAttempting to build site. Please wait a moment.");
                            //pasteSchematic(p.getWorld(), new File(plugin.getDataFolder().getParent() + "/WorldEdit/schematics/" + args[1].toLowerCase() + ".schematic"), new Vector(p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ()), p, plugin.getConfig().getDouble("BuildSites." + args[1].toLowerCase() + ".Time"), (int) currenttime); 
                            final String buildtime = plugin.getConfig().getString("CS." + args[1].toLowerCase() + ".Time").replace("’", "");
                            final double cost = plugin.getConfig().getDouble("CS." + args[1].toLowerCase() + ".Cost");
                                //p.sendMessage("Time in milis is: " + currenttime + ". Time in milis (finish) is: " + currenttime + plugin.getConfig().getInt("BuildSites." + args[1] + ".Time"));
                            
                            //p.sendMessage("Is there such a path? " + plugin.getConfig().contains("BuildSites." + args[1].toLowerCase() + ".Time"));
                            
                            if(!"0:0:0".equals(plugin.getConfig().getString("CS." + args[1].toLowerCase() + ".Time")))
                            {
                                try {
                                    WorldEditPlugin wep = (WorldEditPlugin)Bukkit.getPluginManager().getPlugin("WorldEdit");
                                    TerrainManager tm = new TerrainManager(wep, p);
                                        p.sendMessage(Prefix + "§aScanning build area...");
                                        boolean test = tm.testLoadSchematic(new File(plugin.getDataFolder().getParent() + "/WorldEdit/schematics/" + args[1].toLowerCase() + ".schematic"), p.getLocation(), p, (int) TerrainManager.getFaceYaw(TerrainManager.getPlayerDirection(p.getLocation()).getOppositeFace()), true);
                                        if(test)
                                        {
                                        final Block b = p.getLocation().getBlock();
                                        b.setType(Material.SIGN_POST);
                                        final Sign s = (Sign) b.getState();
                                        //((Directional)b.getState().getData()).setFacingDirection(getFaceFromFloat(p.getLocation().getYaw(), true));
                                        //p.sendMessage("Yaw: " + p.getLocation().getYaw() + "SignFaceDir: " + ((Directional)b.getState().getData()).getFacing());
                                        //Date now = new Date(currenttime);
                                        //Date then = new Date(currenttime + buildtime);
                                        //now.getTime() returns the Unix-Timestamp.

                                        //format the output to "24-01-2012 15:19:45"
                                        //SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                                        //String form = format.format(then);
                                        String strong = CSTime.getMsgsafeTime(buildtime);
                                        
                                            if(strong.endsWith(" "))
                                            {
                                                strong = strong.substring(0, (strong.length() - 1));
                                            }
                                            
                                            final String strang = strong;
                                            

                                            p.sendMessage(Prefix + "§aScanning successful: no restricted regions nearby. Creating Construction Site...");
                                            if(b.getType() == Material.SIGN_POST)
                                            {
                                                //String[] split = form.split(" ");
                                                //p.sendMessage("It was indeed a sign! :o");
                                                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.ConstructionSites, new Runnable()
                                                {
                                                    
                                                    @Override
                                                    public void run()
                                                    {
                                                        p.sendMessage(Main.Prefix + "§eSite built. Your building will be completed in §a" + strang + ". §eThe total building cost will be §a$" + cost + ".");
                                                        s.setLine(0, "§1[Construct]");
                                                        s.setLine(1, "§b" + buildtime.replace("‘", "").replace("’", "").replace("\'", ""));
                                                        s.setLine(2, "§b$" + cost);
                                                        s.setLine(3, "§3" + args[1].trim());
                                                        org.bukkit.material.Sign matSign = new org.bukkit.material.Sign(Material.SIGN_POST);
                                                        matSign.setFacingDirection(TerrainManager.getPlayerDirection(p.getLocation())); // TODO ...
                                                        s.setData(matSign);
                                                        s.update();
                                                        p.sendMessage(Prefix + "§6An advance of §a$" + cost / Prefs.ac + " §6has been taken from your account.");
                                                        p.sendMessage(Prefix + "§6This cost will be returned upon the building's completion.");
                                                        signCountDown(b.getLocation());
                                                        Location loc = s.getLocation();

                                                        List<String> processes = (List<String>) plugin.getConfig().getList("Processes");

                                                        if(processes != null && !processes.isEmpty() && !processes.contains(loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ()))
                                                        {
                                                            processes.add(loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
                                                            plugin.saveConfig();
                                                        }
                                                        else
                                                        {
                                                            List<String> toadd = new ArrayList<>();
                                                            toadd.add(loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());

                                                            //p.sendMessage("PROCESSES: " + toadd.toString());
                                                            plugin.getConfig().set("Processes", toadd);
                                                            plugin.saveConfig();
                                                        }
                                                    }
                                                }, 1);
                                            }
                                        
                                        }
                                        else
                                        {
                                            p.sendMessage(Prefix + "§cError: This site would overlap a region you don't have access to.");
                                        }
                                    } catch (FilenameException | DataException | IOException | MaxChangedBlocksException | EmptyClipboardException ex) {
                                        Logger.getLogger(ConstructCmd.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            else
                            {
                                try 
                                {
                                    WorldEditPlugin wep = (WorldEditPlugin)Bukkit.getPluginManager().getPlugin("WorldEdit");
                                    TerrainManager tm = new TerrainManager(wep, p);
                                    boolean test = tm.testLoadSchematic(new File(plugin.getDataFolder().getParent() + "/WorldEdit/schematics/" + args[1].toLowerCase() + "-test.schematic"), p.getLocation(), p, (int) TerrainManager.getFaceYaw(TerrainManager.getPlayerDirection(p.getLocation()).getOppositeFace()), false);
                                    if(test)
                                    {
                                    tm.loadSchematic(new File(plugin.getDataFolder().getParent() + "/WorldEdit/schematics/" + args[1].toLowerCase() + ".schematic"), p.getLocation(), p, (int) TerrainManager.getFaceYaw(TerrainManager.getPlayerDirection(p.getLocation()).getOppositeFace()));
                                    p.sendMessage(Prefix + "§aBuilding created.");
                                    BuildSounds.playBuildSound(BuildSounds.BuildSound.SITE_BUILT, p.getLocation());
                                    }
                                } 
                                catch (FilenameException | DataException | IOException | MaxChangedBlocksException | EmptyClipboardException ex) 
                                {
                                    Logger.getLogger(ConstructCmd.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }



                        }
                        else
                        {
                            p.sendMessage(Prefix + "§cNo blueprint for this site is avalible.");
                        }
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

    public static void signCountDown(Location loc) 
    {
        BukkitTask task = new CSCountTask(Main.getProvidingPlugin(Main.class), loc).runTaskTimer(ConstructCmd.plugin, 20, 20);
    }
}
