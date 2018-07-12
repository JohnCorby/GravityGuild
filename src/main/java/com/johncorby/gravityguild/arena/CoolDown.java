package com.johncorby.gravityguild.arena;

import com.johncorby.coreapi.util.MessageHandler;
import com.johncorby.coreapi.util.storedclass.IdentTask;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CoolDown extends IdentTask<Player> {
    private static final int d = 5;

    public CoolDown(Player identity) {
        super(identity);
    }

    @Nullable
    public static CoolDown get(Player identity) {
        return get(CoolDown.class, identity);
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
        MessageHandler.info(get(), "You are no longer invincible");
        dispose();
    }

    @Override
    public boolean dispose() {
        get().setInvulnerable(false);
//        get().setGlowing(false);
        return super.dispose();
    }

    @Override
    protected boolean create(@NotNull Player identity) {
        if (!super.create(identity)) return false;
        heal(identity);
        identity.setInvulnerable(true);
//        get().setGlowing(true);
        MessageHandler.info(identity, "You are invincible for " + d + " seconds");
        task.runTaskLater(20 * d);
        return true;
    }
}
