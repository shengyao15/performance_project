package com.hp.it.perf.ac.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

public class TimeWatch {

    public final static TimeWatch Gobal = new TimeWatch(true);
    
    static enum Status {
        RUN, PAUSED;
    }

    static class TimeEntry {

        private long duration;

        private long resumeTime;

        String name;

        public void reset() {
            duration = 0L;
            resumeTime = 0L;
        }

        public long total(long now) {
            if (resumeTime == 0L) {
                // is paused
                return duration;
            } else {
                return now - resumeTime + duration;
            }
        }

        public void pause(long now) {
            duration = now - resumeTime;
            resumeTime = 0L;
        }

        public void resume(long now) {
            resumeTime = now;
        }
    }

    private TimeEntry global = new TimeEntry();

    private static final TimeUnit UNIT = TimeUnit.NANOSECONDS;

    private Status status = Status.PAUSED;

    private Map<String, List<Long>> times = new HashMap<String, List<Long>>();

    private Stack<TimeEntry> splitStack = new Stack<TimeEntry>();

    private Set<String> splitNameCache = new LinkedHashSet<String>();

    private final long now() {
        return System.nanoTime();
    }

    public TimeWatch() {
        this(true);
    }

    public TimeWatch(boolean start) {
        reset();
        if (start) {
            resume();
        }
    }

    public void resume() {
        switch (status) {
        case PAUSED:
            long now = now();
            global.resume(now);
            for (TimeEntry entry : splitStack) {
                entry.resume(now);
            }
            status = Status.RUN;
            break;
        case RUN:
            // no-op;
            break;
        default:
            throw new IllegalArgumentException("unknow status: " + status);
        }
    }

    public void split(String name) {
        TimeEntry entry = new TimeEntry();
        entry.name = name;
        if (!splitNameCache.contains(name)) {
            splitNameCache.add(name);
        }
        switch (status) {
        case PAUSED:
            entry.reset();
            splitStack.add(entry);
            break;
        case RUN:
            splitStack.add(entry);
            // set time later
            entry.resume(now());
            break;
        default:
            throw new IllegalArgumentException("unknow status: " + status);
        }
    }

    public void reset() {
        status = Status.PAUSED;
        global.reset();
        times.clear();
        splitStack.clear();
    }

    public void pause() {
        long now = now();
        switch (status) {
        case PAUSED:
            // no-op;
            break;
        case RUN:
            global.pause(now);
            for (TimeEntry entry : splitStack) {
                entry.pause(now);
            }
            status = Status.PAUSED;
            break;
        default:
            throw new IllegalArgumentException("unknow status: " + status);
        }
    }

    public void unsplit() {
        long now = now();
        TimeEntry timeEntry = splitStack.peek();
        if (timeEntry != null) {
            splitStack.pop();
        } else {
            return;
        }
        switch (status) {
        case RUN:
            timeEntry.pause(now);
        case PAUSED:
            List<Long> list = times.get(timeEntry.name);
            if (list == null) {
                list = new ArrayList<Long>();
                times.put(timeEntry.name, list);
            }
            list.add(timeEntry.total(now));
            break;
        default:
            throw new IllegalArgumentException("unknow status: " + status);
        }
    }

    public long total(TimeUnit unit) {
        return total(null, now(), unit);
    }

    private long total(String name, long now, TimeUnit unit) {
        switch (status) {
        case PAUSED:
        case RUN:
            long d = 0L;
            if (name == null) {
                d = global.total(now);
            } else {
                long[] entries = getSplitsByName(name, now);
                for (long entry : entries) {
                    d += entry;
                }
            }
            return unit.convert(d, UNIT);
        default:
            throw new IllegalArgumentException("unknow status: " + status);
        }
    }

    public long total(String name, TimeUnit unit) {
        return total(name, now(), unit);
    }

    public long last(String name, TimeUnit unit) {
        long[] data = all(unit).get(name);
        if (data.length != 0) {
            return data[data.length - 1];
        } else {
            return 0L;
        }
    }

    public long[] all(String name, TimeUnit unit) {
        return all(unit).get(name);
    }

    public Map<String, long[]> all(TimeUnit unit) {
        return all(now(), unit);
    }

    private Map<String, long[]> all(long now, TimeUnit unit) {
        switch (status) {
        case PAUSED:
        case RUN:
            Map<String, long[]> map = new LinkedHashMap<String, long[]>();
            for (String name : splitNameCache) {
                long[] splits = getSplitsByName(name, now);
                long[] ret = new long[splits.length];
                for (int i = 0; i < splits.length; i++) {
                    ret[i] = unit.convert(splits[i], UNIT);
                }
                map.put(name, ret);
            }
            map.put(null, new long[] { total(unit) });
            return map;
        default:
            throw new IllegalArgumentException("unknow status: " + status);
        }
    }

    private long[] getSplitsByName(String name, long now) {
        List<Long> list = times.get(name);
        TimeEntry inSplitting = null;
        for (TimeEntry entry : splitStack) {
            if (entry.name.equals(name)) {
                inSplitting = entry;
            }
        }
        long[] ret = new long[(list == null ? 0 : list.size()) + (inSplitting == null ? 0 : 1)];
        if (list != null) {
            int i = 0;
            for (long l : list) {
                ret[i] = l;
                i++;
            }
        }
        if (inSplitting != null) {
            ret[ret.length - 1] = inSplitting.total(now);
        }
        return ret;
    }
    
    public String toString() {
        return toString(UNIT);        
    }

    public String toString(TimeUnit unit) {
        StringBuilder buffer = new StringBuilder();
        Map<String, long[]> allMap = all(now(), unit);
        String unitString = unit.toString().toLowerCase();
        buffer.append("[Global Watch] ").append("Status: ").append(status).append(", Duration: ")
                .append(allMap.remove(null)[0]).append(" ").append(unitString);
        for (String name : allMap.keySet()) {
            buffer.append("\n\t").append("[").append(name).append("]");
            long[] data = allMap.get(name);
            Calculator cal = Calculator.build(data);
            buffer.append(" ").append(cal);
            for (int i = 0; i < Math.min(data.length, 4); i++) {
                buffer.append("\n\t\t").append("(").append(i + 1).append(") ").append(data[i]).append(" ")
                        .append(unitString);
            }
            if (data.length >= 5) {
                buffer.append("\n\t\t\t").append(data.length - 4).append(" more...");
            }
        }
        return buffer.toString();
    }

}
