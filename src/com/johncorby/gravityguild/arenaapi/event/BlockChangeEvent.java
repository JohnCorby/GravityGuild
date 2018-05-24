package com.johncorby.gravityguild.arenaapi.event;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;

/**
 * Called when a block is changed
 * @see org.bukkit.event.block.BlockPlaceEvent
 */
public class BlockChangeEvent extends BlockEvent {
    private static final HandlerList handlers = new HandlerList();
    protected BlockState replacedBlockState;
    protected Entity entity;

    public BlockChangeEvent(final Block changedBlock, final BlockState replacedBlockState) {
        this(changedBlock, replacedBlockState, null);
    }

    public BlockChangeEvent(final Block changedBlock, final BlockState replacedBlockState, final Entity theEntity) {
        super(changedBlock);
        this.replacedBlockState = replacedBlockState;
        entity = theEntity;
    }

    /**
     * Gets the entity who placed the block involved in this event.
     * May be null
     *
     * @return The Entity who placed the block involved in this event
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Clarity method for getting the changed block. Not really needed except
     * for reasons of clarity.
     *
     * @return The Block that was changed
     */
    public Block getBlockChanged() {
        return getBlock();
    }

    /**
     * Gets the BlockState for the block which was replaced. Material type air
     * mostly.
     *
     * @return The BlockState for the block which was replaced.
     */
    public BlockState getBlockReplacedState() {
        return this.replacedBlockState;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
