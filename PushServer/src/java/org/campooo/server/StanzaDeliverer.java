/*
 *
 * @author ckb
 * 
 * @date 2015年12月11日 上午1:20:03
 */
package org.campooo.server;

import org.campooo.server.stanza.Stanza;

public interface StanzaDeliverer {

	void deliver(Stanza stanza);

}
