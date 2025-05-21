package dabusmc.minepacker.backend.logging;

import dabusmc.minepacker.backend.MinePackerRuntime;

public class Logger {

    private static void log(String owner, Object message, LogLevel level, TextColor color) {
        if (logLevelGreaterThan(level, MinePackerRuntime.s_Instance.getLogLevel())) {
            String finalMessage = color.toString();
            finalMessage += "[";
            finalMessage += owner;
            finalMessage += "] ";
            finalMessage += message.toString();
            finalMessage += TextColor.RESET.toString();

            System.out.println(finalMessage);
        }
    }

    public static void message(String owner, String message) {
        log(owner, message, LogLevel.MESSAGE, TextColor.RESET);
    }

    public static void info(String owner, String message) {
        log(owner, message, LogLevel.INFO, TextColor.GREEN);
    }

    public static void error(String owner, String message) {
        log(owner, message, LogLevel.ERROR, TextColor.RED);
    }

    public static void fatal(String owner, String message) {
        log(owner, message, LogLevel.FATAL, TextColor.RED_UNDERLINED);
    }


    private static boolean logLevelGreaterThan(LogLevel test, LogLevel control) {
        int testValue = logLevelToInt(test);
        if (testValue == 4) return false;
        int controlValue = logLevelToInt(control);
        return testValue >= controlValue;
    }

    private static int logLevelToInt(LogLevel level) {
        switch (level)
        {
            case MESSAGE -> {
                return 0;
            }
            case INFO -> {
                return 1;
            }
            case ERROR -> {
                return 2;
            }
            case FATAL -> {
                return 3;
            }
            case NONE -> {
                return 4;
            }
        }

        return -1;
    }

}
