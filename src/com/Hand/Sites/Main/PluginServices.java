/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.Hand.Sites.Main;

/**
 *
 * @author Robert
 */
public class PluginServices 
{
    public static Main plugin = Main.plugin;
    
    public static boolean isInstalled(String input)
    {
        return plugin.getServer().getPluginManager().getPlugin(input) != null;
    }
}
