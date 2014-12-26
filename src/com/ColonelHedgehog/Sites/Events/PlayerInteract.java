package com.ColonelHedgehog.Sites.Events;

import com.ColonelHedgehog.Sites.Commands.ConstructCmd;
import com.ColonelHedgehog.Sites.Core.ConstructionSites;
import com.ColonelHedgehog.Sites.Core.Prefs;
import com.ColonelHedgehog.Sites.Services.BuildSounds;
import com.ColonelHedgehog.Sites.Services.BuildSounds.BuildSound;
import com.ColonelHedgehog.Sites.Services.CSBuilder;
import com.ColonelHedgehog.Sites.Services.CSTime;
import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.FilenameException;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Directional;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.ColonelHedgehog.Sites.Core.ConstructionSites.Prefix;
import static com.ColonelHedgehog.Sites.Core.ConstructionSites.economy;

public class PlayerInteract implements Listener
{

    private static ConstructionSites plugin = ConstructionSites.plugin;
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            Block clicked = event.getClickedBlock();
            
            if(clicked.getType() == Material.SIGN || clicked.getType() == Material.WALL_SIGN || clicked.getType() == Material.SIGN_POST)
            {
                Sign sign = (Sign) clicked.getState();
                String line0 = sign.getLine(0).replace("§0", "");
                String line1 = sign.getLine(1).replace("§0", "");
                String line2 = sign.getLine(2).replace("§0", "");
                String line3 = sign.getLine(3).replace("§0", "");
                
                if (line0.equals("§1[Construct]"))
                {
                    Player p = event.getPlayer();
                    boolean Continue = true;
                    double cost = 0;
                    try{cost = Double.parseDouble(line2.replace("§b$", ""));}
                    catch(NumberFormatException nfe){p.sendMessage(Prefix + "§4CRITICAL ERROR: §cYour admin did not set up the price correctly! Report this to him immediately."); Continue = false;}
                            
                    if(line1.equals("§aCompleted") && Continue)
                    {
                        if(ConstructionSites.economy.getBalance(p) >= cost)
                        {
                            
                            File file = new File(plugin.getDataFolder().getParent() + "/WorldEdit/schematics/" + line3.replace("§0", "") + ".schematic");
                            if(!file.exists())
                            {
                                p.sendMessage(ConstructionSites.Prefix + "§4FATAL: §cCouldn't construct site from file, because no file could be found. It was likely deleted... or it never even existed in the first place.");
                                return;
                            }
                            //Vector vector = new Vector(p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ());
                                try {
                                    WorldEditPlugin wep = (WorldEditPlugin)Bukkit.getPluginManager().getPlugin("WorldEdit");
                                    CSBuilder testtm = new CSBuilder(wep, p);
                                        boolean test = testtm.testLoadSchematic(file, event.getClickedBlock().getLocation(), p, (int) CSBuilder.getFaceYaw(((Directional) sign.getData()).getFacing().getOppositeFace()), false);
                                        if(test)
                                        {
                                        CSBuilder tm = new CSBuilder(wep, p);
                                        boolean loadschematic = tm.loadSchematic(file, event.getClickedBlock().getLocation(), p, (int) CSBuilder.getFaceYaw(((Directional) sign.getData()).getFacing().getOppositeFace()), false);
                                        //Bukkit.broadcastMessage("Rotation is: " + TerrainManager.getPlayerDirection(event.getClickedBlock().getLocation()) + " and " + TerrainManager.getFaceYaw(((Directional) sign).getFacing().getOppositeFace()));

                                        if(loadschematic)
                                        {
                                        ConstructionSites.economy.withdrawPlayer(p, cost);
                                        p.sendMessage(Prefix + "§eBuild cost: $" + cost + ". Returned: " + cost * Prefs.ac + ". "
                                                + "Total due: §e$" + (cost - cost * Prefs.ac) + "§e.");
                                        p.sendMessage(Prefix + "§aBuilding created.");
                                        ConstructionSites.economy.withdrawPlayer(p, cost);
                                        //event.getClickedBlock().breakNaturally();
                                        BuildSounds.playBuildSound(BuildSounds.BuildSound.SITE_BUILT, p.getLocation(), null);

                                        BukkitUtil.findFreePosition(p);

                                        sign.setLine(0, "§c[Construct]");

                                        BuildSounds.playBuildSound(BuildSound.SITE_BUILT, p.getLocation(), null);
                                        }
                                    }
                                    } catch (FilenameException | DataException | IOException | MaxChangedBlocksException | EmptyClipboardException ex) {
                                    Logger.getLogger(ConstructCmd.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            BuildSounds.playBuildSound(BuildSound.SITE_BUILT, p.getLocation(), null);

                        }
                        else
                        {
                            p.sendMessage(Prefix + "§4Insufficient funds. §6You need §a$" + cost + " §6to complete this building. Your balance is §a$" + economy.getBalance(p) + "§6.");
                        }
                    }
                    else if(!line1.equals("§aCompleted"))
                    {
                        //now.getTime() returns the Unix-Timestamp.

                        //format the output to "24-01-2012 15:19:45"
                        String strang = CSTime.getMsgsafeTime(line1.replace("§b", ""));
                        if(strang.endsWith(" "))
                        {
                            strang = strang.substring(0, (strang.length() - 1));
                        }

                        p.sendMessage(Prefix + "§6Your building is not ready yet! Time remaining: §e" + strang + "§6.");
                    }
                }
                else
                {
                    plugin.getLogger().info("NUUU: " + line0 + ", " + line1);
                }
            }

        }
    }
}