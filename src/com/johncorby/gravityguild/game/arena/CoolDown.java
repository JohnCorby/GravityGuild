package com.johncorby.gravityguild.game.arena;

import com.johncorby.gravityguild.util.IdentifiableTask;
import com.johncorby.gravityguild.util.MessageHandler;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class CoolDown extends IdentifiableTask<Player> {
    private static final int d = 5;

    public CoolDown(Player identity) {
        super(identity);
    }

    public static CoolDown get(Player identity) {
        return (CoolDown) get(identity, CoolDown.class);
    }

    public static boolean dispose(Player identity) {
        return dispose(identity, CoolDown.class);
    }

    public static void heal(Player p) {
        p.setHealth(p.getMaxHealth());
        p.setFoodLevel(20);
        p.setFireTicks(0);
        for (PotionEffect e : p.getActivePotionEffects())
            p.removePotionEffect(e.getType());
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
        return super.dispose();
    }

    @Override
    protected boolean create(Player identity) {
        if (!super.create(identity)) return false;
        heal(identity);
        identity.setInvulnerable(true);
//        get().setGlowing(true);
        MessageHandler.msg(identity, MessageHandler.MessageType.GAME, "You are invincible for " + d + " seconds");
        task.runTaskLater(20 * d);
        return true;
    }
}
