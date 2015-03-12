package com.ColonelHedgehog.Sites.Services;

import com.ColonelHedgehog.Sites.Commands.ConstructCmd;
import com.ColonelHedgehog.Sites.Core.ConstructionSites;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by ColonelHedgehog on 12/15/14.
 * You have freedom to modify given sources. Please credit me as original author.
 * Keep in mind that this is not for sale.
 */
public class CommandMenu
{
    public static HashMap<UUID, CommandMenu> menus = new HashMap<>();
    private static ConstructionSites plugin = ConstructionSites.plugin;
    private UUID u;
    private Inventory inv;
    private List<String> sitenames = new ArrayList<>();
    private int index = 0;

    public CommandMenu(Player p)
    {
        u = p.getUniqueId();
        sitenames.addAll(plugin.getConfig().getConfigurationSection("CS").getKeys(false));
        menus.put(p.getUniqueId(), this);
    }

    public static CommandMenu getMenu(Player p)
    {
        return menus.get(p.getUniqueId());
    }

    public List<String> getSites()
    {
        return sitenames;
    }

    public int getViewing()
    {
        return index;
    }

    public void createCommandMenu()
    {
        Player p = Bukkit.getPlayer(u);
        inv = Bukkit.createInventory(p, 9, "§3Construction§9Sites");

        if (p.hasPermission("csites.build"))
        {
            inv.addItem(getItem(ItemType.BUILD));
        }
        if (p.hasPermission("csites.scan"))
        {
            inv.addItem(getItem(ItemType.SCAN));
        }
        if (p.hasPermission("csites.admin"))
        {
            inv.addItem(getItem(ItemType.ADMIN));
        }

        inv.setItem(8, getItem(ItemType.CLOSE));
        BuildSounds.playBuildSound(BuildSounds.BuildSound.MENU_OPENED, p.getLocation(), p);
        p.openInventory(inv);
    }

    public void createBuildMenu(int index)
    {
        Player p = Bukkit.getPlayer(u);

        Inventory inv = createBaseInv(index, p, "§3C§9S §8> §6Build - " + (index + 1));
        p.openInventory(inv);
    }

    private Inventory createBaseInv(int index, Player p, String name)
    {
        inv = Bukkit.createInventory(p, 45, name);

        this.index = index;
        ItemStack left = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        SkullMeta lsm = (SkullMeta) left.getItemMeta();
        lsm.setOwner("MHF_ArrowLeft");
        lsm.setDisplayName("§7§oPrevious");
        left.setItemMeta(lsm);

        ItemStack right = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        SkullMeta rsm = (SkullMeta) right.getItemMeta();
        rsm.setOwner("MHF_ArrowRight");
        rsm.setDisplayName("§7§oNext");
        right.setItemMeta(rsm);

        ItemStack sep = new ItemStack(Material.FENCE);
        ItemMeta sepm = sep.getItemMeta();
        sepm.setDisplayName("");
        sep.setItemMeta(sepm);

        inv.setItem(18, left);
        inv.setItem(26, right);

        inv.setItem(0, sep);
        inv.setItem(9, sep);
        inv.setItem(27, sep);
        inv.setItem(36, sep);

        inv.setItem(8, sep);
        inv.setItem(17, sep);
        inv.setItem(35, sep);
        inv.setItem(44, sep);

        //System.out.println("INDEX IS: " + index);
        for (int i = 35 * index; sitenames.size() > i && i < (index > 0 ? 35 * index * 2 : 35); i++)
        {
            String site = sitenames.get(i);
            boolean cb = ConstructCmd.getAllowedBuildSite(p, site);
            //System.out.println("DISP: " + site + " CB? " + cb);
            //plugin.getLogger().info("TESTING FOR " + site + ": " + cb);
            if (cb)
            {
                ItemStack cs = new ItemStack(Material.MAP);
                ItemMeta cm = cs.getItemMeta();

                cm.setDisplayName("§9" + WordUtils.capitalizeFully(site));
                List<String> lore = new ArrayList<>();

                lore.add("§6§lDuration: §e" + plugin.getConfig().getString("CS." + site + ".Time"));
                lore.add("§6§lCost: §a$" + plugin.getConfig().getString("CS." + site + ".Cost"));

                cm.setLore(lore);
                cs.setItemMeta(cm);


                inv.addItem(cs);
            }
        }

        return inv;
    }

    public void createAdminMenu()
    {
        Player p = Bukkit.getPlayer(u);

        inv = Bukkit.createInventory(p, 9, "§9C§3S §8> §cAdmin");

        ItemStack schems = new ItemStack(Material.SIGN);
        ItemMeta sm = schems.getItemMeta();
        sm.setDisplayName("§eManage Sites");

        List<String> lore = new ArrayList<>();
        lore.add("§fClick here to view and");
        lore.add("§fdelete existing sites.");
        sm.setLore(lore);
        schems.setItemMeta(sm);

        ItemStack add = new ItemStack(Material.EMPTY_MAP);
        ItemMeta am = add.getItemMeta();
        am.setDisplayName("§9Create Site");
        List<String> alore = new ArrayList<>();
        alore.add("§fClick here to create a");
        alore.add("§fConstruction Site from");
        alore.add("§fa WorldEdit schematic.");
        am.setLore(alore);
        add.setItemMeta(am);

        inv.addItem(schems, add);
        inv.setItem(8, getItem(ItemType.CLOSE));

        p.openInventory(inv);
    }

