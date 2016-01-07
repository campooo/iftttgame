/*
 *
 * @author ckb
 * 
 * @date 2015年12月11日 上午12:02:08
 */
package org.campooo.server.nio;

import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.campooo.server.Connection;
import org.campooo.server.StanzaRouter;
import org.campooo.server.session.LocalClientSession;
import org.campooo.server.session.LocalSession;
import org.campooo.server.stanza.Stanza;

public class ClientStanzaHandler extends StanzaHandler {

	private static final Logger Log = Logger.getLogger(ClientStanzaHandler.class);

	public ClientStanzaHandler(StanzaRouter router, String serverName, Connection conn) {
		super(router, serverName, conn);
	}

	@Override
	protected void process(Stanza stanza) {
		try {
			stanza.setFrom(connection.getHostAddress());
		} catch (UnknownHostException e) {
			Log.error("stanza from unknown host");
		}
		super.process(stanza);
	}

	@Override
	LocalSession createSession(Connection conn) {
		session = LocalClientSession.createSession(connection);
		return session;
	}

}
