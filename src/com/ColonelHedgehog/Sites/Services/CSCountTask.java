package com.ColonelHedgehog.Sites.Services;


import com.ColonelHedgehog.CSAPI.Events.SiteCompleteEvent;
import com.ColonelHedgehog.CSAPI.Events.SiteFinishEvent;
import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.FilenameException;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Sign;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.material.Directional;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("deprecation")
public class CSCountTask extends BukkitRunnable
{

    private final JavaPlugin plugin;

    private int counter;
    private Location signloc;
    private UUID uuid;
    private int bt;

    public CSCountTask(JavaPlugin plugin, Location loc, UUID uuid)
    {
        this.plugin = plugin;
        if (loc.getBlock().getType() == Material.SIGN || loc.getBlock().getType() == Material.WALL_SIGN || loc.getBlock().getType() == Material.SIGN_POST)
        {
            //Bukkit.broadcastMessage("§cIt's working! " + counter);
            this.counter = CSTime.getDurationBreakdownToTicks(((Sign) loc.getBlock().getState()).getLine(1).replace("§b", ""));
            this.signloc = loc;
            this.uuid = uuid;
            this.bt = CSTime.getDurationBreakdownToTicks(((Sign) loc.getBlock().getState()).getLine(1).replace("§b", ""));
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void run()
    {
        // What you want to schedule goes here
        if (signloc == null)
        {
            this.cancel();
        }
        else
        {

            if (counter > 0 && (signloc.getBlock().getType() == Material.SIGN || signloc.getBlock().getType() == Material.SIGN_POST || signloc.getBlock().getType() == Material.WALL_SIGN))
            {
                Sign s = (Sign) signloc.getBlock().getState();
                int signticks = CSTime.getDurationBreakdownToTicks(s.getLine(1).replace("§b", ""));
                counter = ((signticks) - 20);
                try
                {
                    File file = new File(plugin.getDataFolder().getParent() + "/WorldEdit/schematics/" + s.getLine(3).replace("§3", "") + "_" + s.getLine(1).replace("§b", "").replace(":", "-") + ".schematic");
                    if (file.exists() && Bukkit.getPlayer(uuid) != null && Bukkit.getPlayer(uuid).isOnline())
                    {
                        //Bukkit.broadcastMessage("File existed, so placing " + file.toString());
                        WorldEditPlugin wep = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
                        Player p = Bukkit.getPlayer(uuid);
                        CSBuilder tm = new CSBuilder(wep, Bukkit.getPlayer(uuid));
                        tm.loadSchematic(file, signloc, p, (int) CSBuilder.getFaceYaw(((Directional) s.getData()).getFacing().getOppositeFace()), true);
                    }
                }
                catch (IOException | DataException | FilenameException | MaxChangedBlocksException | EmptyClipboardException ex)
                {
                    Logger.getLogger(CSCountTask.class.getName()).log(Level.SEVERE, null, ex);
                }

                s.setLine(1, "§b" + CSTime.getDurationBreakdown(counter));
                s.update();
                Location loc = s.getLocation();

                List<String> processes = plugin.getConfig().getStringList("Processes");
                if (processes != null && !processes.isEmpty() && !processes.contains(loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "," + uuid))
                {
                    processes.add(loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "," + uuid);
                    plugin.saveConfig();
                }

                //Bukkit.broadcastMessage("§bCountingDown! " + counter);
            }
            else if (counter <= 0 && (signloc.getBlock().getType() == Material.SIGN || signloc.getBlock().getType() == Material.SIGN_POST || signloc.getBlock().getType() == Material.WALL_SIGN))
            {
                Sign s = (Sign) signloc.getBlock().getState();
                s.setLine(1, "§aCompleted");
                s.update();


                // Legacy
                if (s.getLine(3) != null && Bukkit.getPlayer(uuid) != null)
                {
                    SiteCompleteEvent event = new SiteCompleteEvent(s.getLine(3), Bukkit.getPlayer(uuid));
                    Bukkit.getServer().getPluginManager().callEvent(event);
                }

                // New, better

                SiteFinishEvent event1 = new SiteFinishEvent(s.getLine(3), uuid, bt, CSTime.getDurationBreakdownToTicks(((Sign) signloc.getBlock().getState()).getLine(1).replace("§3$", "")), signloc);
                Bukkit.getServer().getPluginManager().callEvent(event1);

                Firework stack = signloc.getWorld().spawn(signloc, Firework.class);
                stack.setVelocity(new Vector(0, stack.getVelocity().getBlockY(), 0));
                FireworkMeta meta = stack.getFireworkMeta();

                Builder effect1 = FireworkEffect.builder();
                effect1.with(Type.BALL_LARGE);
                effect1.withColor(Color.GREEN);
                effect1.withFlicker();
                meta.addEffect(effect1.build());

                meta.setPower(3);

                stack.setFireworkMeta(meta);

                //Bukkit.broadcastMessage("§aDone! " + counter);
                Location loc = signloc;

                List<String> processes = plugin.getConfig().getStringList("Processes");
                if (processes != null && !processes.isEmpty() && processes.contains(loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ()))
                {
                    processes.remove(loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "," + uuid);
                    plugin.saveConfig();
                }
                this.cancel();
            }
            else
            {
                Location loc = signloc;

                List<String> processes = plugin.getConfig().getStringList("Processes");

                if (processes != null && !processes.isEmpty() && processes.contains(loc.getWorld() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "," + uuid))
                {
                    processes.remove(loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "," + uuid);
                    plugin.saveConfig();
                }

                this.cancel();
            }

            //Bukkit.broadcastMessage("§6Runner! " + counter);
        }
    }

}