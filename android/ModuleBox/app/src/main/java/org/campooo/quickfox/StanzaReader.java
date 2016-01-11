package org.campooo.quickfox;

import org.campooo.quickfox.stanza.Stanza;
import org.campooo.quickfox.stanza.StanzaParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StanzaReader implements Runnable {

    public static final String CHARSET = "utf-8";

    private Thread readerThread;
    private ExecutorService listenerExecutor;

    private Connection conn;

    private BufferedReader reader;

    private StanzaParser parser;

    private boolean done;

    protected StanzaReader(final PushConnection conn) {
        setConnection(conn);
    }

    void init() throws IOException {
        done = false;
        readerThread = new Thread(this);
        readerThread.setName("Stanza Reader ( " + "quickfox" + " )");
        readerThread.setDaemon(true);

        listenerExecutor = Executors.newSingleThreadExecutor();

        reader = new BufferedReader(new InputStreamReader(conn.socket.getInputStream(), CHARSET),
                conn.config.getReaderBufferSize());

        parser = new StanzaParser(reader);
    }


    public void startup() {
        readerThread.start();
    }

    public void shutdown() {
        done = true;
        listenerExecutor.shutdown();
        if (reader != null) {
            try {
                reader.close();
            } catch (Throwable ignore) {
            }
            reader = null;
        }
    }

    void cleanup() {
        conn.recvListeners.clear();
    }

    private void parseStanza(Thread thread) {
        try {
            while (!done && thread == readerThread) {

                processStanza(parser.parseStanza());

            }
        } catch (Exception e) {
            if (!done) {
                done = true;
                conn.closeOnError(e);
            }
        }
    }

    private void processStanza(Stanza stanza) {
        if (stanza == null) {
            return;
        }
        listenerExecutor.submit(new ListenerNotification(stanza));
    }

    void setConnection(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void run() {
        parseStanza(readerThread);
    }

    private class ListenerNotification implements Runnable {

        private Stanza stanza;

        public ListenerNotification(Stanza stanza) {
            this.stanza = stanza;
        }

        public void run() {
            for (Connection.ListenerWrapper listenerWrapper : conn.recvListeners.values()) {
                listenerWrapper.notifyListener(stanza);
            }
        }
    }
}
