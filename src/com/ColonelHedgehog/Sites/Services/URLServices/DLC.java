/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ColonelHedgehog.Sites.Services.URLServices;

import com.ColonelHedgehog.Sites.Core.ConstructionSites;
import org.apache.commons.io.FileUtils;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Robert
 */
public class DLC
{
    private static ConstructionSites plugin = ConstructionSites.plugin;
    private static boolean reading = false;

    public static void parseCrossNetworkParseableCS(File f)
    {
        /*try {
            BufferedReader br = null;
            br = new BufferedReader(new FileReader(f));
            int linenum = 1;
            String read;
            
            while ((read = br.readLine()) != null) 
            {
                if
            }
        
        } 
        catch (FileNotFoundException ex) 
        {
            Logger.getLogger(DLC.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(DLC.class.getName()).log(Level.SEVERE, null, ex);
        }
    */
    }

    public static void parseCrossNetworkParseable(final File f)
    {
        new BukkitRunnable()
        {

            @Override
            public void run()
            {

                try
                {
                    BufferedReader br = null;
                    try
                    {
                        br = new BufferedReader(new FileReader(f));
                        int linenum = 1;
                        String read;

                        while ((read = br.readLine()) != null)
                        {
                            //System.out.println("[CS | CNP] Reading... " + linenum + ". Parsing... " + reading);
                            if (read.startsWith("~ (action#copyfiles) "))
                            {
                                System.out.println("[CS | CNP] Found action#copyfiles at line " + linenum + ". Parsing...");
                                String[] sy = read.split(" : ");
                                File source = new File(sy[0].replace("~ (action#copyfiles) ", "").replace("#pluginsfolder#", plugin.getDataFolder().getParent()));
                                File dest = new File(sy[1].replace("#pluginsfolder#", plugin.getDataFolder().getParent()));

                                System.out.println("[CS | CNP] Copying source: " + source.toString());
                                System.out.println("[CS | CNP] Copying to destination: " + dest.toString());
                                int schematicscount = 0;

                                File[] files = source.listFiles();

                                if (files == null)
                                {
                                    System.out.println("[CS | CNP] ERROR: No files in directory: " + source.getPath());
                                    return;
                                }
                                for (File schematic : files)
                                {
                                    FileUtils.copyFileToDirectory(schematic, dest);
                                    schematicscount++;
                                }

                                System.out.println("[CS | CNP] Copied files. Total copied: " + schematicscount);
                            }
                            else if (read.startsWith("~ (action#writetoconfig) "))
                            {
                                //System.out.println("[CS | CNP] Found action#writetoconfig at line " + linenum + ". Parsing...");
                                //String[] sy = read.split(" : ");

                                //File source = new File(sy[0].replace("~ (action#writetoconfig) ", "").replace("#pluginsfolder#", plugin.getDataFolder().getParent()));
                                //parseCrossNetworkParseableScript(source);
                        /* ===== Not nescessary right now! ===== */
                            }
                            linenum++;
                        }
                    }
                    catch (IOException ex)
                    {
                        Logger.getLogger(DLC.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    finally
                    {
                        try
                        {
                            if (br != null)
                            {
                                br.close();
                            }
                            else
                            {
                                System.out.println("[CS | CNP] Failed to close buffered reader!");
                            }
                        }
                        catch (IOException ex)
                        {
                            Logger.getLogger(DLC.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    System.out.println("[CS | CNP] Finished parsing. Deleting package...");
                    FileUtils.deleteDirectory(f.getParentFile());
                }
                catch (IOException ex)
                {
                    Logger.getLogger(DLC.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }.runTaskAsynchronously(plugin);
    }

    public static boolean installPackage(File f)
    {
        if (f.isDirectory())
        {
            if (new File(f.getPath() + "/install.cnp").exists())
            {
                parseCrossNetworkParseable(new File(f.getPath() + "/install.cnp"));
                return true;
            }
            else
            {
                System.out.println("[CS | CNP] Could not parse " + f.getPath() + "/install.cnp" + ". Does not exist.");
            }
        }
        else
        {
            System.out.println("[CS | CNP] Could not load " + f.getName() + ". It is a file.");
        }
        return false;
    }
}
