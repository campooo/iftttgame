/*
 *
 * @author ckb
 * 
 * @date 2015年12月11日 上午1:31:35
 */
package org.campooo.server;

import org.apache.log4j.Logger;
import org.campooo.server.stanza.Stanza;

public class StanzaDelivererImpl extends BaseModule implements StanzaDeliverer {

	private static final Logger Log = Logger.getLogger(StanzaDelivererImpl.class);

	private PushServer server;
	private RoutingTable routingTable;

	public StanzaDelivererImpl() {
		super("Packet Deliverer");
	}

	@Override
	public void deliver(Stanza stanza) {
		routingTable.routeStanza(stanza.getTo(), stanza, false);
	}

	@Override
	public void initialize(PushServer server) {
		super.initialize(server);
		this.server = server;
		this.routingTable = server.getRoutingTable();
	}

}
