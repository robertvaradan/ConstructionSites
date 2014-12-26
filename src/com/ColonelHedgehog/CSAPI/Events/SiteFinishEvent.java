package com.ColonelHedgehog.CSAPI.Events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

/**
 * Created by ColonelHedgehog on 8/26/14.
 * You have freedom to modify given sources. Please credit me as original author.
 * Keep in mind that this is not for sale.
 */
public class SiteFinishEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    ;
    private String sitename;
    private UUID creator;
    private int sitetime;
    private double cost;
    private Location loc;

    public SiteFinishEvent(String sitename, UUID creator, int sitetime, double cost, Location location)
    {
        this.sitename = sitename;
        if (creator != null)
        {
            this.creator = creator;
        }
        this.sitetime = sitetime;
        this.cost = cost;
        this.loc = location;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
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

    public int getSitetime()
    {
        return sitetime;
    }

    public double getCost()
    {
        return cost;
    }

    public Location getLoc()
    {
        return loc;
    }
}
