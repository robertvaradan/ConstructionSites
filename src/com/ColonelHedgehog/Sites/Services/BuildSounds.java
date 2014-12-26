/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ColonelHedgehog.Sites.Services;

import com.ColonelHedgehog.Sites.Core.ConstructionSites;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Robert
 */
public class BuildSounds
{
    static int count = 0;
    static int SoundTask = 0;
    private static ConstructionSites plugin = ConstructionSites.plugin;

    public static void playBuildSound(BuildSound sound, final Location loc, final Player src)
    {
        final int[] count = {0};
        if (sound == BuildSound.SITE_BUILT)
        {
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    loc.getWorld().playSound(loc, Sound.NOTE_BASS_DRUM, 3, 1);

                    count[0]++;

                    if (count[0] == 3)
                    {
                        cancel();
                    }
                }
            }.runTaskTimer(plugin, 0, 5);
        }
        else if (sound == BuildSound.MENU_OPENED)
        {
            new BukkitRunnable()
            {
                @Override
                public void run()
                {

                    count[0]++;

                    if (count[0] == 2)
                    {
                        src.playSound(loc, Sound.NOTE_BASS_GUITAR, 3, 1);
                        cancel();
                    }
                    else if (count[0] == 1)
                    {
                        src.playSound(loc, Sound.NOTE_BASS_GUITAR, 3, 0);
                    }
                }
            }.runTaskTimer(plugin, 0, 2);
        }
    }

    public enum BuildSound
    {
        SITE_BUILT, MENU_OPENED
    }
}
