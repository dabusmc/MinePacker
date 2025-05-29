package dabusmc.minepacker.backend.analytics;

import java.util.*;

public class Analytics {

    private static HashMap<String, Calendar> s_TimeTracker;
    private static HashMap<String, Long> s_PerformanceMap;

    public static void init() {
        s_TimeTracker = new HashMap<>();
        s_PerformanceMap = new HashMap<>();
    }

    public static void begin(String name) {
        Calendar current = Calendar.getInstance(TimeZone.getDefault());
        s_TimeTracker.put(name, current);
    }

    public static void end(String name) {
        if(s_TimeTracker.containsKey(name)) {
            Calendar startTime = s_TimeTracker.get(name);
            Calendar endTime = Calendar.getInstance(TimeZone.getDefault());

            String performanceName = name + "_" + UUID.randomUUID();
            long msTaken = endTime.getTimeInMillis() - startTime.getTimeInMillis();
            s_PerformanceMap.put(performanceName, msTaken);
        }
    }

    public static void endAll() {
        for(String name : s_TimeTracker.keySet()) {
            end(name);
        }
    }

    public static HashMap<String, Long> getPerformanceMap() {
        return s_PerformanceMap;
    }

}
