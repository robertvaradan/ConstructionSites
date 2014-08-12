package com.Hand.Sites.Main;

import static com.Hand.Sites.Commands.ConstructCmd.locationCanBuild;
import static com.Hand.Sites.Commands.ConstructCmd.scanForArea;
import static com.Hand.Sites.Main.Main.Prefix;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.FilenameException;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import java.io.File;
import java.io.IOException;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

/**
 * @author desht
 *
 * A wrapper class for the WorldEdit terrain loading & saving API to make things a little
 * simple for other plugins to use.
 */
public class TerrainManager {
	private static final String EXTENSION = "schematic";

	private final WorldEdit we;
	private final LocalSession localSession;
	private final EditSession editSession;
	private final LocalPlayer localPlayer;

	/**
	 * Constructor
	 * 
	 * @param wep	the WorldEdit plugin instance
	 * @param player	the player to work with
	 */
	public TerrainManager(WorldEditPlugin wep, Player player) {
		we = wep.getWorldEdit();
		localPlayer = wep.wrapPlayer(player);
		localSession = we.getSession(localPlayer);
		editSession = localSession.createEditSession(localPlayer);		
	}

	/**
	 * Constructor
	 * 
	 * @param wep	the WorldEdit plugin instance
	 * @param world	the world to work in
	 */
	public TerrainManager(WorldEditPlugin wep, World world) {
		we = wep.getWorldEdit();
		localPlayer = null;
		localSession = new LocalSession(we.getConfiguration());
		editSession = new EditSession(new BukkitWorld(world), we.getConfiguration().maxChangeLimit);
	}

