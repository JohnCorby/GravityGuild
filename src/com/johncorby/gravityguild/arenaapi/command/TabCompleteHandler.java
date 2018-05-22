package com.johncorby.gravityguild.arenaapi.command;

import com.johncorby.gravityguild.MessageHandler;

// It's broken I give up
/*
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.*;
import java.util.stream.Collectors;

import static com.johncorby.gravityguild.GravityGuild.gravityGuild;
import static com.johncorby.gravityguild.arenaapi.command.CommandHandler.commands;
import static com.johncorby.gravityguild.arenaapi.command.CommandHandler.getCommand;

public class TabCompleteHandler implements TabCompleter {
    public static final TabCompleteHandler tabComplete = new TabCompleteHandler();

    private static TabList tabList = new TabList();

    public static class TabList {
        private ArrayList<Object[]> elements = new ArrayList<>();

        void put(BaseCommand command, int argPos, String... result) {
            elements.add(new Object[]{command, argPos, result});
        }

        String[] get(BaseCommand command, int argPos) {
            for (Object[] element : elements) {
                if (element[0].equals(command) && element[1].equals(argPos)) return (String[]) element[2];
            }
            return null;
        }
    }

    private TabCompleteHandler() {
        // Register tab completer
        gravityGuild.getCommand("gravityguild").setTabCompleter(this);
    }

    public static void register(BaseCommand command, int argPos, String... result) {
        //tabList.put(command, argPos, result);
    }

    @Override
    public ArenaStats<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList result = new ArrayList();

        // If no BaseCommand supplied: return list of BaseCommands
        if (args.length == 1)
            result = Arrays.asList(commands.stream().map(BaseCommand::getName));
            for (BaseCommand r : result)
                if (!r.hasPermission(sender)) result.


        result = tabList.get(getCommand(args[0]), args.length - 2);

        // If no matching found: return null
        if (result == null) return null;

        // If this command/args matches with one in the tabList: return corresponding list
        return result;
    }
}
*/

public class TabCompleteHandler  {
    public static void register(BaseCommand command, int argPos, String... result) {
        MessageHandler.log(MessageHandler.MessageType.ERROR, "TabComplete registering won't do anything");
    }
}
