package com.johncorby.gravityguild.arenaapi.command;

import com.johncorby.gravityguild.MessageHandler;
import org.bukkit.entity.Player;

import static com.johncorby.gravityguild.MessageHandler.MessageType;
import static com.johncorby.gravityguild.MessageHandler.msg;
import static com.johncorby.gravityguild.arenaapi.command.CommandHandler.getCommand;
import static com.johncorby.gravityguild.arenaapi.command.CommandHandler.getCommands;

public class Help extends BaseCommand {
    Help() {
        super("Display help", "[command]", "");
    }

    static void getHelp(Player sender, BaseCommand... commands) {
        // Header
        if (commands.length == 1)
            msg(sender, MessageType.GENERAL, "----- Help for command " + commands[0].getName() + " -----");
        else
            msg(sender, MessageType.GENERAL, "----- Help for commands -----");

        // Get help for commands
        for (BaseCommand c : commands) {
            msg(sender, MessageType.GENERAL, "/gravityguild " + c.getName() + " " + c.getUsage() + " - " + c.getDescription());
        }
    }

    @Override
    public boolean onCommand(Player sender, String[] args) {
        // If no args: show all help
        if (args.length == 0) {
            Help.getHelp(sender, getCommands(sender).toArray(new BaseCommand[0]));
            return false;
        }

        // Try to get command
        BaseCommand command = getCommand(args[0]);
        if (command == null)
            return MessageHandler.commandError(sender, "Command " + args[0] + " not found", "Do /gravityguild help for a list of commands");

        // Get help for command
        getHelp(sender, command);
        return true;
    }
}
