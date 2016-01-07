/*
 *
 * @author ckb
 * 
 * @date 2015年11月11日 上午11:25:01
 */
package org.campooo.server.nio;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderException;
import org.campooo.server.Connection;

public abstract class ConnectionHandler extends IoHandlerAdapter {
	private static final Logger Log = Logger.getLogger(ConnectionHandler.class);

	static final String CHARSET = "utf-8";
	static final String PARSER = "PARSER";
	protected static final String HANDLER = "HANDLER";
	protected static final String CONNECTION = "CONNECTION";

	protected String serverName;

	protected ConnectionHandler(String serverName) {
		this.serverName = serverName;
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		super.sessionOpened(session);
		final StanzaParser parser = new StanzaParser();
		session.setAttribute(PARSER, parser);
		final NIOConnection connection = createNIOConnection(session);
		session.setAttribute(CONNECTION, connection);
		session.setAttribute(HANDLER, createStanzaHandler(connection));
		final int idleTime = getMaxIdleTime() / 2;
		if (idleTime > 0) {
			session.getConfig().setIdleTime(IdleStatus.READER_IDLE, idleTime);
		}
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		Connection conn = (Connection) session.getAttribute(CONNECTION);
		conn.close();
		super.sessionClosed(session);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		super.sessionIdle(session, status);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		super.exceptionCaught(session, cause);
		Log.error(cause);
		if (cause instanceof IOException) {
			Log.debug("ConnectionHandler: ", cause);
		} else if (cause instanceof ProtocolDecoderException) {
			Log.warn("Closing session due to exception: " + session, cause);
			session.close(true);
		} else {
			Log.error(cause.getMessage(), cause);
		}
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		StanzaHandler handler = (StanzaHandler) session.getAttribute(HANDLER);
		StanzaParser parser = (StanzaParser) session.getAttribute(PARSER);

		handler.process((String) message, parser);

	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		super.messageSent(session, message);
	}

	@Override
	public void inputClosed(IoSession session) throws Exception {
		super.inputClosed(session);
	}

	abstract NIOConnection createNIOConnection(IoSession session);

	abstract StanzaHandler createStanzaHandler(Connection connection);

	abstract int getMaxIdleTime();
}
