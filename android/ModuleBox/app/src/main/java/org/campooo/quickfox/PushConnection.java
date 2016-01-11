package org.campooo.quickfox;

import android.os.SystemClock;

import org.campooo.quickfox.log.Logger;
import org.campooo.quickfox.log.QLog;
import org.campooo.quickfox.stanza.Stanza;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.net.SocketFactory;

public class PushConnection extends Connection {

    private static final Logger Log = QLog.getLogger(PushConnection.class);

    String connectionID = null;
    private boolean connected = false;


    private SocketFactory socketFactory;


    // 发送器和接收器
    StanzaWriter stanzaWriter;
    StanzaReader stanzaReader;

    public PushConnection(ConnectionConfiguration config) {
        super(config);
    }

    public String getConnectionID() {
        if (!isConnected()) {
            return null;
        }
        return connectionID;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public void connect() throws Exception {
        connectUsingConfiguration();

        if (connected) {
            callConnectionConnectedListener();
        }
    }

    private void connectUsingConfiguration() throws Exception {

        if (socketFactory != null) {
            socket = socketFactory.createSocket();
        } else {
            socket = new Socket();
        }

        socket.setKeepAlive(true);
        socket.setReuseAddress(false);
        socket.setSoTimeout(0);
        socket.setTrafficClass(0x04);
        socket.setReceiveBufferSize(config.getReaderBufferSize());
        socket.setSendBufferSize(config.getWriterBufferSize());

        String host = config.getHost();
        int port = config.getPort();
        InetSocketAddress address = new InetSocketAddress(host, port);
        socket.connect(address, config.getConnectTimeOut());

        initConnection();
    }

    private void initConnection() throws IOException {
        boolean isFirstInitialization = stanzaReader == null || stanzaWriter == null;

        try {
            if (isFirstInitialization) {
                stanzaWriter = new StanzaWriter(this);
                stanzaReader = new StanzaReader(this);
            }

            stanzaWriter.init();
            stanzaReader.init();

            stanzaWriter.startup();
            stanzaReader.startup();

            connected = true;

            stanzaWriter.startKeepAliveProcess();

            if (isFirstInitialization) {
                callConnectionCreatedListener();
            }

            addConnectionListener(new ReconnectStrategy(this)); // 重连机制

        } catch (IOException ex) {

            if (stanzaWriter != null) {
                try {
                    stanzaWriter.shutdown();
                } catch (Throwable ignore) {
                }
                stanzaWriter = null;
            }
            if (stanzaReader != null) {
                try {
                    stanzaReader.shutdown();
                } catch (Throwable ignore) {
                }
                stanzaReader = null;
            }
            closeSocket();
            connected = false;
            throw ex;
        }

    }

    private void closeSocket() {

        if (socket != null) {
            try {
                socket.close();
            } catch (Exception e) {
            }
            socket = null;
        }
    }

    @Override
    public void send(String rawText) {
        send(new Stanza(rawText));
    }


    public void send(Stanza stanza) {
        if (!isConnected()) {
            throw new IllegalStateException("Not connected to server.");
        }
        if (stanza == null) {
            throw new NullPointerException("stanza is null.");
        }
        stanzaWriter.send(stanza);
    }

    private void shutdown() {
        connected = false;
        if (stanzaReader != null) {
            stanzaReader.shutdown();
        }
        if (stanzaWriter != null) {
            stanzaWriter.shutdown();
        }
        SystemClock.sleep(1000);
        closeSocket();
        stanzaWriter.cleanup();
        stanzaWriter = null;
        stanzaReader.cleanup();
        stanzaReader = null;
    }

    @Override
    public void disconnect() {

        shutdown();

        callConnectionClosedListener();

    }

    @Override
    public void closeOnError(Exception e) {

        shutdown();

        callConnectionClosedOnErrorListener(e);

    }

    public void setSocketFactory(SocketFactory socketFactory) {
        this.socketFactory = socketFactory;
    }


}