	/**
	 * Write the terrain bounded by the given locations to the given file as a MCedit format
	 * schematic.
	 * 
	 * @param saveFile	a File representing the schematic file to create
	 * @param l1	one corner of the region to save
	 * @param l2	the corner of the region to save, opposite to l1
     * @throws com.sk89q.worldedit.FilenameException
	 * @throws DataException
	 * @throws IOException
	 */
	public void saveTerrain(File saveFile, Location l1, Location l2) throws FilenameException, DataException, IOException {
		Vector min = getMin(l1, l2);
		Vector max = getMax(l1, l2);

		saveFile = we.getSafeSaveFile(localPlayer,
		                              saveFile.getParentFile(), saveFile.getName(),
		                              EXTENSION, new String[] { EXTENSION });

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
	 * @param saveFile	a File representing the schematic file to load
	 * @param loc		the location to paste the clipboard at (may be null)
     * @param p
     * @param rotation
     * @return 
	 * @throws FilenameException
	 * @throws DataException
	 * @throws IOException
	 * @throws MaxChangedBlocksException
	 * @throws EmptyClipboardException
	 */
	public boolean loadSchematic(File saveFile, Location loc, Player p, int rotation) throws FilenameException, DataException, IOException, MaxChangedBlocksException, EmptyClipboardException {
		saveFile = we.getSafeSaveFile(localPlayer,
                           saveFile.getParentFile(), saveFile.getName(),
                           EXTENSION, 
                           new String[] { EXTENSION });

		editSession.enableQueue();
		localSession.setClipboard(SchematicFormat.MCEDIT.load(saveFile));
                boolean resolved = false;
                localSession.getClipboard().rotate2D((int) rotation);
                
                
                //p.sendMessage("DEBUG ROTATION: " + rotation);
                localSession.getClipboard().setOffset(new Vector(0, localSession.getClipboard().getHeight() / 2, 0));
                localSession.getClipboard().setOrigin(new Vector(loc.getBlockX(), loc.getBlockY() / 2, loc.getBlockZ()));
                
                    int x = (localSession.getClipboard().getWidth() + 1) / 2;
                    int y = (localSession.getClipboard().getHeight() + 1) / 2;
                    int z = (localSession.getClipboard().getLength() + 1) / 2;
                
                    for(Location newloc : scanForArea(loc.getBlock().getLocation(), x, y, z,
                            Prefs.bsox + localSession.getClipboard().getOffset().getBlockX(),
                            Prefs.bsoy + localSession.getClipboard().getOffset().getBlockY(),
                            Prefs.bsoz + localSession.getClipboard().getOffset().getBlockZ()))
                    {
                        //p.sendMessage("§7DEBUG: §aScanning area: §6" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ".");
                        if(!locationCanBuild(newloc, p) && !resolved)
                        {
                                p.sendMessage(Prefix + "§4ERROR: §6Could not create construction site. It overlaps a region you can't build in.");
                                resolved = true;
                                if(loc.getBlock().getType() == Material.SIGN || loc.getBlock().getType() == Material.SIGN_POST || loc.getBlock().getType() == Material.WALL_SIGN)
                                {
                                    loc.getBlock().breakNaturally();
                                }
                                return false;
                        }
                        
                    }

                    if(!resolved)
                    {
                        localSession.getClipboard().place(editSession, new Vector(loc.getBlockX() - (localSession.getClipboard().getWidth() / 2), loc.getBlockY(), (loc.getBlockZ() - localSession.getClipboard().getWidth() / 2)), true);
                    }

                    editSession.flushQueue();
                    
		we.flushBlockBag(localPlayer, editSession);
                return true;
	}

	/**
	 * Load the data from the given schematic file and paste it at the saved clipboard's origin.
	 * 
	 * @param saveFile
	 * @throws FilenameException
	 * @throws DataException
	 * @throws IOException
	 * @throws MaxChangedBlocksException
	 * @throws EmptyClipboardException
	 */
	public void loadSchematic(File saveFile) throws FilenameException, DataException, IOException, MaxChangedBlocksException, EmptyClipboardException {
		loadSchematic(saveFile, null, null, 0);
	}

	private Vector getPastePosition(Location loc) throws EmptyClipboardException {
		/*if (loc == null) 
			return localSession.getClipboard().getOrigin();
		else */
			return new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}

	private Vector getMin(Location l1, Location l2) {
		return new Vector(
		                  Math.min(l1.getBlockX(), l2.getBlockX()),
		                  Math.min(l1.getBlockY(), l2.getBlockY()),
		                  Math.min(l1.getBlockZ(), l2.getBlockZ())
				);
	}

	private Vector getMax(Location l1, Location l2) {
		return new Vector(
		                  Math.max(l1.getBlockX(), l2.getBlockX()),
		                  Math.max(l1.getBlockY(), l2.getBlockY()),
		                  Math.max(l1.getBlockZ(), l2.getBlockZ())
				);
	}


    public static BlockFace getPlayerDirection(Player player)
    {
 
        BlockFace dir;
     
        float y = player.getLocation().getYaw() + 90; // Offset since we're just using this for signs, and signs have weeeeird rotations. :I
     
        if( y < 0 ){y += 360;}
     
        y %= 360;
     
        int i = (int)((y+8) / 22.5);
     
        if(i == 0 || i == 1 || i == 2 || i == 3){dir = BlockFace.WEST;}
        else if(i == 4 || i == 5 || i == 6 || i == 7){dir = BlockFace.NORTH;}
        else if(i == 8 || i == 9 || i == 10 || i == 11){dir = BlockFace.EAST;}
        else if(i == 12 || i == 13 || i == 14 || i == 15){dir = BlockFace.SOUTH;}
        else {dir = BlockFace.WEST;}
        // You're a nerd if you're reading this.
     
        return dir;
 
    }

    public static float getFaceYaw(BlockFace dir)
    {
      
        float y;
        if(dir == BlockFace.WEST){y = 90;}
        else if(dir == BlockFace.NORTH){y = 180;}
        else if(dir == BlockFace.EAST){y = 270;}
        else if(dir == BlockFace.SOUTH){y = 0;}
        else {y = 90;}
        // You're a nerd if you're reading this.
     
        return y;
 
    }

    public boolean testLoadSchematic(File saveFile, Location loc, Player p, int rotation, boolean placetest) throws FilenameException, DataException, IOException, MaxChangedBlocksException, EmptyClipboardException 
    {
            saveFile = we.getSafeSaveFile(localPlayer,
                       saveFile.getParentFile(), saveFile.getName(),
                       EXTENSION, 
                       new String[] { EXTENSION });

            editSession.enableQueue();
            localSession.setClipboard(SchematicFormat.MCEDIT.load(saveFile));
            boolean resolved = false;
            localSession.getClipboard().rotate2D((int) rotation);


            //p.sendMessage("DEBUG ROTATION: " + rotation);
            localSession.getClipboard().setOffset(new Vector(0, localSession.getClipboard().getHeight() / 2, 0));
            localSession.getClipboard().setOrigin(new Vector(loc.getBlockX(), loc.getBlockY() / 2, loc.getBlockZ()));

                int x = (localSession.getClipboard().getWidth() + 1) / 2;
                int y = (localSession.getClipboard().getHeight() + 1) / 2;
                int z = (localSession.getClipboard().getLength() + 1) / 2;
                
                int use = x;
                
                if(y > x && y > z)
                {
                    use = y;
                }
                
                if(z > y && z > x)
                {
                    use = z;
                }
                        
                        

                for(Location newloc : scanForArea(loc.getBlock().getLocation(), use, use / 2, use,
                        Prefs.bsox + localSession.getClipboard().getOffset().getBlockX(),
                        Prefs.bsoy + localSession.getClipboard().getOffset().getBlockY(),
                        Prefs.bsoz + localSession.getClipboard().getOffset().getBlockZ()))
                {
                    //p.sendMessage("§7DEBUG: §aScanning area: §6" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ".");
                    if(!locationCanBuild(newloc, p) && !resolved)
                    {
                            p.sendMessage(Prefix + "§4ERROR: §6Could not create construction site. It overlaps a region you can't build in.");
                            resolved = true;
                            
                            return false;
                    }
                    
                    if(placetest)
                    {
                        if(locationCanBuild(newloc, p))
                        {
                            p.playEffect(newloc, Effect.SMOKE, 0);
                        }
                    }
                    
                        /*Bukkit.getScheduler().scheduleSyncDelayedTask(Main.ConstructionSites, new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                
                            }
                        }, 20);*/

                    

                }

                editSession.flushQueue();

            we.flushBlockBag(localPlayer, editSession);
            return true;
    }
}