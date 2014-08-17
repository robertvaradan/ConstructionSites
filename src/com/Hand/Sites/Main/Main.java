/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.Hand.Sites.Main;

import com.Hand.Sites.Commands.ConstructCmd;
import com.Hand.Sites.Commands.ConstructTabComplete;
import com.Hand.Sites.Events.PlayerInteract;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Robert
 */
public class Main extends JavaPlugin
{
    public static Main plugin;
    public static Plugin ConstructionSites;
    public static String Prefix = "§8[§eConstruction§6Sites§8]§f: ";
    public static Economy economy;
    
    @Override
    public void onEnable()
    {
        plugin = this;
        ConstructionSites = Bukkit.getPluginManager().getPlugin("ConstructionSites");
        
        Prefs.configTasks();
        
        plugin.getCommand("construct").setExecutor(new ConstructCmd());
        
        plugin.getCommand("construct").setTabCompleter(new ConstructTabComplete());
        setupEconomy();
        
        plugin.getServer().getPluginManager().registerEvents(new PlayerInteract(), this);
        
        if(!PluginServices.isInstalled("WorldEdit") || !PluginServices.isInstalled("WorldGuard") || !PluginServices.isInstalled("Vault"))
        {
            System.out.println("[ConstructionSites] PLUGIN BREAK! Not all necessary plugins were found. WorldEdit: " + PluginServices.isInstalled("WorldEdit") + ". WorldGuard: " + PluginServices.isInstalled("WorldGUard") + ". Vault: " + PluginServices.isInstalled("Vault"));
            plugin.setEnabled(false);
        }
        
        CSConfigManager.resumeBuildProcesses();
        
        // Go away, metalmikey002.
    }
    
    @Override
    public void onDisable()
    {
        
    }
    
    public static WorldGuardPlugin getWorldGuard() 
    {
        Plugin wgplugin = plugin.getServer().getPluginManager().getPlugin("WorldGuard");
        // WorldGuard may not be loaded
        if (wgplugin == null || !(wgplugin instanceof WorldGuardPlugin)) {
            return null; // Maybe you want throw an exception instead
        }

        return (WorldGuardPlugin) wgplugin;
    }    

    private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) 
        {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }

    
}
