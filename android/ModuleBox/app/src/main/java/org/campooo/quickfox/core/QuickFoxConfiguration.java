package org.campooo.quickfox.core;

public class QuickFoxConfiguration {

    private static final String VERSION = "1.0";

    private static int defaultReplyTimeout = 5000;
    private static int keepAliveInterval = 10000;

    public static boolean DEBUG_ENABLED = true;

    public static String getVersion() {
        return VERSION;
    }

    public static int getDefaultReplyTimeout() {
        if (defaultReplyTimeout <= 0) {
            defaultReplyTimeout = 5000;
        }
        return defaultReplyTimeout;
    }

    public static void setDefaultReplyTimeout(int timeout) {
        if (timeout <= 0) {
            return;
        }
        defaultReplyTimeout = timeout;
    }

    public static int getKeepAliveInterval() {
        return keepAliveInterval;
    }

    public static void setKeepAliveInterval(int interval) {
        keepAliveInterval = interval;
    }
}
