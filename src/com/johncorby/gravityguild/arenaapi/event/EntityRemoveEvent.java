package com.johncorby.gravityguild.arenaapi.event;

import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

/**
 * Called when an entity is removed
 * @see org.bukkit.event.entity.EntityDeathEvent
 */
public class EntityRemoveEvent extends EntityEvent {
    private static final HandlerList handlers = new HandlerList();

    public EntityRemoveEvent(Entity what) {
        super(what);
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
