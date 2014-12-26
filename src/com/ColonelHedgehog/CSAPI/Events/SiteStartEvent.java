package com.ColonelHedgehog.CSAPI.Events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class SiteStartEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private String sitename;
    private UUID creator;
    private int sitetime;
    private double cost;
    private Location loc;

    public SiteStartEvent(String sitename, Player creator, int sitetime, double cost, Location location)
    {
        this.sitename = sitename;
        this.creator = creator.getUniqueId();
        this.sitetime = sitetime;
        this.cost = cost;
        this.loc = location;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    public Player getCreatorAsPlayer()
    {
        return Bukkit.getPlayer(creator);
    }

    public UUID getCreatorAsUUID()
    {
        return creator;
    }

    public String getSitename()
    {
        return sitename;
    }

    public double getCost()
    {
        return cost;
    }

    public int getSitetime()
    {
        return sitetime;
    }

    public Location getLocation()
    {
        return loc;
    }

    public HandlerList getHandlers()
    {
        return handlers;
    }
}