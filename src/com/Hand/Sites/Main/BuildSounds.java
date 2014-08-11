/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.Hand.Sites.Main;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;

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
        for(int i = 0; i < 4; i++)
        {
            if(sound == BuildSound.SITE_BUILT)
            {
                SoundTask = Bukkit.getScheduler().scheduleSyncDelayedTask(Main.ConstructionSites, new Runnable() 
                {

                    @Override
                    public void run() 
                    {
                            loc.getWorld().playSound(loc, Sound.NOTE_BASS_DRUM, 3, 1);
                    }
                }, 5 + (i * 5));
            }
        }
    }
}
