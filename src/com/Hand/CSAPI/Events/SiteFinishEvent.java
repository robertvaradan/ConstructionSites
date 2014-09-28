package com.Hand.CSAPI.Events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

/**
 * Created by Robert on 8/26/14.
 */
public class SiteFinishEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();;
    private String sitename;
    private UUID creator;
    private int sitetime;
    private double cost;
    private Location loc;

    public SiteFinishEvent(String sitename, UUID creator, int sitetime, double cost, Location location)
    {
        this.sitename = sitename;
        this.creator = creator;
        this.sitetime = sitetime;
        this.cost = cost;
        this.loc = location;
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
