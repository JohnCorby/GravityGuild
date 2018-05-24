package com.johncorby.gravityguild.arenaapi.event;

import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

/**
 * Called when an entity is created
 * @see org.bukkit.event.entity.EntitySpawnEvent
 */
public class EntityCreateEvent extends EntityEvent {
    private static final HandlerList handlers = new HandlerList();

    public EntityCreateEvent(Entity what) {
        super(what);
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
