package com.Hand.Sites.Events;

import com.Hand.Sites.Commands.ConstructCmd;
import com.Hand.Sites.Main.BuildSounds;
import com.Hand.Sites.Main.BuildSounds.BuildSound;
import com.Hand.Sites.Main.CSTime;
import com.Hand.Sites.Main.Main;
import static com.Hand.Sites.Main.Main.Prefix;
import static com.Hand.Sites.Main.Main.economy;
import com.Hand.Sites.Main.Prefs;
import com.Hand.Sites.Main.TerrainManager;
import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.FilenameException;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteract implements Listener
{

    public static Main plugin = Main.plugin;
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            Block clicked = event.getClickedBlock();
            
            if(clicked.getType() == Material.SIGN || clicked.getType() == Material.WALL_SIGN || clicked.getType() == Material.SIGN_POST)
            {
                Sign sign = (Sign) clicked.getState();
                if(sign.getLine(0).equals("§1[Construct]"))
                {
                    Player p = event.getPlayer();
                    boolean Continue = true;
                    double cost = 0;
                    try{cost = Double.parseDouble(sign.getLine(2).replace("§b$", ""));}
                    catch(NumberFormatException nfe){p.sendMessage(Prefix + "§4CRITICAL ERROR: §cYour admin did not set up the price correctl! Report this to him immediately."); Continue = false;}
                            
                    if(sign.getLine(1).equals("§aCompleted") && Continue)
                    {
                        if(Main.economy.getBalance((OfflinePlayer) p) >= cost)
                        {
                            Main.economy.withdrawPlayer(p, cost);
                            p.sendMessage(Prefix + "§eBuild cost: $" + cost + ". Returned: " + cost * Prefs.ac + ". "
                                    + "Total due: §e$" + (cost - cost * Prefs.ac) + "§e.");
                            sign.getBlock().breakNaturally();
                            p.sendMessage("Message: " + sign.getLine(3).replace("§3", ""));
                            sign.getLocation().getWorld().playSound(sign.getLocation(), Sound.DIG_WOOD, 3, 1);
                            
                            File file = new File(plugin.getDataFolder().getParent() + "/WorldEdit/schematics/" + sign.getLine(3).replace("§3", "") + ".schematic");
                            //Vector vector = new Vector(p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ());
                                try {
                                    WorldEditPlugin wep = (WorldEditPlugin)Bukkit.getPluginManager().getPlugin("WorldEdit");
                                    TerrainManager tm = new TerrainManager(wep, p);
                                    tm.loadSchematic(file, p.getLocation(), p, (int) TerrainManager.getFaceYaw(event.getBlockFace()));
                                    p.sendMessage("FACEYAW: " + TerrainManager.getFaceYaw(event.getBlockFace()));
                                    BuildSounds.playBuildSound(BuildSound.SITE_BUILT, p.getLocation());
                                } catch (FilenameException | DataException | IOException | MaxChangedBlocksException | EmptyClipboardException ex) {
                                    Logger.getLogger(ConstructCmd.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            BuildSounds.playBuildSound(BuildSound.SITE_BUILT, p.getLocation());

                        }
                        else
                        {
                            p.sendMessage(Prefix + "§4Insufficient funds. §6You need §a$" + cost + " §6to complete this building. Your balance is §a$" + economy.getBalance((OfflinePlayer) p) + "§6.");
                        }
                    }
                    else if(!sign.getLine(1).equals("§aCompleted"))
                    {
                        //now.getTime() returns the Unix-Timestamp.

                        //format the output to "24-01-2012 15:19:45"
                        String strang = CSTime.getMsgsafeTime(sign.getLine(1).replace("§b", ""));
                        if(strang.endsWith(" "))
                        {
                            strang = strang.substring(0, (strang.length() - 1));
                        }

                        p.sendMessage(Prefix + "§6Your building is not ready yet! Time remaining: §e" + strang + "§6.");
                    }
                }
            }
        }
    }
}