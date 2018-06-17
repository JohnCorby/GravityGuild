package com.johncorby.gravityguild.arenaapi.command;

import com.johncorby.gravityguild.util.Class;
import com.johncorby.gravityguild.util.MessageHandler;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.entity.Player;

public class Debug extends BaseCommand {
    Debug() {
        super("Get debug stuff", "", "gg.admin");
    }

    @Override
    public boolean onCommand(Player sender, String[] args) {
        //debug(Thread.getAllStackTraces());
        for (Class c : Class.getClasses())
            try {
                MessageHandler.debug((Object[]) c.getDebug().toArray());
            } catch (Exception e) {
                MessageHandler.debug("Error getting debug for " + c + ": " + ExceptionUtils.getStackTrace(e));
            }
        MessageHandler.msg(sender, MessageHandler.MessageType.GENERAL, "See console");
        return true;
    }
}
