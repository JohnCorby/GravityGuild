package com.johncorby.gravityguild.arenaapi.command;

import com.johncorby.gravityguild.util.Common;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.johncorby.gravityguild.GravityGuild.gravityGuild;
import static com.johncorby.gravityguild.arenaapi.command.CommandHandler.getCommands;

// It's broken I give up
// No it's not
public class TabCompleteHandler implements TabCompleter {
    private static List<TabResult> tabResults = new ArrayList<>();


    public TabCompleteHandler() {
        // Register tab completer
        gravityGuild.getCommand("gravityguild").setTabCompleter(this);
    }

    public static void register(String command, int argPos, Supplier<List<String>> results) {
        TabResult tabResult = new TabResult(command, argPos, results);
        if (tabResults.contains(tabResult))
            throw new IllegalArgumentException("TabResult already exists");
        tabResults.add(tabResult);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> results = TabResult.getResults(args);
        // If no BaseCommand, match BaseCommands
        if (results == null) {
            return TabResult.match(args[0], Common.map(getCommands(sender), BaseCommand::getName));
        }

        // Return matching TabResult
        return TabResult.match(args[args.length - 1], results);
    }

    private static class TabResult {
        private String command;
        private int argPos;
        private Supplier<List<String>> results;
        //private List<String> results;

        private TabResult(String command, int argPos, Supplier<List<String>> results) {
            this.command = command;
            this.argPos = argPos;
            this.results = results;
        }

        // Returns null if no BaseCommand or results of TabResult that matches
        private static List<String> getResults(String[] args) {
            if (args.length < 2) return null;
            for (TabResult t : tabResults)
                if (t.command.equals(args[0]) && t.argPos == args.length - 2) return t.results.get();
            return new ArrayList<>();
        }

        // Match partial to from
        private static List<String> match(String partial, List<String> from) {
            if (from.isEmpty() || partial.isEmpty()) return from;

            List<String> matches = new ArrayList<>();
            for (String s : from)
                if (s.indexOf(partial) == 0) matches.add(s);
            return matches;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof TabResult &&
                    ((TabResult) obj).command.equals(command) &&
                    ((TabResult) obj).argPos == argPos;
        }
    }
}

/*
public class TabCompleteHandler  {
    public static void register(BaseCommand command, int argPos, String... result) {
        error("TabComplete registering won't do anything");
    }
}
*/
