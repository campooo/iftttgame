package org.campooo.quickfox;

import org.campooo.quickfox.log.Logger;
import org.campooo.quickfox.log.QLog;
import org.campooo.quickfox.stanza.Stanza;

import java.net.Socket;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author campo
 */
public abstract class Connection {

    private static final Logger Log = QLog.getLogger(Connection.class);

    protected final Collection<ConnectionListener> connectionListeners = new CopyOnWriteArrayList<ConnectionListener>();

    protected final Map<StanzaListener, ListenerWrapper> recvListeners = new ConcurrentHashMap<StanzaListener, ListenerWrapper>();

    protected final Map<StanzaListener, ListenerWrapper> sendListeners = new ConcurrentHashMap<StanzaListener, ListenerWrapper>();

    protected final ConnectionConfiguration config;

    protected Socket socket;

    protected Connection(ConnectionConfiguration configuration) {
        config = configuration;
    }

    protected ConnectionConfiguration getConfiguration() {
        return config;
    }

    public String getHost() {
        return config.getHost();
    }

    public int getPort() {
        return config.getPort();
    }

    protected boolean isReconnectionAllowed() {
        return config.isReconnectionAllowed();
    }

    public abstract String getConnectionID();

    public abstract boolean isConnected();

    public abstract void connect() throws Exception;

    public abstract void send(String rawText);

    public abstract void disconnect();

    public abstract void closeOnError(Exception e);

    // 链接事件接口

    public void addConnectionListener(ConnectionListener connectionListener) {
        if (!isConnected()) {
            throw new IllegalStateException("Not connected to server.");
        }
        if (connectionListener == null) {
            return;
        }
        if (!connectionListeners.contains(connectionListener)) {
            connectionListeners.add(connectionListener);
        }
    }

    public void removeConnectionListener(ConnectionListener connectionListener) {
        connectionListeners.remove(connectionListener);
    }

    Collection<ConnectionListener> getConnectionListeners() {
        return Collections.unmodifiableCollection(connectionListeners);
    }


    protected void callConnectionCreatedListener() {
        for (ConnectionListener aListener : connectionListeners) {
            aListener.connectionCreated(this);
        }
    }

    protected void callConnectionClosedOnErrorListener(Exception e) {
        for (ConnectionListener aListener : connectionListeners) {
            try {
                aListener.connectionClosedOnError(e);
            } catch (Exception e2) {
                Log.err(e2);
            }
        }
    }


    protected void callConnectionClosedListener() {
        for (ConnectionListener aListener : connectionListeners) {
            try {
                aListener.connectionClosed();
            } catch (Exception e) {
                Log.err(e);
            }
        }
    }

    protected void callConnectionConnectedListener() {
        for (ConnectionListener aListener : connectionListeners) {
            aListener.connected(this);
        }
    }

    // 包接收器接口

    public void addStanzaListener(StanzaListener listener, StanzaFilter filter) {
        if (listener == null) {
            return;
        }
        ListenerWrapper wrapper = new ListenerWrapper(listener, filter);
        recvListeners.put(listener, wrapper);
    }

    public void removeStanzaListener(StanzaListener listener) {
        recvListeners.remove(listener);
    }

    Map<StanzaListener, ListenerWrapper> getStanzaListeners() {
        return Collections.unmodifiableMap(recvListeners);
    }


    // 包发送接口

    public void addStanzaSendingListener(StanzaListener listener, StanzaFilter filter) {
        if (listener == null) {
            return;
        }
        ListenerWrapper wrapper = new ListenerWrapper(listener, filter);
        sendListeners.put(listener, wrapper);
    }

    public void removeStanzaSendingListener(StanzaListener listener) {
        sendListeners.remove(listener);
    }

    Map<StanzaListener, ListenerWrapper> getStanzaSendingListeners() {
        return Collections.unmodifiableMap(sendListeners);
    }

    protected static class ListenerWrapper {

        private StanzaListener listener;
        private StanzaFilter filter;

        public ListenerWrapper(StanzaListener listener, StanzaFilter filter) {
            this.listener = listener;
            this.filter = filter;
        }

        public void notifyListener(Stanza stanza) {
            if (filter == null || filter.accept(stanza)) {
                listener.processStanza(stanza);
            }
        }
    }

    Socket getSocket() {
        return socket;
    }

}