    private ItemStack getItem(ItemType it)
    {
        Material _mat =
                it == ItemType.BUILD ? Material.STONE_PICKAXE :
                        it == ItemType.SCAN ? Material.STAINED_GLASS :
                        it == ItemType.ADMIN ? Material.REDSTONE_BLOCK :

                                Material.TNT;
        String _name =
                it == ItemType.BUILD ? "§6Build" : it == ItemType.SCAN ? "§aScan" :
                        it == ItemType.ADMIN ? "§4Admin Center" :
                                "§cClose Menu";
        List<String> _lore = new ArrayList<>();

        if (it == ItemType.BUILD)
        {
            _lore.add("§7See all available blueprints");
            _lore.add("§7that you are able to build");
            _lore.add("§7then choose one to create");
            _lore.add("§7a construction site.");
        }
        else if (it == ItemType.SCAN) // Ayy rofl
        {
            _lore.add("§7Select from a list of");
            _lore.add("§7blueprints, then scan the");
            _lore.add("§7area around you based on your");
            _lore.add("§7selection to see if you are");
            _lore.add("§7allowed to build there.");
        }
        else if (it == ItemType.ADMIN)
        {
            _lore.add("§7Access all of the administrator");
            _lore.add("§7commands to add and delete sites.");
        }

        ItemStack stack = new ItemStack(_mat, 1, it == ItemType.SCAN ? (byte) 5 : (byte) 0);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(_name);
        meta.setLore(_lore);
        stack.setItemMeta(meta);

        return stack;
    }

    public void createViewMenu(int index)
    {
        Player p = Bukkit.getPlayer(u);

        Inventory inv = createBaseInv(index, p, "§3C§9S §8> §5Manage - " + (index + 1));
        p.openInventory(inv);
    }

    public void createCreateMenu(int index)
    {
        Player p = Bukkit.getPlayer(u);

        Inventory inv = createCreateInv(index, p, "§3C§9S §8> §eCreate - " + (index + 1));

        p.openInventory(inv);
    }

    public void createScanMenu(int index)
    {
        Player p = Bukkit.getPlayer(u);

        Inventory inv = createBaseInv(index, p, "§3C§9S §8> §aScan - " + (index + 1));
        p.openInventory(inv);
    }

    private Inventory createCreateInv(int index, Player p, String name)
    {
        inv = Bukkit.createInventory(p, 45, name);

        this.index = index;
        ItemStack left = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        SkullMeta lsm = (SkullMeta) left.getItemMeta();
        lsm.setOwner("MHF_ArrowLeft");
        lsm.setDisplayName("§7§oPrevious");
        left.setItemMeta(lsm);

        ItemStack right = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        SkullMeta rsm = (SkullMeta) right.getItemMeta();
        rsm.setOwner("MHF_ArrowRight");
        rsm.setDisplayName("§7§oNext");
        right.setItemMeta(rsm);

        ItemStack sep = new ItemStack(Material.FENCE);
        ItemMeta sepm = sep.getItemMeta();
        sepm.setDisplayName("");
        sep.setItemMeta(sepm);

        inv.setItem(18, left);
        inv.setItem(26, right);

        inv.setItem(0, sep);
        inv.setItem(9, sep);
        inv.setItem(27, sep);
        inv.setItem(36, sep);

        inv.setItem(8, sep);
        inv.setItem(17, sep);
        inv.setItem(35, sep);
        inv.setItem(44, sep);

        //System.out.println("INDEX IS: " + index);
        String[] files = new File(plugin.getDataFolder().getParent() + "/WorldEdit/schematics").list();
        for (int i = 35 * index; i < (index > 0 ? 35 * index * 2 : 35); i++)
        {
            if (files.length > i)
            {
                String site = files[i];
                boolean cb = !site.matches("(.*)_\\d+-\\d+-\\d+(.*)") && site.endsWith(".schematic") && !plugin.getConfig().contains("CS." + site.substring(0, site.lastIndexOf(".")).toLowerCase());
                //plugin.getLogger().info("TESTING FOR " + site + ": " + cb);
                if (cb)
                {
                    ItemStack cs = new ItemStack(Material.EMPTY_MAP);
                    ItemMeta cm = cs.getItemMeta();

                    cm.setDisplayName("§9" + WordUtils.capitalizeFully(site));
                    cs.setItemMeta(cm);


                    inv.addItem(cs);
                }
            }
        }

        return inv;
    }

    public enum ItemType
    {
        BUILD, ADMIN, SCAN, CLOSE
    }
}
