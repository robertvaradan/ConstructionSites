package com.Hand.CSAPI.Events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

/**
 * Created by Robert on 8/26/14.
 */

@Deprecated
public class SiteCompleteEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private String sitename;
    private UUID creator;

    public SiteCompleteEvent(String sitename, Player creator)
    {
        this.sitename = sitename;
        this.creator = creator.getUniqueId();
    }

    public Player getCreator()
    {
        return Bukkit.getPlayer(creator);
    }

    public String getSitename()
    {
        return sitename;
    }

    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
