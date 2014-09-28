/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.Hand.Sites.Core;

import com.Hand.Sites.Main.Main;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Robert
 */
public class BuildSounds 
{
    public static Main plugin = Main.plugin;
    
    public enum BuildSound
    {
        SITE_BUILT
    }
    static int count = 0;
    static int SoundTask = 0;

    public static void playBuildSound(BuildSound sound, final Location loc) 
    {
        final int[] count = {0};
        if(sound == BuildSound.SITE_BUILT)
        {
            new BukkitRunnable()
            {

                @Override
                public void run()
                {
                    loc.getWorld().playSound(loc, Sound.NOTE_BASS_DRUM, 3, 1);

                    count[0]++;

                    if(count[0] == 3)
                    {
                        cancel();
                    }
                }
            }.runTaskTimer(plugin, 0, 5);
        }
    }
}
