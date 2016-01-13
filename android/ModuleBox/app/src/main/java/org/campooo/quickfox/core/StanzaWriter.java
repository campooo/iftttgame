package org.campooo.quickfox.core;

import android.os.SystemClock;

import org.campooo.app.Global;
import org.campooo.quickfox.log.Logger;
import org.campooo.quickfox.log.QLog;
import org.campooo.quickfox.stanza.Stanza;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class StanzaWriter implements Runnable {

    private static final Logger Log = QLog.getLogger(StanzaWriter.class);

    private static final String CRLF = "\r\n";

    public static final String CHARSET = "utf-8";

    private Thread writerThread;
    private Thread keepAliveThread;
    private BufferedWriter writer;
    private Connection conn;
    private final BlockingQueue<Stanza> queue = new LinkedBlockingQueue<Stanza>(100);
    private boolean done;

    private long lastActive = System.currentTimeMillis();

    protected StanzaWriter(PushConnection conn) {
        setConnection(conn);
    }

    void init() throws IOException {
        done = false;
        writerThread = new Thread(this);
        writerThread.setName("Stanza Writer ( " + "quickfox" + " )");
        writerThread.setDaemon(true);

        writer = new BufferedWriter(new OutputStreamWriter(conn.socket.getOutputStream(), CHARSET),
                conn.config.getWriterBufferSize());
    }

    public void startup() {
        writerThread.start();
    }

    public void shutdown() {
        done = true;
        synchronized (queue) {
            queue.notifyAll();
        }
        if (writer != null) {
            try {
                writer.close();
            } catch (Throwable ignore) {
            }
            writer = null;
        }
    }

    void cleanup() {
        conn.sendListeners.clear();
    }

    private void writeStanza(Thread thread) {
        try {
            // 开启会话
            openStream();

            while (!done && writerThread == thread) {
                Stanza stanza = nextStanza();
                if (stanza != null) {
                    synchronized (writer) {
                        writer.write(stanza.getService());
                        writer.write(CRLF);
                        writer.flush();
                        lastActive = System.currentTimeMillis();
                    }
                }
            }

            try {
                synchronized (writer) {
                    while (!queue.isEmpty()) {
                        Stanza stanza = queue.remove();
                        writer.write(stanza.toString());
                        writer.write(CRLF);
                    }
                    writer.flush();
                }
            } catch (Exception e) {
                Log.err(e);
            }
            queue.clear();
            try {
                closeStream();
            } catch (Exception e) {
                // ignore
            } finally {
                try {
                    writer.close();
                } catch (Exception e) {
                    // ignore
                }
            }

        } catch (IOException e) {
            if (!done) {
                done = true;
                conn.closeOnError(e);
            }
        }

    }

    public void send(Stanza stanza) {
        if (!done) {
            try {
                queue.put(stanza);
            } catch (InterruptedException ie) {
                Log.err(ie);
                return;
            }
            synchronized (queue) {
                queue.notifyAll();
            }
            callStanzaSendingListeners(stanza);
        }
    }

    void callStanzaSendingListeners(Stanza stanza) {
        for (Connection.ListenerWrapper listenerWrapper : conn.sendListeners.values()) {
            listenerWrapper.notifyListener(stanza);
        }
    }

    /**
     * 开启会话传入一些客户端的初始信息，如版本号，客户端名称等
     */
    private void openStream() throws IOException {
        // StringBuilder stream = new StringBuilder();
        // stream.append("<stream:stream");
        // stream.append(" version=\"1.0\">");
        // // writer.write(stream.toString());
        // // writer.flush();
        // ByteBuffer buf =
        // ByteBuffer.allocate(stream.toString().getBytes().length);
        // buf.put(stream.toString().getBytes());
        // buf.flip();
        // conn.socket.getChannel().write(buf);
    }

    private void closeStream() throws IOException {
        // writer.write("</stream:stream>");
        // writer.flush();
    }

    private Stanza nextStanza() {
        Stanza stanza = null;
        while (!done && (stanza = queue.poll()) == null) {
            try {
                synchronized (queue) {
                    queue.wait();
                }
            } catch (InterruptedException ie) {

            }
        }
        return stanza;
    }

    void startKeepAliveProcess() {
        int keepAliveInterval = QuickFoxConfiguration.getKeepAliveInterval();
        if (keepAliveInterval > 0) {
            KeepAliveStrategy task = new KeepAliveStrategy(keepAliveInterval);
            keepAliveThread = new Thread(task);
            task.setThread(keepAliveThread);
            keepAliveThread.setDaemon(true);
            keepAliveThread.setName("Keep Alive ( " + "quickfox" + " )");
            keepAliveThread.start();
        }
    }

    public Connection getConnection() {
        return conn;
    }

    public void setConnection(Connection conn) {
        this.conn = conn;
    }

    class KeepAliveStrategy implements Runnable {

        private int delay;
        private Thread thread;

        public KeepAliveStrategy(int delay) {
            this.delay = delay;
        }

        protected void setThread(Thread thread) {
            this.thread = thread;
        }

        @Override
        public void run() {
            try {
                // 预留程序启动时间
                Thread.sleep(5000);
            } catch (InterruptedException ie) {
                // ignore
            }
            while (!done && keepAliveThread == thread) {
                synchronized (writer) {
                    if (System.currentTimeMillis() - lastActive >= delay) {
                        try {
                            writer.write(CRLF);
                            writer.flush();
                        } catch (Exception e) {
                            conn.closeOnError(e);
                        }
                    }
                }
                SystemClock.sleep(delay);
            }
        }

    }

    @Override
    public void run() {
        writeStanza(writerThread);
    }
}
