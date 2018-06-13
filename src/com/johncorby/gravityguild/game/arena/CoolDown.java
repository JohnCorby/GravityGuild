package com.johncorby.gravityguild.game.arena;

import com.johncorby.gravityguild.MessageHandler;
import com.johncorby.gravityguild.util.IdentifiableTask;
import org.bukkit.entity.Player;

public class CoolDown extends IdentifiableTask<Player> {
    private static final int d = 5;

    public CoolDown(Player identity) {
        super(identity);
    }

    public static CoolDown get(Player identity) {
        return (CoolDown) get(identity, CoolDown.class);
    }

    public static boolean contains(Player identity) {
        return contains(identity, CoolDown.class);
    }

    public static boolean dispose(Player identity) {
        return dispose(identity, CoolDown.class);
    }

    @Override
    protected boolean create(Player identity) {
        if (!super.create(identity)) return false;
        identity.setInvulnerable(true);
//        get().setGlowing(true);
        identity.setHealth(identity.getMaxHealth());
        identity.setFireTicks(0);
        MessageHandler.msg(identity, MessageHandler.MessageType.GAME, "You are invincible for " + d + " seconds");
        task.runTaskLater(20 * d);
        return true;
    }

    @Override
    protected void run() {
        super.run();
        MessageHandler.msg(get(), MessageHandler.MessageType.GAME, "You are no longer invincible");
        dispose();
    }

    @Override
    public boolean dispose() {
        get().setInvulnerable(false);
//        get().setGlowing(false);
        get().setHealth(get().getMaxHealth());
        get().setFireTicks(0);
        return super.dispose();
    }
}
