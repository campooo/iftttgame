package org.campooo.quickfox.log;

/**
 * ckb on 15/11/17.
 */
public interface Logger {

    void verbose(String msg);

    void debug(String msg);

    void info(String msg);

    void wran(String msg);

    void err(String msg);

    void err(Throwable tr);

    void err(String msg, Throwable tr);
}
