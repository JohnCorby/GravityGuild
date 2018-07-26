package com.johncorby.gravityguild.arena;

import com.johncorby.coreapi.util.storedclass.IdentTask;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

import java.util.List;

public class ProjVelSet extends IdentTask<Projectile> {
    private static final int d = 20;
    private Vector startVel;

    public ProjVelSet(Projectile identity) {
        super(identity);
        create();
    }

    public static ProjVelSet get(Projectile identity) {
        return get(ProjVelSet.class, identity);
    }

    @Override
    public boolean create() {
        if (!super.create()) return false;
        startVel = identity.getVelocity();

        task.runTaskTimer(d, d);
        return true;
    }

    @Override
    protected void run() {
        get().setVelocity(startVel);
    }


    @Override
    public List<String> getDebug() {
        List<String> r = super.getDebug();
        r.add("StartVel: " + startVel);
        return r;
    }
}
