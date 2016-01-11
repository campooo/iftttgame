package org.campooo.quickfox;

/**
 * 链接的变化全靠它监听
 *
 * @author campo
 */
public interface ConnectionListener {

    void connectionCreated(Connection conn);

    public void connected(Connection conn);

    public void authenticated(Connection conn);

    public void connectionClosed();

    public void connectionClosedOnError(Exception e);

    public void reconnectingIn(int timeOut);

    public void reconnectSuccessful();

    public void reconnectionFailed(Exception e);

}
