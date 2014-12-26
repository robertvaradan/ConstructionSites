/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ColonelHedgehog.Sites.Core;

/**
 * @author Robert
 */
public class PluginServices
{
    private static ConstructionSites plugin = ConstructionSites.plugin;

    public static boolean isInstalled(String input)
    {
        return plugin.getServer().getPluginManager().getPlugin(input) != null;
    }
}
