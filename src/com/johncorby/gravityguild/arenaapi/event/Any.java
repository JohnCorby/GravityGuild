package com.johncorby.gravityguild.arenaapi.event;

import com.johncorby.gravityguild.Utils;
import com.johncorby.gravityguild.arenaapi.arena.ArenaHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.plugin.RegisteredListener;

import static com.johncorby.gravityguild.GravityGuild.gravityGuild;

public class Any implements Listener {
    Any() {
        RegisteredListener registeredListener = new RegisteredListener(this,
                (listener, event) ->
                {
                    if (event instanceof BlockEvent) {
                        BlockEvent blockEvent = (BlockEvent) event;
                        //Utils.debug("Block Event: " + blockEvent.getEventName());

                        // Ignore if physics (aka block update) event
                        //if (blockEvent.getEventName().equalsIgnoreCase("BlockPhysicsEvent")) return;

                        // Ignore if not in arena
                        ArenaHandler.Arena aI = ArenaHandler.arenaInC(blockEvent.getBlock().getLocation());
                        if (aI == null) return;

                        // Add block to arena's changed blocks list
                        Utils.debug("Block Event in arena: " + blockEvent.getEventName());
                        aI.addChangedBlock(blockEvent.getBlock().getState());
                    } else {
                        //Utils.debug("Event: " + event.getEventName());
                    }

                },
                EventPriority.NORMAL, gravityGuild, false);
        for (HandlerList handler : HandlerList.getHandlerLists())
            handler.register(registeredListener);
    }
}