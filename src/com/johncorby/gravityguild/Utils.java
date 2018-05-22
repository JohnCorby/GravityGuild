package com.johncorby.gravityguild;

import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.johncorby.gravityguild.MessageHandler.MessageType.GENERAL;
import static com.johncorby.gravityguild.MessageHandler.log;

public class Utils {
    // Print message if debug
    public static void debug(Object... msgs) {
        boolean DEBUG = true;

        if (DEBUG)
            for (Object m : msgs)
                log(GENERAL, ChatColor.AQUA + "[DEBUG] " + ChatColor.RESET + m);
    }

    // Convert array to list and vice versa
    public static <T> List<T> toList(T[] o) {
        return Arrays.asList(o);
    }
    public static <T> T[] toArray(List<T> l, T[] i) {
        return l.toArray(i);
    }

    // Convert object to string
    public static <T> String toStr(T o) {
        return String.valueOf(o);
    }
    public static <T> String[] toStr(T[] oa) {
        return Arrays.stream(oa).map(Utils::toStr).toArray(String[]::new);
    }
    // Convert string to int
    public static <T> List<String> toStr(List<T> ol) {
        return map(ol, Utils::toStr);
    }
    public static <T> Integer toInt(T o) {
        try {
            return Integer.parseInt(toStr(o));
        } catch (NumberFormatException e) {
            return null;
        }
    }
    public static <T> Integer[] toInt(T[] oa) {
        return Arrays.stream(oa).map(Utils::toInt).toArray(Integer[]::new);
    }
    public static <T> List<Integer> toInt(List<T> ol) {
        return map(ol, Utils::toInt);
    }

    // Map list/array
    public static <T, R> R[] map(T[] array, Function<? super T, ? extends R> function, R[] instance) {
        return toArray(Arrays.stream(array).map(function).collect(Collectors.toList()), instance);
    }
    public static <T, R> List<R> map(List<T> list, Function<? super T, ? extends R> function) {
        return list.stream().map(function).collect(Collectors.toList());
    }

    // Basically try/catch
    public static void doTry(Runnable action, Runnable errorAction, boolean printStackTrace) {
        try {
            action.run();
        } catch (Exception e) {
            if (printStackTrace) e.printStackTrace();
            errorAction.run();
        }
    }
    public static <T> T doTry(Callable<T> action, Callable<T> errorAction, boolean printStackTrace) throws Exception {
        try {
            return action.call();
        } catch (Exception e) {
            if (printStackTrace) e.printStackTrace();
            return errorAction.call();
        }
    }

    // Get a random int between min and max
    public static int randInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max+1);
    }

    // Check if string is int
    public static boolean isInt(String s) {
        return toInt(s) != null;
    }

    // String version of array with formatting
    public static <T> String toStr(T[] array, String format) {
        assert array.getClass().isArray();
        return String.format(format, (Object[]) array);
    }

    // Find property R of list T and return T that has property R
    public static <T, R> T find(T[] array, T[] instance, Function<? super T, ? extends R> function, R element) {
        return find(Arrays.asList(array), function, element);
    }

    public static <T, R> T find(List<T> list, Function<? super T, ? extends R> function, R element) {
        int i = map(list, function).indexOf(element);
        return i < 0 ? null : list.get(i);
    }


    // See if one list has an item from another
    public static <T> boolean containsAny(List<T> list1, List<T> list2) {
        for (T e : list2)
            if (list1.contains(e)) return true;
        return false;
    }
}
