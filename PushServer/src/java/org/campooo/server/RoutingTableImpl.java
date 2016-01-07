/*
 *
 * @author ckb
 * 
 * @date 2015年12月11日 上午1:31:15
 */
package org.campooo.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.campooo.server.session.ClientSession;
import org.campooo.server.session.LocalClientSession;
import org.campooo.server.stanza.Stanza;

public class RoutingTableImpl extends BaseModule implements RoutingTable {

	private static final Logger Log = Logger.getLogger(RoutingTableImpl.class);

	private String serverName;
	private PushServer server;

	Map<String, LocalClientSession> routes = new ConcurrentHashMap<String, LocalClientSession>();

	@Override
	public boolean addClientRoute(String username, LocalClientSession route) {
		return routes.put(username, route) != route;
	}

	Collection<LocalClientSession> getClientRoutes() {
		List<LocalClientSession> sessions = new ArrayList<LocalClientSession>();
		for (LocalClientSession route : routes.values()) {
			if (route instanceof ClientSession) {
				sessions.add(route);
			}
		}
		return sessions;
	}

	void removeRoute(String address) {
		routes.remove(address);
	}

	public RoutingTableImpl() {
		super("Routing table");
	}

	@Override
	public void routeStanza(String streamId, Stanza stanza, boolean fromServer) {
		LocalClientSession session = getClientRoute(streamId);
		session.deliverRawText(stanza.toString());
	}

	@Override
	public boolean hasClientRoute(String id) {
		return routes.containsKey(id);
	}

	@Override
	public LocalClientSession getClientRoute(String username) {
		LocalClientSession session = routes.get(username);
		if (session == null) {
			// TODO
		}
		return session;
	}

	@Override
	public boolean removeClientRoute(String id) {
		return routes.remove(id) != null;
	}

	@Override
	public void initialize(PushServer server) {
		super.initialize(server);
		this.server = server;
		serverName = server.getServerName();
	}

}
