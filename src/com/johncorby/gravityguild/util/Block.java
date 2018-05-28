package com.johncorby.gravityguild.util;

import com.johncorby.gravityguild.MessageHandler;
import org.bukkit.Chunk;
import org.bukkit.block.BlockState;

import java.util.ArrayList;
import java.util.List;

import static com.johncorby.gravityguild.GravityGuild.WORLD;

public class Block {
    // Get blocks in chunk
    public static List<BlockState> getChunk(int x, int z) {
        MessageHandler.debug(String.format("Getting chunk (%s, %s)", x, z));
        List<BlockState> b = new ArrayList<>();
        Chunk c = WORLD.getChunkAt(x, z);


        new Runnable() {
            @Override
            public void run() {
                for (int cz = 0; cz < 16; cz++)
                    for (int cx = 0; cx < 16; cx++)
                        for (int cy = 0; cy < 256; cy++)
                            b.add(c.getBlock(cx, cy, cz).getState());
            }
        }.runTask();

        /*
        for (int cz = 0; cz < 16; cz++)
            for (int cx = 0; cx < 16; cx++)
                for (int cy = 0; cy < 256; cy++)
                    b.add(c.getBlock(cx, cy, cz).getState());
        */

        MessageHandler.debug(String.format("Done getting chunk (%s, %s)", x, z));
        return b;
    }

    // Set blocks in chunk (will only get first 2**16 blocks in list
    public static List<BlockState> setChunk(int x, int z, List<BlockState> blocks) {
        MessageHandler.debug(String.format("Setting chunk (%s, %s)", x, z));
        if (blocks.size() < 65536)
            blocks = getChunk(x, z);
        else blocks = blocks.subList(0, 65536);

        WORLD.getChunkAt(blocks.get(0).getLocation()).getChunkSnapshot();
        List<BlockState> finalBlocks = blocks;

        new Runnable() {
            @Override
            public void run() {
                for (BlockState b : finalBlocks.subList(0, 65536))
                    b.update();
            }
        }.runTask();

        /*
        for (BlockState b : finalBlocks.subList(0, 65536))
            b.update();
        */

        MessageHandler.debug(String.format("Done setting chunk (%s, %s)", x, z));
        return blocks.subList(65536, blocks.size());
    }
}
