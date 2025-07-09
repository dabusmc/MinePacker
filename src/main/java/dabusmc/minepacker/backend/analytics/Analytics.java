package dabusmc.minepacker.backend.analytics;

import dabusmc.minepacker.backend.logging.LogLevel;

import java.util.*;

public class Analytics {

    private static HashMap<String, Calendar> s_TimeTracker;
    private static HashMap<String, AnalyticProfileCollection> s_PerformanceMap;

    /**
     * Determines if the Analytics should be saved
     * @return True if the Analytics should be saved, otherwise false
     */
    public static boolean shouldSave() {
        return !s_PerformanceMap.isEmpty();
    }

    /**
     * Initialises the Analytics
     */
    public static void init() {
        s_TimeTracker = new HashMap<>();
        s_PerformanceMap = new HashMap<>();
    }

    /**
     * Start tracking the performance of a function with a specified name. Must have a call to {@link #end(String)} within the same scope.
     * @param name The name of the function to track
     */
    public static void begin(String name) {
        if(s_TimeTracker.containsKey(name))
            return;

        Calendar current = Calendar.getInstance(TimeZone.getDefault());
        s_TimeTracker.put(name, current);
    }

    /**
     * End tracking the performance of a function. Must have called {@link #begin(String)} before in the same scope and the {@code name}s must match
     * @param name The name of the function to stop tracking
     */
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

    /**
     * Ends all currently active performance trackers
     */
    public static void endAll() {
        String[] names = s_TimeTracker.keySet().toArray(new String[0]);
        for(String name : names) {
            end(name);
        }
    }

    /**
     * Returns a map of all the tracked Performance data
     * @return A HashMap linking the names (specified in {@link #begin(String)}) and the {@link AnalyticProfileCollection}s
     */
    public static HashMap<String, AnalyticProfileCollection> getPerformanceMap() {
        return s_PerformanceMap;
    }

}
