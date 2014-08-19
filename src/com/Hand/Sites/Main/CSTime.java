/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.Hand.Sites.Main;

import java.util.Arrays;
import org.bukkit.Bukkit;

/**
 *
 * @author Robert
 */
public class CSTime 
{
    public static int getDurationBreakdownToTicks(String string)
    {
        String[] part = string.replace("", "‘").replace("\'", "").replace("‘", "").split(":");
        int isecs = Integer.parseInt(part[2]);
        
        int imins = Integer.parseInt(part[1]);
        
        int ihrs = Integer.parseInt(part[0]);

        int hours = ihrs * 60 * 60 * 20;
        int minutes = imins * 60 * 20;
        int seconds = isecs * 20;
        
        int simplemaths = hours + minutes + seconds;
        
        //Bukkit.broadcastMessage("Returning: §a" + hours + " + " + minutes + " + " + seconds + " = " + simplemaths);

        return simplemaths;
    }
    
    public static String getMsgsafeTime(String input)
    {                
        String[] to = input.replace("`", "").replace("\'", "").replace("‘", "").replace("’", "")
                .replace("h", ":").replace("m", ":").split(":");
        //Bukkit.broadcastMessage("Test: " + input + ", test 2: " + Arrays.toString(to));
        return to[0] + " hours, " + to[1] + " minutes, " + to[2] + " seconds ";
    }
    
    public static String getDurationBreakdown(int ticks)
    {
        
        int seconds = ticks / 20;
        int minutes = seconds / 60;
        seconds = seconds - minutes * 60;
        int hours = minutes / 60;
        minutes = minutes - hours * 60;
                
        String input = hours + ":" + minutes + ":" + seconds;
        //Bukkit.broadcastMessage(input);
        
        return(input);
    }
}