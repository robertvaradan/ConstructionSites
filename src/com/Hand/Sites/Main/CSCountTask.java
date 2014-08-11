package com.Hand.Sites.Main;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class CSCountTask extends BukkitRunnable {
 
    private final JavaPlugin plugin;
 
    private int counter;
    private Location signloc;
 
    public CSCountTask(JavaPlugin plugin, Location loc) 
    {
        this.plugin = plugin;
        if (loc.getBlock().getType() != Material.SIGN && loc.getBlock().getType() != Material.WALL_SIGN && loc.getBlock().getType() != Material.SIGN_POST) 
        {
        //Bukkit.broadcastMessage("§6ERROR WITH SIGN! NOT EXIST ONOES");
        }
        else
        {
            //Bukkit.broadcastMessage("§cIt's working! " + counter);
            this.counter = CSTime.getDurationBreakdownToTicks(((Sign) loc.getBlock().getState()).getLine(1).replace("§b", ""));
            this.signloc = loc;
        }
    }
 
    @Override
    public void run() 
    {
        // What you want to schedule goes here
        if(signloc == null)
        {
            this.cancel();
        }
        else
        {
        
            if (counter > 0 && (signloc.getBlock().getType() == Material.SIGN || signloc.getBlock().getType() == Material.SIGN_POST || signloc.getBlock().getType() == Material.WALL_SIGN)) 
            { 
                Sign s = (Sign) signloc.getBlock().getState();
                counter = (CSTime.getDurationBreakdownToTicks(s.getLine(1).replace("§b", "")) - 20);
                s.setLine(1, "§b" + CSTime.getDurationBreakdown(counter));
                s.update();
                Location loc = s.getLocation();

                List<String> processes = (List<String>) plugin.getConfig().getList("Processes");
                if(processes != null && !processes.isEmpty() && !processes.contains(loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ()))
                {
                processes.add(loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
                plugin.saveConfig();
                }

                //Bukkit.broadcastMessage("§bCountingDown! " + counter);
            } 
            else if(counter <= 0 && (signloc.getBlock().getType() == Material.SIGN || signloc.getBlock().getType() == Material.SIGN_POST || signloc.getBlock().getType() == Material.WALL_SIGN))
            {
                Sign s = (Sign) signloc.getBlock().getState();
                s.setLine(1, "§aCompleted");
                s.update();
                //Bukkit.broadcastMessage("§aDone! " + counter);
                Location loc = signloc;

                List<String> processes = (List<String>) plugin.getConfig().getList("Processes");
                if(processes != null && !processes.isEmpty() && processes.contains(loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ()))
                {
                    processes.remove(loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
                    plugin.saveConfig();
                }

                this.cancel();
            }
            else
            {
            Location loc = signloc;

            List<String> processes = (List<String>) plugin.getConfig().getList("Processes");

                if(processes != null && !processes.isEmpty() && processes.contains(loc.getWorld() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ()))
                {
                    processes.remove(loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
                    plugin.saveConfig();
                }

                this.cancel();
            }

            //Bukkit.broadcastMessage("§6Runner! " + counter);
        }
    }
 
}