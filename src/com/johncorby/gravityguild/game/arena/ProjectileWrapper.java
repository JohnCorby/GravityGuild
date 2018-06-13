package com.johncorby.gravityguild.game.arena;

import com.johncorby.gravityguild.util.IdentifiableTask;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class ProjectileWrapper extends IdentifiableTask<Projectile> {
    private static final int d = 20;
    private Vector startVel;

    public ProjectileWrapper(Projectile identity) {
        super(identity);
    }

    public static ProjectileWrapper get(Projectile identity) {
        return (ProjectileWrapper) get(identity, ProjectileWrapper.class);
    }

    public static boolean contains(Projectile identity) {
        return contains(identity, ProjectileWrapper.class);
    }

    public static boolean dispose(Projectile identity) {
        return dispose(identity, ProjectileWrapper.class);
    }

    @Override
    protected boolean create(Projectile identity) {
        if (!super.create(identity)) return false;
        startVel = identity.getVelocity();
        task.runTaskTimer(d, d);
        return true;
    }

    @Override
    protected void run() {
        super.run();
        get().setVelocity(startVel);
    }

    @Override
    public List<String> getDebug() {
        List<String> r = new ArrayList<>();
        r.add(toString());
        r.add("StartVel: " + startVel);
        return r;
    }
}
