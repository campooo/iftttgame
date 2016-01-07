/*
 *
 * @author ckb
 * 
 * @date 2015年12月11日 上午1:10:57
 */
package org.campooo.server;

import org.campooo.server.stanza.Stanza;

public interface StanzaRouter {

	void route(Stanza stanza);

}
