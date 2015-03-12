package com.ColonelHedgehog.Sites.Services;

import com.ColonelHedgehog.Sites.Core.Prefs;
import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.schematic.SchematicFormat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import static com.ColonelHedgehog.Sites.Commands.ConstructCmd.locationCanBuild;
import static com.ColonelHedgehog.Sites.Commands.ConstructCmd.scanForArea;
import static com.ColonelHedgehog.Sites.Core.ConstructionSites.Prefix;
import static com.ColonelHedgehog.Sites.Core.ConstructionSites.plugin;

/**
 * @author desht
 *         <p/>
 *         A wrapper class for the WorldEdit terrain loading & saving API to make things a little
 *         simple for other plugins to use.
 */
@SuppressWarnings("deprecation")
public class CSBuilder
{
    private static final String EXTENSION = "schematic";

    private final WorldEdit we;
    private final LocalSession localSession;
    private final EditSession editSession;
    private final LocalPlayer localPlayer;

    /**
     * Constructor
     *
     * @param wep    the WorldEdit plugin instance
     * @param player the player to work with
     */
    public CSBuilder(WorldEditPlugin wep, Player player)
    {
        we = wep.getWorldEdit();
        localPlayer = wep.wrapPlayer(player);
        localSession = we.getSession(localPlayer);
        editSession = localSession.createEditSession(localPlayer);
    }

    /**
     * Constructor
     *
     * @param wep   the WorldEdit plugin instance
     * @param world the world to work in
     */
    public CSBuilder(WorldEditPlugin wep, World world)
    {
        we = wep.getWorldEdit();
        localPlayer = null;
        localSession = new LocalSession(we.getConfiguration());
        editSession = new EditSession(new BukkitWorld(world), we.getConfiguration().maxChangeLimit);
    }

    public static BlockFace getPlayerDirection(Location player)
    {

        BlockFace dir;

        float y = player.getYaw() + 90; // Offset since we're just using this for signs, and signs have weeeeird rotations. :I

        if (y < 0)
        {
            y += 360;
        }

        y %= 360;

        int i = (int) ((y + 8) / 22.5);

        if (i == 0 || i == 1 || i == 2 || i == 3)
        {
            dir = BlockFace.WEST;
        }
        else if (i == 4 || i == 5 || i == 6 || i == 7)
        {
            dir = BlockFace.NORTH;
        }
        else if (i == 8 || i == 9 || i == 10 || i == 11)
        {
            dir = BlockFace.EAST;
        }
        else if (i == 12 || i == 13 || i == 14 || i == 15)
        {
            dir = BlockFace.SOUTH;
        }
        else
        {
            dir = BlockFace.WEST;
        }
        // You're a nerd if you're reading this.

        return dir;

    }

    public static float getFaceYaw(BlockFace dir)
    {

        float y;
        if (dir == BlockFace.WEST)
        {
            y = 90;
        }
        else if (dir == BlockFace.NORTH)
        {
            y = 180;
        }
        else if (dir == BlockFace.EAST)
        {
            y = 270;
        }
        else if (dir == BlockFace.SOUTH)
        {
            y = 0;
        }
        else
        {
            y = 90;
        }
        // You're a nerd if you're reading this.

        return y;

    }

    /**
     * Write the terrain bounded by the given locations to the given file as a MCedit format
     * schematic.
     *
     * @param saveFile a File representing the schematic file to create
     * @param l1       one corner of the region to save
     * @param l2       the corner of the region to save, opposite to l1
     * @throws IOException
     */
    public void saveTerrain(File saveFile, Location l1, Location l2) throws Exception
    {
        Vector min = getMin(l1, l2);
        Vector max = getMax(l1, l2);

        saveFile = we.getSafeSaveFile(localPlayer,
                saveFile.getParentFile(), saveFile.getName(),
                EXTENSION, EXTENSION);

        editSession.enableQueue();
        CuboidClipboard clipboard = new CuboidClipboard(max.subtract(min).add(new Vector(1, 1, 1)), min);
        clipboard.copy(editSession);
        SchematicFormat.MCEDIT.save(clipboard, saveFile);
        editSession.flushQueue();
    }

