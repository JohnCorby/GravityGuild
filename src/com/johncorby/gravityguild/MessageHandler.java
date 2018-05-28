package com.johncorby.gravityguild;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

import static com.johncorby.gravityguild.MessageHandler.MessageType.ERROR;
import static com.johncorby.gravityguild.MessageHandler.MessageType.GENERAL;

public class MessageHandler {
    public static final MessageHandler messageHandler = new MessageHandler();

    public static final String prefix = ChatColor.GRAY + "[" + ChatColor.GOLD + "GravityGuild" + ChatColor.GRAY + "] " + ChatColor.RESET;

    private static final boolean DEBUG = true;

    // Print message if debug
    public static void debug(Object... msgs) {
        if (DEBUG)
            for (Object m : msgs)
                log(GENERAL, ChatColor.AQUA + "[DEBUG] " + ChatColor.RESET + m);
    }

    // Print commandError is debug
    public static void error(Object... msgs) {
        if (DEBUG)
            for (Object m : msgs)
                log(ERROR, ChatColor.RED + "[ERROR] " + ChatColor.RESET + m);
    }

    public static boolean commandError(CommandSender to, Object... messages) {
        msg(to, MessageType.ERROR, messages);
        return false;
    }

    public enum MessageType {
		GENERAL(ChatColor.GRAY),
        GAME(ChatColor.YELLOW),
		ERROR(ChatColor.RED);

		private ChatColor color;

		MessageType(ChatColor color) {
			this.color = color;
		}

        public ChatColor get() {
            return color;
        }
    }


    private MessageHandler() {}

    // Message of type to player
	public static void msg(CommandSender to, MessageType type, Object... messages) {
        for (Object message : messages) to.sendMessage(prefix + type.get() + convert(message));
	}

	// Log of type to console
	public static void log(MessageType type, Object... messages) {
        for (Object message : messages) msg(Bukkit.getConsoleSender(), type, messages);
    }

    // Log of type to console and players
	public static void superLog(MessageType type, Object... messages) {
        log(type, messages);
        broadcast(type, messages);
    }

    // Message of type to players
	public static void broadcast(MessageType type, Object... messages) {
        for (Object message : messages) {
            String messageString = convert(message);
            for (Player player : Bukkit.getServer().getOnlinePlayers())
                player.sendMessage(prefix + type.get() + messageString);
        }
	}

	// Convert arrays to string form
	private static String convert(Object message) {
        if (message instanceof List) return Arrays.toString(((List) message).toArray());
        if (message instanceof Object[]) return Arrays.toString((Object[]) message);
        return message.toString();
    }
}
