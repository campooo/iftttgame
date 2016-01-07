/*
 *
 * @author ckb
 * 
 * @date 2015年11月10日 下午8:33:16
 */
package org.campooo.server;

import java.util.Collection;

public interface ConnectionManager {

	public static final int DEFAULT_PORT = 8380;

	public Collection<ServerPort> getPorts();

	public void enableClientListener(boolean enabled);

	public boolean isClientListenerEnabled();

	public void setClientListenerPort(int port);

	public int getClientListenerPort();

}