    /**
     * Load the data from the given schematic file and paste it at the given location.  If the location is null, then
     * paste it at the saved data's origin.
     *
     * @param saveFile a File representing the schematic file to load
     * @param loc      the location to paste the clipboard at (may be null)
     * @throws IOException
     * @throws MaxChangedBlocksException
     * @throws EmptyClipboardException
     */
    public boolean loadSchematic(File saveFile, Location loc, Player p, int rotation, boolean noair) throws Exception
    {
        saveFile = we.getSafeSaveFile(localPlayer,
                saveFile.getParentFile(), saveFile.getName(),
                EXTENSION,
                EXTENSION);

        EditSession es = new EditSession(new BukkitWorld(loc.getWorld()), Integer.MAX_VALUE);
        //es.enableQueue();
        CuboidClipboard cc = SchematicFormat.MCEDIT.load(saveFile);
        boolean resolved = false;
        //Bukkit.broadcastMessage("Rotation is: " + rotation);
        cc.rotate2D(rotation);


        //p.sendMessage("DEBUG ROTATION: " + rotation);
        cc.setOffset(new Vector(0, cc.getHeight() / 2, 0));
        cc.setOrigin(new Vector(loc.getBlockX(), loc.getBlockY() / 2, loc.getBlockZ()));

        int x = (cc.getWidth() + 1) / 2;
        int y = (cc.getHeight() + 1) / 2;
        int z = (cc.getLength() + 1) / 2;

        for (Location newloc : scanForArea(loc.getBlock().getLocation(), x, y, z,
                Prefs.bsox + cc.getOffset().getBlockX(),
                Prefs.bsoy + cc.getOffset().getBlockY(),
                Prefs.bsoz + cc.getOffset().getBlockZ()))
        {
            //p.sendMessage("§7DEBUG: §aScanning area: §6" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ".");
            if (p != null && !locationCanBuild(newloc, p))
            {
                p.sendMessage(Prefix + "§4ERROR: §6Could not create construction site. It overlaps a region you can't build in.");
                resolved = true;
                return false;
            }

        }

        cc.place(es, new Vector(loc.getBlockX() - (cc.getWidth() / 2), loc.getBlockY(), (loc.getBlockZ() - cc.getLength() / 2)), noair);

        //es.flushQueue();

        we.flushBlockBag(localPlayer, es);
        return true;
    }

    /**
     * Load the data from the given schematic file and paste it at the saved clipboard's origin.
     *
     * @throws IOException
     * @throws MaxChangedBlocksException
     * @throws EmptyClipboardException
     */
    public void loadSchematic(File saveFile) throws Exception
    {
        loadSchematic(saveFile, null, null, 0, true);
    }

    private Vector getPastePosition(Location loc) throws EmptyClipboardException
    {
        /*if (loc == null)
			return cc.getOrigin();
		else */
        return new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    private Vector getMin(Location l1, Location l2)
    {
        return new Vector(
                Math.min(l1.getBlockX(), l2.getBlockX()),
                Math.min(l1.getBlockY(), l2.getBlockY()),
                Math.min(l1.getBlockZ(), l2.getBlockZ())
        );
    }

    private Vector getMax(Location l1, Location l2)
    {
        return new Vector(
                Math.max(l1.getBlockX(), l2.getBlockX()),
                Math.max(l1.getBlockY(), l2.getBlockY()),
                Math.max(l1.getBlockZ(), l2.getBlockZ())
        );
    }

    public boolean testLoadSchematic(File saveFile, final Location loc, Player p, int rotation, boolean placetest) throws Exception
    {
        boolean tr = true;
        saveFile = we.getSafeSaveFile(localPlayer,
                saveFile.getParentFile(), saveFile.getName(),
                EXTENSION,
                EXTENSION);

        EditSession es = new EditSession(new BukkitWorld(loc.getWorld()), Integer.MAX_VALUE);
        //es.enableQueue();
        CuboidClipboard cc = SchematicFormat.MCEDIT.load(saveFile);

        cc.rotate2D(rotation);


        //p.sendMessage("DEBUG ROTATION: " + rotation);
        cc.setOffset(new Vector(0, cc.getHeight() / 2, 0));
        cc.setOrigin(new Vector(loc.getBlockX(), loc.getBlockY() / 2, loc.getBlockZ()));

        int x = (cc.getWidth() + 1) / 2;
        int y = (cc.getHeight() + 1) / 2;
        int z = (cc.getLength() + 1) / 2;

        boolean v = Prefs.ve;
        final HashSet<Location> resolver = new HashSet<>();

        for (Location newloc : scanForArea(loc.getBlock().getLocation(), x, y, z,
                Prefs.bsox + cc.getOffset().getBlockX(),
                Prefs.bsoy + cc.getOffset().getBlockY(),
                Prefs.bsoz + cc.getOffset().getBlockZ()))
        {
            //p.sendMessage("§7DEBUG: §aScanning area: §6" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ".");
            if (!locationCanBuild(newloc, p))
            {
                tr = false;
            }

            int ny = (y * 2) + Prefs.bsoy;

            if (Math.abs(loc.getBlockX() - newloc.getBlockX()) == x + Prefs.bsox || Math.abs(loc.getBlockY() - newloc.getBlockY()) == (ny % 2 == 0 ? ny : ny + (ny < 0 ? -1 : 1)) || Math.abs(loc.getBlockZ() - newloc.getBlockZ()) == z + Prefs.bsoz)
            {
                if (locationCanBuild(newloc, p) && v)
                {
                    p.sendBlockChange(newloc, Material.STAINED_GLASS, (byte) 5);
                }
                else
                {
                    p.sendBlockChange(newloc, Material.STAINED_GLASS, (byte) 14);
                }
                resolver.add(newloc);
            }
        }


        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for (Location r : resolver)
                {
                    BlockState state = r.getBlock().getState();
                    state.update(true);
                }
            }
        }.runTaskLater(plugin, Prefs.vto);

        //es.flushQueue();

        we.flushBlockBag(localPlayer, es);

        return tr;
    }
}