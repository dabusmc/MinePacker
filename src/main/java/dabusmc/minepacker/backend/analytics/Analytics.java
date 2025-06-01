package dabusmc.minepacker.backend.analytics;

import java.util.*;

public class Analytics {

    private static HashMap<String, Calendar> s_TimeTracker;
    private static HashMap<String, AnalyticProfileCollection> s_PerformanceMap;

    public static boolean shouldSave() {
        return !s_PerformanceMap.isEmpty();
    }

    public static void init() {
        s_TimeTracker = new HashMap<>();
        s_PerformanceMap = new HashMap<>();
    }

    public static void begin(String name) {
        if(s_TimeTracker.containsKey(name))
            return;

        Calendar current = Calendar.getInstance(TimeZone.getDefault());
        s_TimeTracker.put(name, current);
    }

    public static void end(String name) {
        if(s_TimeTracker.containsKey(name)) {
            Calendar startTime = s_TimeTracker.get(name);
            Calendar endTime = Calendar.getInstance(TimeZone.getDefault());

            AnalyticProfile profile = new AnalyticProfile();
            profile.Name = String.valueOf(UUID.randomUUID());
            profile.StartDateAndTime = startTime;
            profile.EndDateAndTime = endTime;
            profile.TimeTakenMs = endTime.getTimeInMillis() - startTime.getTimeInMillis();

            if(!s_PerformanceMap.containsKey(name)) {
                AnalyticProfileCollection collection = new AnalyticProfileCollection();
                collection.Name = name;
                collection.AverageTime = profile.TimeTakenMs;
                collection.Profiles = new ArrayList<>();
                collection.Profiles.add(profile);
                s_PerformanceMap.put(name, collection);
            } else {
                s_PerformanceMap.get(name).AverageTime += profile.TimeTakenMs;
                s_PerformanceMap.get(name).Profiles.add(profile);
            }

            s_TimeTracker.remove(name);
        }
    }

    public static void endAll() {
        String[] names = s_TimeTracker.keySet().toArray(new String[0]);
        for(String name : names) {
            end(name);
        }
    }

    public static HashMap<String, AnalyticProfileCollection> getPerformanceMap() {
        return s_PerformanceMap;
    }

}
