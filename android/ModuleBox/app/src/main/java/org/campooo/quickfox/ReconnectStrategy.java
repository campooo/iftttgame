package org.campooo.quickfox;

import org.campooo.quickfox.log.Logger;
import org.campooo.quickfox.log.QLog;

public class ReconnectStrategy implements ConnectionListener {

    private static final Logger Log = QLog.getLogger(ReconnectStrategy.class);

    private Connection conn;
    private Thread reconnectionThread;

    private int baseInterval = 10;

    boolean done = false;

    ReconnectStrategy(Connection conn) {
        this.conn = conn;
    }

    private boolean isReconnectionAllowed() {
        return !done && !conn.isConnected() && conn.getConfiguration().isReconnectionAllowed();
    }

    private synchronized void reconnect() {
        if (isReconnectionAllowed()) {
            if (reconnectionThread != null && reconnectionThread.isAlive()) {
                return;
            }
            reconnectionThread = new Thread() {

                private int attempts = 0;

                private int timeDelay() {
                    attempts++;
                    if (attempts > 13) {
                        return baseInterval * 6 * 5; // 5分钟重试一次
                    }
                    if (attempts > 7) {
                        return baseInterval * 6; // 1分钟重试一次
                    }
                    return baseInterval; // 10 秒
                }

                public void run() {
                    while (isReconnectionAllowed()) {
                        int remainingSeconds = timeDelay();
                        while (isReconnectionAllowed() && remainingSeconds > 0) {
                            try {
                                Thread.sleep(1000);
                                remainingSeconds--;
                                notifyAttemptToReconnectIn(remainingSeconds);
                            } catch (InterruptedException e1) {
                                notifyReconnectionFailed(e1);
                            }
                        }

                        try {
                            if (isReconnectionAllowed()) {
                                conn.connect();
                                if (conn.isConnected()) {
                                    notifyReconnectSuccessful();
                                }
                            }
                        } catch (Exception e) {
                            notifyReconnectionFailed(e);
                        }
                    }
                }
            };
            reconnectionThread.setName("Reconnect Thread ( " + "quickfox" + " )");
            reconnectionThread.setDaemon(true);
            reconnectionThread.start();
        }
    }

    @Override
    public void connectionCreated(Connection conn) {

    }

    @Override
    public void connected(Connection conn) {

    }

    @Override
    public void authenticated(Connection conn) {

    }

    @Override
    public void connectionClosed() {
        done = true;
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        done = false;
        if (isReconnectionAllowed()) {
            reconnect();
        }
    }

    @Override
    public void reconnectingIn(int timeOut) {
        // ignore
    }

    @Override
    public void reconnectSuccessful() {
        // ignore
    }

    @Override
    public void reconnectionFailed(Exception e) {
        // ignore
    }

    protected void notifyReconnectionFailed(Exception exception) {
        if (isReconnectionAllowed()) {
            for (ConnectionListener listener : conn.getConnectionListeners()) {
                listener.reconnectionFailed(exception);
            }
        }
    }

    protected void notifyAttemptToReconnectIn(int seconds) {
        if (isReconnectionAllowed()) {
            for (ConnectionListener listener : conn.getConnectionListeners()) {
                listener.reconnectingIn(seconds);
            }
        }
    }

    protected void notifyReconnectSuccessful() {
        if (isReconnectionAllowed()) {
            for (ConnectionListener listener : conn.getConnectionListeners()) {
                try {
                    listener.reconnectSuccessful();
                } catch (Exception e) {
                    Log.err(e);
                }
            }
        }
    }
}
