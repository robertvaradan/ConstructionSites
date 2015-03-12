/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ColonelHedgehog.CSAPI;

import com.ColonelHedgehog.CSAPI.Events.SiteStartEvent;
import com.ColonelHedgehog.Sites.Commands.ConstructCmd;
import com.ColonelHedgehog.Sites.Core.ConstructionSites;
import com.ColonelHedgehog.Sites.Core.Prefs;
import com.ColonelHedgehog.Sites.Services.BuildSounds;
import com.ColonelHedgehog.Sites.Services.CSBuilder;
import com.ColonelHedgehog.Sites.Services.CSTime;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.ColonelHedgehog.Sites.Commands.ConstructCmd.getAllowedBuildSite;
import static com.ColonelHedgehog.Sites.Commands.ConstructCmd.signCountDown;
import static com.ColonelHedgehog.Sites.Core.ConstructionSites.economy;

/**
 * @author Robert
 */
public class ConstructionSite
{
    private static ConstructionSites plugin = ConstructionSites.plugin;
    private final Location loc;
    private final UUID uuid;
    private final String name;
    private final boolean free;
    private final String Prefix;

    public ConstructionSite(Location loc, UUID uuid, String name, String Prefix, boolean free)
    {
        this.loc = loc;
        this.uuid = uuid;
        this.name = name;
        this.Prefix = Prefix;
        this.free = free;

        makeBuildSite();
    }

    public static void scanBuildArea(final Location loc1, final String name1, final Player player)
    {
        plugin = ConstructionSites.plugin;


        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    WorldEditPlugin wep = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
                    File file = new File(plugin.getDataFolder().getParent() + "/WorldEdit/schematics/" + name1.toLowerCase() + ".schematic");

                    if (!file.exists())
                    {
                        player.sendMessage(ConstructionSites.Prefix + "§4FATAL: §eSite is registered but no matching schematic could be loaded. Might it have been deleted?");
                        return;
                    }

                    CSBuilder tm = new CSBuilder(wep, player);


                    boolean test = tm.testLoadSchematic(file, loc1, player, (int) CSBuilder.getFaceYaw(CSBuilder.getPlayerDirection(loc1).getOppositeFace()), false);
                    if(!test)
                    {
                        player.sendMessage(ConstructionSites.Prefix + "§6Results: §cYou can't build here. §6It overlaps a protected area.");
                    }
                    else
                    {
                        player.sendMessage(ConstructionSites.Prefix + "§eResults: §aYou can build here. §eNo restricted regions are nearby.");
                    }
                }
                catch (Exception ex)
                {
                    Logger.getLogger(ConstructionSite.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, 20L);
    }

