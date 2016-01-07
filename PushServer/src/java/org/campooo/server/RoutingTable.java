/*
 *
 * @author ckb
 * 
 * @date 2015年12月11日 上午1:27:56
 */
package org.campooo.server;

import org.campooo.server.session.LocalClientSession;
import org.campooo.server.stanza.Stanza;

public interface RoutingTable {

	boolean addClientRoute(String id, LocalClientSession destination);

	void routeStanza(String id, Stanza stanza, boolean fromServer);

	boolean hasClientRoute(String id);

	LocalClientSession getClientRoute(String id);

	boolean removeClientRoute(String id);

}
