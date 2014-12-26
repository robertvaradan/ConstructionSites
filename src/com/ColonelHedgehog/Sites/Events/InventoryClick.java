package com.ColonelHedgehog.Sites.Events;

import com.ColonelHedgehog.Sites.Core.ConstructionSites;
import com.ColonelHedgehog.Sites.Services.CommandMenu;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by ColonelHedgehog on 12/17/14.
 * You have freedom to modify given sources. Please credit me as original author.
 * Keep in mind that this is not for sale.
 */
public class InventoryClick implements Listener
{
    private ConstructionSites plugin = ConstructionSites.plugin;

    @EventHandler
    public void onClick(InventoryClickEvent event)
    {
        Inventory inv = event.getInventory();
        final Player p = (Player) event.getWhoClicked();
        final ItemStack is = event.getCurrentItem();

        if (is == null)
        {
            return;
        }
        if(inv.getName().equals("§3Construction§9Sites"))
        {
            final CommandMenu menu = CommandMenu.getMenu(p);

            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    if (is.getType() == Material.STONE_PICKAXE)
                    {
                        menu.createBuildMenu(0);
                    }
                    else if(is.getType() == Material.REDSTONE_BLOCK)
                    {
                        //menu.createAdminMenu();
                    }
                    else if(is.getType() == Material.TNT)
                    {
                        p.closeInventory();
                    }
                }
            }.runTask(plugin);

            p.playSound(p.getLocation(), Sound.NOTE_BASS_GUITAR, 3, 1);
            event.setCancelled(true);

        }
        else if(inv.getName().startsWith("§3C§9S §8> §6Build - "))
        {
            final CommandMenu menu = CommandMenu.getMenu(p);

            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    if (is.getType() == Material.MAP)
                    {
                        p.chat("/construct build " + is.getItemMeta().getDisplayName().substring(2));
                        p.closeInventory();
                    }
                    else if (isArrowRight(is))
                    {
                        if(menu.getSites().size() / 35 > menu.getViewing())
                        menu.createBuildMenu(menu.getViewing() + 1);
                    }
                    else if (isArrowLeft(is))
                    {
                        if(menu.getViewing() > 0)
                        {
                            menu.createBuildMenu(menu.getViewing() - 1);
                        }
                        else
                        {
                            menu.createCommandMenu();
                        }
                    }
                    p.playSound(p.getLocation(), Sound.NOTE_BASS_GUITAR, 3, 1);
                }
            }.runTask(plugin);
            event.setCancelled(true);
        }
        else if(inv.getName().startsWith("§9C§3S §8> §cAdmin"))
        {
            final CommandMenu menu = CommandMenu.getMenu(p);

            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    if (is.getType() == Material.SIGN)
                    {
                        menu.createViewMenu(0);
                    }
                    else if (is.getType() == Material.EMPTY_MAP)
                    {
                        menu.createCreateMenu(0);
                    }
                    else if (is.getType() == Material.TNT)
                    {
                        menu.createCommandMenu();
                    }
                    p.playSound(p.getLocation(), Sound.NOTE_BASS_GUITAR, 3, 1);
                }
            }.runTask(plugin);
            event.setCancelled(true);
        }
    }

    private boolean isArrowLeft(ItemStack is)
    {
        if(is.hasItemMeta())
        {
            ItemMeta im = is.getItemMeta();


            return im.getDisplayName().equals("§7§oPrevious");
        }

        return false;
    }

    private boolean isArrowRight(ItemStack is)
    {
        if (is.hasItemMeta())
        {
            ItemMeta im = is.getItemMeta();


            return im.getDisplayName().equals("§7§oNext");
        }

        return false;
    }
}
