/*
 * the starter of server
 * 
 * @author ckb
 * 
 * @date 2015年11月8日 下午4:38:21
 */
package org.campooo.server.starter;

import java.io.File;
import java.net.URL;

public final class ServerStarter {

	private static final String DEFAULT_LIB_PATH = "target/lib";

	public static void main(String[] args) {
		new ServerStarter().start();
	}

	public final void start() {
		try {
			final ClassLoader parent = findParentClassLoader();
			// 工程编译后，将目前服务代码打成jar包放在 和starter同级目录下
			URL clazzUrl = getClass().getResource("");
			if (clazzUrl != null) {
				String clazzStr = clazzUrl.toString();

				int start = clazzStr.lastIndexOf("file:") + "file:".length();
				int end = clazzStr.lastIndexOf("PushServer") + "PushServer".length();
				String homePath = clazzStr.substring(start, end);
				System.out.println("home path " + homePath);
				File libDir = new File(homePath, DEFAULT_LIB_PATH);
				System.out.println("load jars in " + libDir.toString());
				ClassLoader loader = new ServerClassLoader(parent, libDir);

				Thread.currentThread().setContextClassLoader(loader);

				Class<?> containerClass = loader.loadClass("org.campooo.server.PushServer");
				containerClass.newInstance();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ClassLoader findParentClassLoader() {
		ClassLoader parent = Thread.currentThread().getContextClassLoader();
		if (parent == null) {
			parent = ServerStarter.class.getClassLoader();
			if (parent == null) {
				parent = ClassLoader.getSystemClassLoader();
			}
		}
		return parent;
	}
}
