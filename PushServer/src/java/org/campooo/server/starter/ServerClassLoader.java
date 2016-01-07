/*
 * load jar in server path
 *
 * @author ckb
 * 
 * @date 2015年11月8日 下午4:38:50
 */
package org.campooo.server.starter;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

class ServerClassLoader extends URLClassLoader {

	public ServerClassLoader(ClassLoader parent, File libDir) throws MalformedURLException {
		super(new URL[] { libDir.toURI().toURL() }, parent);

		File[] jars = libDir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				boolean accept = false;
				String smallName = name.toLowerCase();
				if (smallName.endsWith(".jar")) {
					accept = true;
				}
				return accept;
			}

		});

		if (jars == null) {
			return;
		}

		for (File aJar : jars) {
			System.out.println("loading jar " + aJar.getName());
			addURL(aJar.toURI().toURL());
		}

	}

}
