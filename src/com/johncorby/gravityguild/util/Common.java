package com.johncorby.gravityguild.util;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Common {
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
        return Arrays.stream(oa).map(Common::toStr).toArray(String[]::new);
    }
    // Convert string to int
    public static <T> List<String> toStr(List<T> ol) {
        return map(ol, Common::toStr);
    }
    public static <T> Integer toInt(T o) {
        try {
            return Integer.parseInt(toStr(o));
        } catch (NumberFormatException e) {
            return null;
        }
    }
    public static <T> Integer[] toInt(T[] oa) {
        return Arrays.stream(oa).map(Common::toInt).toArray(Integer[]::new);
    }
    public static <T> List<Integer> toInt(List<T> ol) {
        return map(ol, Common::toInt);
    }

    // Map list/array
    public static <T, R> R[] map(T[] array, Function<? super T, ? extends R> function, R[] instance) {
        return toArray(Arrays.stream(array).map(function).collect(Collectors.toList()), instance);
    }
    public static <T, R> List<R> map(List<T> list, Function<? super T, ? extends R> function) {
        return list.stream().map(function).collect(Collectors.toList());
    }

    // Basically try/catch
    public static void doTry(java.lang.Runnable action, java.lang.Runnable errorAction, boolean printStackTrace) {
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

    public static class MultiForLoop extends Runnable {
        private int delay, period, perTick;
        private List<Container> containers;

        public MultiForLoop(int delay, int period, int perTick, Container... containers) {
            this.delay = delay;
            this.period = period;
            this.perTick = perTick;

            // first is outermost scope and last is innermost scope
            this.containers = Lists.reverse(Arrays.asList(containers));
            start();
        }

        private boolean start() {
            if (!isDone()) return false;
            for (Container c : containers)
                c.i = c.from;
            runTaskTimer(delay, period);
            return true;
        }

        private boolean stop() {
            if (isDone()) return false;
            for (Container c : containers)
                c.i = c.from;
            cancel();
            return true;
        }

        private boolean isDone() {
            boolean cancelled;
            try {
                cancelled = isCancelled();
            } catch (IllegalStateException e) {
                cancelled = true;
            }

            // Check outermost container
            Container c = containers.get(containers.size() - 1);
            return c.i >= c.to || cancelled;
        }

        @Override
        public void run() {
            for (int j = 0; j < perTick; j++) {
                if (isDone()) stop();
                else {
                    // Reverse (from innermost to outermost)
                    List<Container> rc = Lists.reverse(containers);
                    for (int i = 0; i < rc.size(); i++) {
                        // If this container not outermost and reached to
                        Container c = rc.get(i);
                        if (i != rc.size() - 1 && c.next()) {
                            // Reset and increment next outer container
                            c.i = c.from;
                            rc.get(i + 1).next();
                        }
                    }
                }
            }
        }

        public static class Container {
            private int i, from, to;
            private Consumer<Integer[]> action;
            private MultiForLoop parent;

            public Container(int from, int to) {
                this(from, to, null);
            }

            public Container(int from, int to, Consumer<Integer[]> action) {
                this.from = from;
                this.to = to;
                this.action = action;
            }

            // Returns true if reached to
            private boolean next() {
                //if (action != null) action.accept();
                i++;
                return i >= to;
            }
        }
    }
}
