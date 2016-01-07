/*
 *
 * @author ckb
 * 
 * @date 2015年12月10日 下午11:49:52
 */
package org.campooo.server.nio;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;
import org.campooo.server.Connection;
import org.campooo.server.ConnectionCloseListener;
import org.campooo.server.StanzaDeliverer;
import org.campooo.server.session.LocalSession;
import org.campooo.server.session.Session;
import org.campooo.server.stanza.Stanza;

public class NIOConnection implements Connection {

	private static final Logger Log = Logger.getLogger(NIOConnection.class);

	private static final String CRLF = "\r\n";

	public static final String CHARSET = "UTF-8";

	private LocalSession session;

	private IoSession ioSession;

	private ConnectionCloseListener closeListener;

	private StanzaDeliverer backupDeliverer;

	private boolean closed;

	public NIOConnection(IoSession session, StanzaDeliverer stanzaDeliverer) {
		this.ioSession = session;
		this.backupDeliverer = stanzaDeliverer;
		closed = false;
	}

	@Override
	public boolean validate() {
		if (isClosed()) {
			return false;
		}
		return !isClosed();
	}

	@Override
	public void init(LocalSession owner) {
		session = owner;
	}

	@Override
	public byte[] getAddress() throws UnknownHostException {
		return ((InetSocketAddress) ioSession.getRemoteAddress()).getAddress().getAddress();
	}

	@Override
	public String getHostAddress() throws UnknownHostException {
		return ((InetSocketAddress) ioSession.getRemoteAddress()).getAddress().getHostAddress();
	}

	@Override
	public String getHostName() throws UnknownHostException {
		return ((InetSocketAddress) ioSession.getRemoteAddress()).getAddress().getHostName();
	}

	@Override
	public void close() {
		boolean closedSuccessfully = false;
		synchronized (this) {
			if (!isClosed()) {
				if (session != null) {
					session.setStatus(Session.STATUS_CLOSED);
				}
				ioSession.close(true);
				closed = true;
				closedSuccessfully = true;
			}
		}
		if (closedSuccessfully) {
			notifyCloseListeners();
		}
	}

	@Override
	public void systemShutdown() {
		close();
	}

	@Override
	public boolean isClosed() {
		if (session == null) {
			return closed;
		}
		return session.getStatus() == Session.STATUS_CLOSED;
	}

	@Override
	public boolean isSecure() {
		return ioSession.getFilterChain().contains("tls");
	}

	@Override
	public void registerCloseListener(ConnectionCloseListener listener, Object handbackMessage) {
		if (closeListener != null) {
			throw new IllegalStateException("Close listener already configured");
		}
		if (isClosed()) {
			listener.onConnectionClose(session);
		} else {
			closeListener = listener;
		}
	}

	@Override
	public void removeCloseListener(ConnectionCloseListener listener) {
		if (closeListener == listener) {
			closeListener = null;
		}
	}

	private void notifyCloseListeners() {
		if (closeListener != null) {
			try {
				closeListener.onConnectionClose(session);
			} catch (Exception e) {
				Log.error("Error notifying listener: " + closeListener, e);
			}
		}
	}

	@Override
	public void deliverRawText(String text) {
		if (isClosed()) {
			return;
		}
		ioSession.write(text);
		ioSession.write(CRLF);
	}

	@Override
	public boolean isCompressed() {
		return false;
	}

	@Override
	public StanzaDeliverer getStanzaDeliverer() {
		return backupDeliverer;
	}

	@Override
	public void deliver(Stanza stanza) {
		if (isClosed()) {
			backupDeliverer.deliver(stanza);
		} else {
			ioSession.write(stanza);
		}
	}

}
