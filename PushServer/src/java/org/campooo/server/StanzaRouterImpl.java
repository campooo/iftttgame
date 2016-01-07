/*
 *
 * @author ckb
 * 
 * @date 2015年12月11日 上午1:44:59
 */
package org.campooo.server;

import org.apache.log4j.Logger;
import org.campooo.server.stanza.RawTextStanza;
import org.campooo.server.stanza.Stanza;

public class StanzaRouterImpl extends BaseModule implements StanzaRouter {

	private static final Logger Log = Logger.getLogger(StanzaRouterImpl.class);

	public StanzaRouterImpl() {
		super("Packet Router");
	}

	@Override
	public void route(Stanza stanza) {
		String text = (	(RawTextStanza)stanza).getText();
		
	}

	@Override
	public void initialize(PushServer server) {
		super.initialize(server);

	}

}
