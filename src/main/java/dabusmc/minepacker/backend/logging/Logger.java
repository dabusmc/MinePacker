package dabusmc.minepacker.backend.logging;

import dabusmc.minepacker.backend.MinePackerRuntime;

public class Logger {

    private static void log(String owner, Object message, LogLevel level, TextColor color) {
        if (logLevelGreaterThan(level, MinePackerRuntime.Instance.getLogLevel())) {
            String finalMessage = color.toString();
            finalMessage += "[";
            finalMessage += owner;
            finalMessage += "] ";
            finalMessage += message.toString();
            finalMessage += TextColor.RESET.toString();

            System.out.println(finalMessage);
        }
    }

    public static void message(String owner, Object message) {
        log(owner, message, LogLevel.MESSAGE, TextColor.RESET);
    }

    public static void info(String owner, Object message) {
        log(owner, message, LogLevel.INFO, TextColor.GREEN);
    }

    public static void error(String owner, Object message) {
        log(owner, message, LogLevel.ERROR, TextColor.RED);
    }

    public static void fatal(String owner, Object message) {
        log(owner, message, LogLevel.FATAL, TextColor.RED_UNDERLINED);
        System.exit(-1);
    }


    /**
     * Checks if one LogLevel is more severe than another, making use of {@link #logLevelToInt(LogLevel)}
     * @param test The LogLevel to test
     * @param control The LogLevel to test against
     * @return True if {@code test} is more severe than {@code control}, False if not
     */
    private static boolean logLevelGreaterThan(LogLevel test, LogLevel control) {
        int testValue = logLevelToInt(test);
        if (testValue == 4) return false;
        int controlValue = logLevelToInt(control);
        return testValue >= controlValue;
    }

    /**
     * Calculates an integer representation of a LogLevel. The higher the severity of a LogLevel message, the higher the integer.
     * @param level The LogLevel to calculate the representation of
     * @return The calculated representation
     */
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
