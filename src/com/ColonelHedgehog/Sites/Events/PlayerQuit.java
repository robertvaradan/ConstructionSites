package com.ColonelHedgehog.Sites.Events;

import com.ColonelHedgehog.Sites.Services.CommandMenu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by ColonelHedgehog on 12/17/14.
 * You have freedom to modify given sources. Please credit me as original author.
 * Keep in mind that this is not for sale.
 */
public class PlayerQuit implements Listener
{
    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        CommandMenu.menus.remove(event.getPlayer().getUniqueId());
    }
}
