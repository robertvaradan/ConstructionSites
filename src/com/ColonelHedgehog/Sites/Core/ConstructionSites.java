/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ColonelHedgehog.Sites.Core;

import com.ColonelHedgehog.Sites.Commands.ConstructCmd;
import com.ColonelHedgehog.Sites.Commands.ConstructTabComplete;
import com.ColonelHedgehog.Sites.Events.InventoryClick;
import com.ColonelHedgehog.Sites.Events.PlayerCommandPreProcess;
import com.ColonelHedgehog.Sites.Events.PlayerInteract;
import com.ColonelHedgehog.Sites.Services.URLServices.DLC;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * @author Robert
 */
public class ConstructionSites extends JavaPlugin
{
    public static com.ColonelHedgehog.Sites.Core.ConstructionSites plugin;
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
        plugin.getServer().getPluginManager().registerEvents(new PlayerCommandPreProcess(), this);
        plugin.getServer().getPluginManager().registerEvents(new InventoryClick(), this);

        if (!PluginServices.isInstalled("WorldEdit") || !PluginServices.isInstalled("Vault"))
        {
           getLogger().severe("PLUGIN BREAK! Not all necessary plugins were found. WorldEdit: " + PluginServices.isInstalled("WorldEdit") + ". Vault: " + PluginServices.isInstalled("Vault"));
            plugin.setEnabled(false);
        }

        CSConfigManager.resumeBuildProcesses();
        startupPackInstall();


        // Go away, metalmikey002.
    }

    @Override
    public void onDisable()
    {

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

    private void startupPackInstall()
    {

        if(new File(plugin.getDataFolder().getParent()).listFiles() == null)
        {
            return;
        }

        for (File pack : new File(plugin.getDataFolder().getParent()).listFiles())
        {
            if (pack.getName().endsWith("#cspack"))
            {
                System.out.println("[CS | CNP] Found CSPack at " + pack.toString());
                if (pack.exists()) // Uh...? Why am I checking for this? ono phail
                {
                    System.out.println("[CS | CNP] Installing package...");

                    if (DLC.installPackage(pack))
                    {
                        System.out.println("[CS | CNP] Successfully installed files.");
                    }
                    else
                    {
                        System.out.println("[CS | CNP] Error occurred. No CSPack found. Was it deleted?");
                    }
                }
            }
        }
    }


}
