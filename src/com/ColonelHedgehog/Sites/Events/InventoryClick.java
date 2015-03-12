package com.ColonelHedgehog.Sites.Events;

import com.ColonelHedgehog.Sites.Core.ConstructionSites;
import com.ColonelHedgehog.Sites.Services.BuildSounds;
import com.ColonelHedgehog.Sites.Services.CommandMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Arrays;

/**
 * Created by ColonelHedgehog on 12/17/14.
 * You have freedom to modify given sources. Please credit me as original author.
 * Keep in mind that this is not for sale.
 */
public class InventoryClick implements Listener
{
    private ConstructionSites plugin = ConstructionSites.plugin;

    @EventHandler
    public void onClick(final InventoryClickEvent event)
    {
        Inventory inv = event.getInventory();
        final Player p = (Player) event.getWhoClicked();
        final ItemStack is = event.getCurrentItem();

        if (is == null)
        {
            return;
        }
        if (inv.getName().equals("§3Construction§9Sites"))
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
                    else if (is.getType() == Material.REDSTONE_BLOCK)
                    {
                        menu.createAdminMenu();
                    }
                    else if (is.getType() == Material.STAINED_GLASS)
                    {
                        menu.createScanMenu(0);
                    }
                    else if (is.getType() == Material.TNT)
                    {
                        p.closeInventory();
                    }
                }
            }.runTask(plugin);

            p.playSound(p.getLocation(), Sound.NOTE_BASS_GUITAR, 3, 1);
            event.setCancelled(true);

        }
        else if (inv.getName().startsWith("§3C§9S §8> §6Build - "))
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
                        if (menu.getSites().size() / 35 > menu.getViewing())
                            menu.createBuildMenu(menu.getViewing() + 1);
                    }
                    else if (isArrowLeft(is))
                    {
                        if (menu.getViewing() > 0)
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
        else if (inv.getName().startsWith("§3C§9S §8> §aScan - "))
        {
            final CommandMenu menu = CommandMenu.getMenu(p);

            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    if (is.getType() == Material.MAP)
                    {
                        p.chat("/construct scan " + is.getItemMeta().getDisplayName().substring(2));
                        p.closeInventory();
                    }
                    else if (isArrowRight(is))
                    {
                        if (menu.getSites().size() / 35 > menu.getViewing())
                            menu.createScanMenu(menu.getViewing() + 1);
                    }
                    else if (isArrowLeft(is))
                    {
                        if (menu.getViewing() > 0)
                        {
                            menu.createScanMenu(menu.getViewing() - 1);
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
        else if (inv.getName().startsWith("§3C§9S §8> §eCreate -"))
        {
            final CommandMenu menu = CommandMenu.getMenu(p);

            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    if (is.getType() == Material.EMPTY_MAP)
                    {
                        addSite(is.getItemMeta().getDisplayName().substring(2));
                    }
                    else if (isArrowRight(is))
                    {
                        if (menu.getSites().size() / 35 > menu.getViewing())
                            menu.createCreateMenu(menu.getViewing() + 1);
                    }
                    else if (isArrowLeft(is))
                    {
                        if (menu.getViewing() > 0)
                        {
                            menu.createCreateMenu(menu.getViewing() - 1);
                        }
                        else
                        {
                            menu.createCommandMenu();
                        }
                    }
                }

                private void addSite(String name)
                {
                    String site = name.replace(".schematic", "");
                    Inventory inv = Bukkit.createInventory(p, 27, "§aAdd §9" + site);

                    ItemStack yes = new ItemStack(Material.EMERALD_BLOCK);
                    ItemStack no = new ItemStack(Material.REDSTONE_BLOCK);
                    ItemMeta ymeta = yes.getItemMeta();
                    ItemMeta nmeta = no.getItemMeta();
                    nmeta.setDisplayName("§c§lDon't Add This Site");
                    ymeta.setDisplayName("§a§lAdd This Site");
                    ymeta.setLore(Arrays.asList("§aClick §7to add this site with", "§7all of the settings you've chosen."));
                    yes.setItemMeta(ymeta);
                    no.setItemMeta(nmeta);

                    inv.setItem(25, yes);
                    inv.setItem(26, no);

                    ItemStack hr = new ItemStack(Material.COAL, 0);
                    ItemStack min = new ItemStack(Material.COAL, 0);
                    ItemStack sec = new ItemStack(Material.COAL, 30);

                    for(int i = 0; i < 9; i++)
                    {
                        ItemStack count = new ItemStack(Material.EMERALD, 0);
                        ItemMeta cmeta = count.getItemMeta();
                        cmeta.setDisplayName("§aPrice §7(" + evalPlace(i) + ")");
                        count.setItemMeta(cmeta);
                        inv.setItem(i + 9, count);
                    }

                    ItemMeta hrm = hr.getItemMeta();
                    ItemMeta minm = min.getItemMeta();
                    ItemMeta secm = sec.getItemMeta();

                    hrm.setDisplayName("§a§lHours");
                    minm.setDisplayName("§a§lMinutes");
                    secm.setDisplayName("§a§lSeconds");

                    hrm.setLore(Arrays.asList("§8- §aLeft-click §7to ", "§bincrease§7 the number of §bhours.",
                            "§8- §aRight-click §7to ", "§bdecrease§7 the number of §bhours.",
                            "§8- §aShift-left-click §7to ", "§badd 10§7 to the number of §bhours.",
                            "§8- §aShift-right-click §7to ", "§bsubtract 10§7 from the number of §bhours."));
                    minm.setLore(Arrays.asList("§8- §aLeft-click §7to ", "§bincrease§7 the number of §bminutes.",
                            "§8- §aRight-click §7to ", "§bdecrease§7 the number of §bminutes.",
                            "§8- §aShift-left-click §7to ", "§badd 10§7 to the number of §bminutes.",
                            "§8- §aShift-right-click §7to ", "§bsubtract 10§7 from the number of §bminutes."));
                    secm.setLore(Arrays.asList("§8- §aLeft-click §7to ", "§bincrease§7 the number of §bseconds.",
                            "§8- §aRight-click §7to ", "§bincrease§7 the number of §bseconds.",
                            "§8- §aShift-left-click §7to ", "§badd 10§7 to the number of §bseconds.",
                            "§8- §aShift-right-click §7to ", "§bsubtract 10§7 from the number of §bseconds."));

                    hr.setItemMeta(hrm);
                    min.setItemMeta(minm);
                    sec.setItemMeta(secm);

                    inv.setItem(3, hr);
                    inv.setItem(4, min);
                    inv.setItem(5, sec);

                    p.openInventory(inv);
                    //inv.setItem();
                }

                private String evalPlace(int i)
                {
                    switch(i)
                    {
                        case 8:
                            return "ones";
                        case 7:
                            return "tens";
                        case 6:
                            return "hundreds";
                        case 5:
                            return "thousands";
                        case 4:
                            return "ten-thousands";
                        case 3:
                            return "hundred-thousands";
                        case 2:
                            return "millions";
                        case 1:
                            return "ten-millions";
                        case 0:
                            return "hundred-millions";
                        default:
                            return "ones";
                    }
                }
            }.runTask(plugin);

            event.setCancelled(true);
        }
        else if (inv.getName().startsWith("§3C§9S §8> §5Manage -"))
        {
            final CommandMenu menu = CommandMenu.getMenu(p);

            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    if (is.getType() == Material.MAP)
                    {
                        areYeShur(is.getItemMeta().getDisplayName().substring(2));
                    }
                    else if (isArrowRight(is))
                    {
                        if (menu.getSites().size() / 35 > menu.getViewing())
                            menu.createViewMenu(menu.getViewing() + 1);
                    }
                    else if (isArrowLeft(is))
                    {
                        if (menu.getViewing() > 0)
                        {
                            menu.createViewMenu(menu.getViewing() - 1);
                        }
                        else
                        {
                            menu.createCommandMenu();
                        }
                    }
                    p.playSound(p.getLocation(), Sound.NOTE_BASS_GUITAR, 3, 1);
                }

                // "No making other unrelated methods in runnables?" YOU CAN'T TELL ME WHAT TO DO!
                private void areYeShur(String str)
                {
                    Inventory inv = Bukkit.createInventory(p, 9, "§cRemove §9" + str);
                    ItemStack yes = new ItemStack(Material.EMERALD_BLOCK);
                    ItemStack no = new ItemStack(Material.REDSTONE_BLOCK);
                    ItemMeta ymeta = yes.getItemMeta();
                    ItemMeta nmeta = no.getItemMeta();
                    nmeta.setDisplayName("§a§lDon't Delete This Site");
                    ymeta.setDisplayName("§c§lDelete This Site");
                    ymeta.setLore(Arrays.asList("§aLeft-click §7to remove this site.", "§aRight-click §7to remove this site", "§7and its matching schematic."));
                    yes.setItemMeta(ymeta);
                    no.setItemMeta(nmeta);
                    inv.addItem(yes);
                    inv.addItem(no);
                    p.openInventory(inv);
                }
            }.runTask(plugin);
            event.setCancelled(true);
        }
        else if (inv.getName().startsWith("§aAdd §9"))
        {
            final CommandMenu menu = CommandMenu.getMenu(p);
            String name = inv.getName().replace("§aAdd §9", "");

            if(is.getType() == Material.COAL)
            {
                inv.setItem(inv.first(is), smartInc(is, event.getClick()));
            }
            else if(is.getType() == Material.EMERALD)
            {
                if(event.getClick() == ClickType.RIGHT)
                {
                    int a = is.getAmount() - 1;
                    if(is.getAmount() > 0)
                    {
                        inv.setItem(inv.first(is), new ItemStack(Material.EMERALD, a));
                    }
                }
                else if (event.getClick() == ClickType.LEFT)
                {
                    if (is.getAmount() < 9)
                    {
                        is.setAmount(is.getAmount() + 1);
                    }
                    inv.setItem(inv.first(is), is);
                }
            }
            else if(is.getType() == Material.REDSTONE_BLOCK)
            {
                menu.createCreateMenu(0);
            }
            else if (is.getType() == Material.EMERALD_BLOCK)
            {
                p.chat("/construct admin addsite " + name + " " + evalTime(inv) + " " + evalPrice(inv) + ".0");
                menu.createCreateMenu(0);
                BuildSounds.playBuildSound(BuildSounds.BuildSound.SITE_BUILT, p.getLocation(), p);
            }

            p.playSound(p.getLocation(), Sound.NOTE_PLING, 3, 1);
            event.setCancelled(true);
        }
        else if(inv.getName().startsWith("§cRemove §9"))
        {
            final CommandMenu menu = CommandMenu.getMenu(p);
            String name = inv.getName().replace("§cRemove §9", "");
            if(is.getType() == Material.EMERALD_BLOCK)
            {
                if(event.getClick() == ClickType.LEFT)
                {
                    p.chat("/construct admin delsite " + name);
                    p.playSound(p.getLocation(), Sound.EXPLODE, 3, 2);
                }
                else if(event.getClick() == ClickType.RIGHT)
                {
                    p.chat("/construct admin delsite " + name);
                    boolean tru = new File(plugin.getDataFolder().getParent() + "/WorldEdit/schematic/" + name + ".schematic").delete();

                    if(tru)
                    {
                        p.sendMessage(ConstructionSites.Prefix + "§cDeleted file: §e\"§6" + name + "§e\"");
                    }

                    p.playSound(p.getLocation(), Sound.EXPLODE, 3, 2);
                }
            }
            else
            {
                menu.createViewMenu(0);
            }

            event.setCancelled(true);
            p.playSound(p.getLocation(), Sound.NOTE_BASS_GUITAR, 3, 1);
            menu.createViewMenu(0);
        }
        else if (inv.getName().startsWith("§9C§3S §8> §cAdmin"))
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

    private String evalPrice(Inventory inv)
    {
        StringBuilder builder = new StringBuilder();

        for(int i = 9; i < 18; i++)
        {
            builder.append(inv.getItem(i).getAmount());
        }

        return builder.toString();
    }

    private String evalTime(Inventory inv)
    {
        int h = inv.getItem(3).getAmount();
        int m = inv.getItem(4).getAmount();
        int s = inv.getItem(5).getAmount();
        return h + ":" + m + ":" + s;
    }

    private ItemStack smartInc(ItemStack is, ClickType click)
    {
        int a = is.getAmount();
        int am = a;

        if(click == ClickType.LEFT)
        {
            if(a < 59)
            {
                am++;
            }
        }
        else if(click == ClickType.RIGHT)
        {
            if(a > 0)
            {
                am--;
            }
        }
        else if(click == ClickType.SHIFT_LEFT)
        {
            if(a < 50)
            {
                am += 10;
            }
        }
        else if(click == ClickType.SHIFT_RIGHT)
        {
            if(a > 9)
            {
                am -= 10;
            }
        }

        ItemMeta meta = is.getItemMeta();
        ItemStack nu = new ItemStack(Material.COAL, am);
        nu.setItemMeta(meta);
        return nu;
    }

    private boolean isArrowLeft(ItemStack is)
    {
        if (is.hasItemMeta())
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
