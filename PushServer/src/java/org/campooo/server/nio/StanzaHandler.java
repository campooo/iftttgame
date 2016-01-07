/*
 *
 * @author ckb
 * 
 * @date 2015年12月11日 上午12:00:14
 */
package org.campooo.server.nio;

import org.apache.log4j.Logger;
import org.campooo.server.Connection;
import org.campooo.server.StanzaRouter;
import org.campooo.server.session.LocalSession;
import org.campooo.server.stanza.Stanza;

public abstract class StanzaHandler {

	private static final Logger Log = Logger.getLogger(StanzaHandler.class);

	protected static String CHARSET = "utf-8";

	protected Connection connection;

	private boolean sessionCreated = false;

	protected LocalSession session;

	protected String serverName;

	private StanzaRouter router;

	public StanzaHandler(StanzaRouter router, String serverName, Connection conn) {
		this.router = router;
		this.serverName = serverName;
		this.connection = conn;
	}

	public void process(String stanzaSrc, StanzaParser parser) {
		if (!sessionCreated) {
			session = createSession(connection);
			sessionCreated = true;
		}
		Stanza stanza = parser.parse(stanzaSrc);
		if (stanza == null)
			return;
		stanza.setStreamId(session.getStreamId());
		router.route(stanza);
	}

	protected void process(Stanza stanza) {
		router.route(stanza);
	}

	abstract LocalSession createSession(Connection conn);

}
