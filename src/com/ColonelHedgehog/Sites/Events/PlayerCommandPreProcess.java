package com.ColonelHedgehog.Sites.Events;

import com.ColonelHedgehog.Sites.Core.ConstructionSites;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Created by ColonelHedgehog on 12/15/14.
 * You have freedom to modify given sources. Please credit me as original author.
 * Keep in mind that this is not for sale.
 */
public class PlayerCommandPreProcess implements Listener
{
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event)
    {
        if((event.getMessage().startsWith("/reload") && event.getMessage().length() == 7) || (event.getMessage().startsWith("/rl") && event.getMessage().length() == 3) || event.getMessage().startsWith("/rl ") || event.getMessage().startsWith("/reload "))
        {
            event.getPlayer().sendMessage(ConstructionSites.Prefix + "§c§lHold it right there!");
            event.getPlayer().sendMessage(
                    "§8- §eHey! Are you §otrying§e to hurt me? Because that's what /reload does. :(" +
                    "\n§8- §ePlease don't try and use this command. I know it's quicker than" +
                    "\n§8- §erestarting, but it can break build processes and make your players" +
                    "\n§8- §elose a lot of progress. That's no fun, is it? Thank you, have a nice day.");
        }
    }
}
