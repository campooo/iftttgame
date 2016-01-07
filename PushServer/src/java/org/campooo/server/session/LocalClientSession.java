package org.campooo.server.session;

import org.campooo.server.Connection;
import org.campooo.server.SessionManager;
import org.campooo.server.stanza.Stanza;

public class LocalClientSession extends LocalSession implements ClientSession {

	private String client = "";

	public LocalClientSession(String serverName, Connection connection) {
		super(serverName, connection);
	}

	public static LocalClientSession createSession(Connection connection) {
		LocalClientSession session = SessionManager.getInstance().createClientSession(connection);
		return session;
	}

	@Override
	boolean canProcess(Stanza packet) {
		return true;
	}

	@Override
	void deliver(Stanza stanza) {
		if (conn != null && !conn.isClosed()) {
			conn.deliver(stanza);
		}
	}

	@Override
	public String getClient() {
		return client;
	}

}
