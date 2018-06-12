package com.johncorby.gravityguild.arenaapi.command;

import com.johncorby.gravityguild.MessageHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.johncorby.gravityguild.GravityGuild.gravityGuild;
import static com.johncorby.gravityguild.MessageHandler.commandError;
import static org.apache.commons.lang.exception.ExceptionUtils.getStackTrace;

public class CommandHandler implements CommandExecutor {
    public static List<BaseCommand> commands = new ArrayList<>();

    public CommandHandler() {
        // Register base command
        gravityGuild.getCommand("gravityguild").setExecutor(this);

        // Register BaseCommands
        //register(new Help());
        register(new Lobby());
        register(new Join());
        register(new Leave());

        register(new Reload());
        register(new Add());
        register(new Cancel());
        register(new Delete());
        register(new SetRegion());
        register(new Update());
        register(new Debug());
        register(new SetLobby());

        //TabCompleteHandler.register("help", 0, () -> Common.map(commands, BaseCommand::getName));
    }

    private static void register(BaseCommand command) {
        commands.add(command);
    }

    // Get commands
    public static List<BaseCommand> getCommands(CommandSender who) {
        return commands.stream().filter(c -> c.hasPermission(who)).collect(Collectors.toList());
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
        if (!(sender instanceof Player)) return commandError(sender, "Sender must be player");
        Player player = (Player) sender;

        // If no args: show help for all commands
        if (args.length == 0) {
            Help.getHelp(player, commands.toArray(new BaseCommand[0]));
            return false;
        }
        BaseCommand baseCommand = getCommand(args[0]);

        // If command not found or no permission: say so
        if (baseCommand == null || !baseCommand.hasPermission(player))
            return commandError(player, "Command " + args[0] + " not found", "Do /gravityguild for a list of commands");

        args = Arrays.copyOfRange(args, 1, args.length);

        // Try to execute command or show commandError if commandError
        try {
            return baseCommand.onCommand(player, args);
        } catch (Exception e) {
            commandError(player, e);
            MessageHandler.error((Object) getStackTrace(e));
            return false;
        }
    }
}
