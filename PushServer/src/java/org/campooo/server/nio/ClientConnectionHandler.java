/*
 *
 * @author ckb
 * 
 * @date 2015年11月11日 上午11:28:07
 */
package org.campooo.server.nio;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;
import org.campooo.server.PushServer;
import org.campooo.server.Connection;

public class ClientConnectionHandler extends ConnectionHandler {

	private static final Logger Log = Logger.getLogger(ClientConnectionHandler.class);

	protected ClientConnectionHandler(String serverName) {
		super(serverName);
	}

	@Override
	NIOConnection createNIOConnection(IoSession session) {
		return new NIOConnection(session, PushServer.getInstance().getStanzaDeliverer());
	}

	@Override
	StanzaHandler createStanzaHandler(Connection connection) {
		return new ClientStanzaHandler(PushServer.getInstance().getStanzaRouter(), serverName, connection);
	}

	@Override
	int getMaxIdleTime() {
		return 6 * 60 * 1000;
	}

}
