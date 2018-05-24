package com.johncorby.gravityguild.game.event;

import com.johncorby.gravityguild.arenaapi.arena.ArenaHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class Block implements Listener {
    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        // Ignore if not in arena
        ArenaHandler.Arena aI = ArenaHandler.arenaIn(event.getPlayer());
        if (aI == null) return;

        // Add block to arena's changed blocks list
        //aI.addChangedBlock(event.getBlockReplacedState());
    }
}
