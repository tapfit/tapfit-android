package co.tapfit.android.helper;

/**
 * Created by bill on 9/15/13.
 */
public class Log {

    /**
     * Level of information to log, ASSERT (7) and ERROR (6) being the highest
     * numbers and least info and VERBOSE (2) and DEBUG (3) being the lowest
     * numbers and most info. Defaults to Verbose, the most information.
     */
    public static int logLevel = android.util.Log.VERBOSE;

    /**
     * Gives ability to turn logging on or off at a global level. Defaults to be
     * off. Can be set by the application.
     */
    private static boolean isLogging = false;

    /**
     * Allows the application to set logging on or off.
     *
     * @param isLogging
     *            true if logging is on, false if logging is off
     */
    public static void setLogging(boolean isLogging) {
        Log.isLogging = isLogging;
    }

    /**
     * @return if logging is on.
     */
    public static boolean isLogging() {
        return isLogging;
    }

    /**
     * @return the level of information to log, ASSERT (7) and ERROR (6) being
     *         the highest numbers and least info and VERBOSE (2) and DEBUG (3)
     *         being the lowest numbers and most info.
     */
    public static int getLogLevel() {
        return logLevel;
    }

    /**
     * @param logLevel
     *            the level of information to log, ASSERT (7) and ERROR (6)
     *            being the highest numbers and least info and VERBOSE (2) and
     *            DEBUG (3) being the lowest numbers and most info.
     */
    public static void setLogLevel(int logLevel) {
        Log.logLevel = logLevel;
    }

    private Log() {
        // Do not allow this class to be instantiated.
    }

    // 2
    public static int v(String tag, String msg) {
        if (logLevel <= android.util.Log.VERBOSE && isLogging) {
            return android.util.Log.v(tag, msg);
        }
        return 0;
    }

    // 3
    public static int d(String tag, String msg) {
        if (logLevel <= android.util.Log.DEBUG && isLogging) {
            return android.util.Log.d(tag, msg);
        }
        return 0;
    }

    // 3
    public static int d(String tag, String msg, Throwable tr) {
        if (logLevel <= android.util.Log.DEBUG && isLogging) {
            return android.util.Log.d(tag, msg, tr);
        }
        return 0;
    }

    // 4
    public static int i(String tag, String msg) {
        if (logLevel <= android.util.Log.INFO && isLogging) {
            return android.util.Log.i(tag, msg);
        }
        return 0;
    }

    // 5
    public static int w(String tag, String msg) {
        if (logLevel <= android.util.Log.WARN && isLogging) {
            return android.util.Log.w(tag, msg);
        }
        return 0;
    }

    // 6
    public static int e(String tag, String msg) {
        if (logLevel <= android.util.Log.ERROR && isLogging) {
            return android.util.Log.e(tag, msg);
        }
        return 0;
    }

    // 6
    public static int e(String tag, String msg, Throwable tr) {
        if (logLevel <= android.util.Log.ERROR && isLogging) {
            return android.util.Log.e(tag, msg, tr);
        }
        return 0;
    }

}