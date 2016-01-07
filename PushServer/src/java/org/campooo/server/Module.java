/*
 * life cycle of a module
 * 
 * @author ckb
 * 
 * @date 2015年11月8日 下午4:13:25
 */
package org.campooo.server;

public interface Module {

	public String getName();

	public void initialize(PushServer server);

	public void start();

	public void stop();

	public void destroy();
}
