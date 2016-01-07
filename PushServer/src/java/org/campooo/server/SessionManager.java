/*
 *
 * @author ckb
 * 
 * @date 2015年12月11日 上午1:27:06
 */
package org.campooo.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.campooo.server.session.LocalClientSession;
import org.campooo.server.session.LocalSession;

public class SessionManager extends BaseModule {

	private static final Logger Log = Logger.getLogger(SessionManager.class);

	private PushServer server;
	private String serverName;
	private RoutingTable routingTable;

	private final AtomicInteger connectionsCounter = new AtomicInteger(0);

	private Map<String, LocalClientSession> localClientSessions = new ConcurrentHashMap<String, LocalClientSession>();

	public static SessionManager getInstance() {
		return PushServer.getInstance().getSessionManager();
	}

	public SessionManager() {
		super("Session Manager");
	}

	public LocalClientSession getSession(String streamId) {
		return localClientSessions.get(streamId);
	}

	public LocalClientSession createClientSession(Connection conn) {
		if (serverName == null) {
			throw new IllegalStateException("Server not initialized");
		}
		LocalClientSession session = new LocalClientSession(serverName, conn);
		conn.init(session);
		conn.registerCloseListener(clientSessionListener, session);

		localClientSessions.put(session.getStreamId(), session);
		connectionsCounter.incrementAndGet();
		return session;
	}

	private ClientSessionListener clientSessionListener = new ClientSessionListener();

	private class ClientSessionListener implements ConnectionCloseListener {
		public void onConnectionClose(Object handback) {
			try {
				LocalClientSession session = (LocalClientSession) handback;
				try {
					// TODO
				} finally {
					removeSession(session);
				}
			} catch (Exception e) {
				Log.error("ClientSessionListener onConnectionClose error", e);
			}
		}
	}

	/**
	 * FIXME 接口
	 * 
	 * @param session
	 * @return
	 */
	public boolean removeSession(LocalClientSession session) {
		if (session == null || serverName == null) {
			return false;
		}
		Object res = localClientSessions.remove(session.getStreamId());
		routingTable.removeClientRoute(session.getStreamId());
		connectionsCounter.decrementAndGet();
		return res != null;
	}

	@Override
	public void initialize(PushServer server) {
		super.initialize(server);
		this.server = server;
		routingTable = server.getRoutingTable();
		serverName = server.getServerName();
	}

	@Override
	public void start() {
		super.start();
	}

	@Override
	public void stop() {
		super.stop();
		for (LocalSession session : localClientSessions.values()) {
			try {
				session.getConnection().systemShutdown();
			} catch (Throwable t) {
				// Ignore.
			}
		}
	}

}