    private void makeBuildSite()
    {
        final Player p = Bukkit.getPlayer(uuid);

        if (p.isOnline() && getAllowedBuildSite(p, name.toLowerCase()))
        {
            //p.sendMessage("§e§oAttempting to build site. Please wait a moment.");
            //pasteSchematic(p.getWorld(), new File(plugin.getDataFolder().getParent() + "/WorldEdit/schematics/" + name.toLowerCase() + ".schematic"), new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), p, plugin.getConfig().getDouble("BuildSites." + name.toLowerCase() + ".Time"), (int) currenttime);
            //System.out.println("NULLABLES: Config? " + (plugin.getConfig() == null) + " Name? " + (name == null));
            final String buildtime = plugin.getConfig().getString("CS." + name.toLowerCase() + ".Time").replace("’", "");
            final double cost = plugin.getConfig().getDouble("CS." + name.toLowerCase() + ".Cost");
            //p.sendMessage("Time in milis is: " + currenttime + ". Time in milis (finish) is: " + currenttime + plugin.getConfig().getInt("BuildSites." + name + ".Time"));

            //p.sendMessage("Is there such a path? " + plugin.getConfig().contains("BuildSites." + name.toLowerCase() + ".Time"));

            if (!"0:0:0".equals(plugin.getConfig().getString("CS." + name.toLowerCase() + ".Time")))
            {
                try
                {
                    WorldEditPlugin wep = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
                    File scanf = new File(plugin.getDataFolder().getParent() + "/WorldEdit/schematics/" + name.toLowerCase() + ".schematic");

                    if(!scanf.exists())
                    {
                        p.sendMessage(ConstructionSites.Prefix + "§4FATAL: §eSite is registered but no matching schematic could be loaded. Might it have been deleted?");
                        return;
                    }

                    CSBuilder tm = new CSBuilder(wep, p);

                    p.sendMessage(Prefix + "§aScanning build area...");
                    boolean test = tm.testLoadSchematic(scanf, loc, p, (int) CSBuilder.getFaceYaw(CSBuilder.getPlayerDirection(loc).getOppositeFace()), Prefs.particles);
                    if (test)
                    {
                        final Block b = loc.getBlock();
                        b.setType(Material.SIGN_POST);
                        final Sign s = (Sign) b.getState();
                        //((Directional)b.getState().getData()).setFacingDirection(getFaceFromFloat(loc.getYaw(), true));
                        //p.sendMessage("Yaw: " + loc.getYaw() + "SignFaceDir: " + ((Directional)b.getState().getData()).getFacing());
                        //Date now = new Date(currenttime);
                        //Date then = new Date(currenttime + buildtime);
                        //now.getTime() returns the Unix-Timestamp.

                        //format the output to "24-01-2012 15:19:45"
                        //SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                        //String form = format.format(then);
                        String strong = CSTime.getMsgsafeTime(buildtime);

                        if (strong.endsWith(" "))
                        {
                            strong = strong.substring(0, (strong.length() - 1));
                        }

                        final String strang = strong;


                        p.sendMessage(Prefix + "§aScanning successful: no restricted regions nearby. Creating Construction Site...");
                        if (b.getType() == Material.SIGN_POST && economy.getBalance(p) >= cost)
                        {
                            //String[] split = form.split(" ");
                            //p.sendMessage("It was indeed a sign! :o");
                            Bukkit.getScheduler().scheduleSyncDelayedTask(ConstructionSites.ConstructionSites, new Runnable()
                            {

                                @Override
                                public void run()
                                {
                                    if (cost > 0)
                                    {
                                        p.sendMessage(ConstructionSites.Prefix + "§eSite built. Your building will be completed in §a" + strang + ". §eThe total building cost will be §a$" + cost + ".");
                                        if(Prefs.ac != 0)
                                        {
                                            p.sendMessage(Prefix + "§6An advance of §a$" + cost * Prefs.ac + " §6has been taken from your account.");
                                        }
                                        p.sendMessage(Prefix + "§6This cost will be returned upon the building's completion.");
                                        economy.withdrawPlayer(p, cost / Prefs.ac);
                                    }
                                    else
                                    {
                                        p.sendMessage(ConstructionSites.Prefix + "§eSite built. Your building will be completed in §a" + strang + ".");
                                    }

                                    s.setLine(0, "§1[Construct]");
                                    s.setLine(1, "§b" + buildtime.replace("‘", "").replace("’", "").replace("\'", ""));
                                    s.setLine(2, "§b$" + cost);
                                    s.setLine(3, "" + name.trim());
                                    org.bukkit.material.Sign matSign = new org.bukkit.material.Sign(Material.SIGN_POST);
                                    matSign.setFacingDirection(CSBuilder.getPlayerDirection(loc));
                                    s.setData(matSign);
                                    s.update();
                                    signCountDown(b.getLocation(), p.getUniqueId());
                                    Location loc = s.getLocation();

                                    List<String> processes = plugin.getConfig().getStringList("Processes");

                                    if (processes != null && !processes.isEmpty() && !processes.contains(loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ()))
                                    {
                                        processes.add(loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "," + p.getUniqueId().toString());
                                        plugin.saveConfig();
                                    }
                                    else
                                    {
                                        List<String> toadd = new ArrayList<>();
                                        toadd.add(loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "," + p.getUniqueId().toString());

                                        //p.sendMessage("PROCESSES: " + toadd.toString());
                                        plugin.getConfig().set("Processes", toadd);
                                        plugin.saveConfig();
                                    }

                                    SiteStartEvent event = new SiteStartEvent(name, p, CSTime.getDurationBreakdownToTicks(((Sign) loc.getBlock().getState()).getLine(1).replace("§b", "")), cost, loc);
                                    Bukkit.getPluginManager().callEvent(event);
                                }
                            }, 1);
                        }
                        else if (economy.getBalance(p) < cost)
                        {
                            p.sendMessage(Prefix + "§cWhoops, looks like you don't have enough money.");
                            loc.getBlock().setType(Material.AIR);
                        }
                    }
                    else
                    {
                        p.sendMessage(Prefix + "§4ERROR: §6Could not create construction site. It overlaps a region you can't build in.");
                    }
                }
                catch (Exception ex)
                {
                    Logger.getLogger(ConstructCmd.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else
            {
                try
                {
                    WorldEditPlugin wep = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
                    CSBuilder tm = new CSBuilder(wep, p);
                    boolean test = tm.testLoadSchematic(new File(plugin.getDataFolder().getParent() + "/WorldEdit/schematics/" + name.toLowerCase() + ".schematic"), loc, p, (int) CSBuilder.getFaceYaw(CSBuilder.getPlayerDirection(loc).getOppositeFace()), Prefs.particles);
                    if (test)
                    {
                        tm.loadSchematic(new File(plugin.getDataFolder().getParent() + "/WorldEdit/schematics/" + name.toLowerCase() + ".schematic"), loc, p, (int) CSBuilder.getFaceYaw(CSBuilder.getPlayerDirection(loc).getOppositeFace()), false);
                        p.sendMessage(Prefix + "§aBuilding created.");
                        BuildSounds.playBuildSound(BuildSounds.BuildSound.SITE_BUILT, loc, null);
                    }
                }
                catch (Exception ex)
                {
                    Logger.getLogger(ConstructCmd.class.getName()).log(Level.SEVERE, null, ex);
                }
            }


        }
        else if(p.isOnline())
        {
            p.sendMessage(Prefix + "§cNo blueprint for this site is available.");
        }
    }
}
