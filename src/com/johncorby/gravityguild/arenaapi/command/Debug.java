package com.johncorby.gravityguild.arenaapi.command;

import com.johncorby.gravityguild.util.Class;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.entity.Player;

import static com.johncorby.gravityguild.MessageHandler.MessageType.GENERAL;
import static com.johncorby.gravityguild.MessageHandler.debug;
import static com.johncorby.gravityguild.MessageHandler.msg;

public class Debug extends BaseCommand {
    Debug() {
        super("Get debug stuff", "", "gg.admin");
    }

    @Override
    public boolean onCommand(Player sender, String[] args) {
        //debug(Thread.getAllStackTraces());
        for (Class c : Class.getClasses())
            try {
                debug((Object[]) c.getDebug().toArray());
            } catch (Exception e) {
                debug("Error getting debug for " + c + ": " + ExceptionUtils.getStackTrace(e));
            }
        msg(sender, GENERAL, "See console");
        return true;
    }
}
