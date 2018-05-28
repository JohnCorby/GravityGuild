package com.johncorby.gravityguild.arenaapi.command;

import com.johncorby.gravityguild.MessageHandler;
import com.johncorby.gravityguild.util.Common;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

import static com.johncorby.gravityguild.GravityGuild.gravityGuild;
import static com.johncorby.gravityguild.MessageHandler.MessageType.ERROR;
import static com.johncorby.gravityguild.MessageHandler.commandError;
import static org.apache.commons.lang.exception.ExceptionUtils.getStackTrace;

public class CommandHandler implements CommandExecutor {
    public static ArrayList<BaseCommand> commands = new ArrayList<>();

    public CommandHandler() {
        // Register base command
        gravityGuild.getCommand("gravityguild").setExecutor(this);

        // Register BaseCommands
        register(new Help());
        register(new Lobby());
        register(new Join());
        register(new Leave());

        register(new Reload());
        register(new Add());
        register(new Cancel());
        register(new Delete());
        register(new SetRegion());
        register(new Update());
        register(new ArenaStats());
        register(new SetLobby());

        TabCompleteHandler.register(getCommand("help"), 0, Common.map(commands, BaseCommand::getName).toArray(new String[0]));
    }

    private static void register(BaseCommand command) {
        commands.add(command);
    }

    public static BaseCommand getCommand(String name) {
        for (BaseCommand command : commands)
            if (command.getName().equals(name)) return command;
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        // Change args to lowercase
        args = Arrays.stream(args).map(String::toLowerCase).toArray(String[]::new);

        // If sender not player: say so
        if (!(sender instanceof Player)) return MessageHandler.commandError(sender, "Sender must be player");
        Player player = (Player) sender;

        // If no args: show help for all commands
        if (args.length == 0) {
            Help.getHelp(player, commands.toArray(new BaseCommand[0]));
            return false;
        }
        BaseCommand baseCommand = getCommand(args[0]);

        // If command not found: say so
        if (baseCommand == null)
            return MessageHandler.commandError(player, "Command " + args[0] + " not found", "Do /gravityguild help for a list of commands");

        // If no permission: say so
        if (!baseCommand.hasPermission(player)) return MessageHandler.commandError(player, "No permission");

        args = Arrays.copyOfRange(args, 1, args.length);

        // Try to execute command or show commandError if commandError
        try {
            return baseCommand.onCommand(player, args);
        } catch (Exception e) {
            commandError(player, ERROR, "Error:" + e);
            //msg(player, ERROR, "Error:", e, "Stack Trace:", getStackTrace(e));
            MessageHandler.error(getStackTrace(e));
            return false;
        }
    }
}
