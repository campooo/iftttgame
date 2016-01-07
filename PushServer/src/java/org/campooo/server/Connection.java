/*
 *
 * @author ckb
 * 
 * @date 2015年11月10日 下午8:32:29
 */
package org.campooo.server;

import java.net.UnknownHostException;

import org.campooo.server.session.LocalSession;
import org.campooo.server.stanza.Stanza;

public interface Connection {

	boolean validate();

	void init(LocalSession owner);

	byte[] getAddress() throws UnknownHostException;

	String getHostAddress() throws UnknownHostException;

	String getHostName() throws UnknownHostException;

	void close();

	void systemShutdown();

	boolean isClosed();

	boolean isSecure();

	void registerCloseListener(ConnectionCloseListener listener, Object handbackMessage);

	void removeCloseListener(ConnectionCloseListener listener);

	void deliver(Stanza stanza);

	void deliverRawText(String text);

	boolean isCompressed();

	StanzaDeliverer getStanzaDeliverer();

}
