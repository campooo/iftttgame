package org.campooo.quickfox;

public class ConnectionConfiguration {

    private boolean debuggerEnabled = QuickFoxConfiguration.DEBUG_ENABLED;

    private boolean reconnectionAllowed = true;

    private String host;
    private int port;

    private String resource;

    private int readBuffer = 8 * 1024;
    private int writBuffer = 8 * 1024;

    private int connectTimeOut = 5000;

    // FIXME 代理加载这里

    public ConnectionConfiguration(String host, int port) {
        init(host, port);
    }

    private void init(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isDebuggerEnabled() {
        return debuggerEnabled;
    }

    public void setDebuggerEnabled(boolean debuggerEnabled) {
        this.debuggerEnabled = debuggerEnabled;
    }

    public void setReconnectionAllowed(boolean isAllowed) {
        this.reconnectionAllowed = isAllowed;
    }

    public boolean isReconnectionAllowed() {
        return this.reconnectionAllowed;
    }

    public String getResource() {
        return resource;
    }

    public int getReaderBufferSize() {
        return readBuffer;
    }

    public void setReaderBufferSize(int size) {
        readBuffer = size;
    }

    public int getWriterBufferSize() {
        return writBuffer;
    }

    public void setWriterBufferSize(int size) {
        writBuffer = size;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public int getConnectTimeOut() {
        return connectTimeOut;
    }

    public void setConnectTimeOut(int connectTimeOut) {
        if (connectTimeOut > 0) {
            this.connectTimeOut = connectTimeOut;
        }
    }
}
