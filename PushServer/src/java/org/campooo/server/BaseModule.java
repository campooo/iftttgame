/*
 *
 * @author ckb
 * 
 * @date 2015年11月8日 下午4:12:49
 */
package org.campooo.server;

public class BaseModule implements Module {

	private String name;

	public BaseModule(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void initialize(PushServer server) {
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}

	@Override
	public void destroy() {
	}
}
