package com.johncorby.gravityguild.arena;

import com.johncorby.coreapi.util.storedclass.IdentTask;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class ProjVelSet extends IdentTask<Projectile> {
    private static final int d = 20;
    private Vector startVel;

    public ProjVelSet(Projectile identity) {
        super(identity);
    }

    public static ProjVelSet get(Projectile identity) {
        return get(ProjVelSet.class, identity);
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
        get().setVelocity(startVel);
    }

    @Override
    public ArrayList<String> getDebug() {
        ArrayList<String> r = new ArrayList<>();
        r.add(toString());
        r.add("StartVel: " + startVel);
        return r;
    }
}
