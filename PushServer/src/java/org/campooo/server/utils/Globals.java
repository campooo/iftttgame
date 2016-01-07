/*
 * globals
 * 
 * @author ckb
 * 
 * @date 2015年11月10日 上午12:20:37
 */
package org.campooo.server.utils;

import java.io.File;

import org.apache.log4j.Logger;

public class Globals {
	private static final Logger Log = Logger.getLogger(Globals.class);

	private static String home = null;

	private static boolean failedLoading = false;

	private static XMLProperties serverProperties = null;

	private static String SERVER_CONFIG_FILENAME = "conf" + File.separator + "server.xml";

	public static void setHomeDirectory(String pathName) {
		File mh = new File(pathName);
		if (!mh.exists()) {
			Log.error("home directory is not exists (" + pathName + ")");
		} else if (!mh.canRead() || !mh.canWrite()) {
			Log.error("home directory must be readable and writeable (" + pathName + ")");
		} else {
			home = pathName;
		}
	}

	private synchronized static void loadServerProperties() {
		if (serverProperties == null) {
			if (home == null && !failedLoading) {
				failedLoading = true;
				Log.error("can not load server config file");
			} else {
				try {
					serverProperties = new XMLProperties(new File(home + File.separator + getConfigName()));
				} catch (Exception e) {
					failedLoading = true;
					Log.error(e.getMessage(), e);
				}
			}
		}
	}

	public static String getXMLProperty(String name, String defaultValue) {
		if (serverProperties == null) {
			loadServerProperties();
		}
		if (serverProperties == null) {
			return defaultValue;
		}
		String value = serverProperties.getProperty(name);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}

	public static boolean getBooleanProperty(String name, boolean defaultValue) {
		String value = getXMLProperty(name, String.valueOf(defaultValue));
		if (value != null) {
			return Boolean.valueOf(value);
		} else {
			return defaultValue;
		}
	}

	public static int getIntProperty(String name, int defaultValue) {
		String value = getXMLProperty(name, String.valueOf(defaultValue));
		if (value != null) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException nfe) {
				// Ignore.
			}
		}
		return defaultValue;
	}

	public static void setXMLProperty(String name, String value) {
		if (serverProperties == null) {
			loadServerProperties();
		}
		serverProperties.setProperty(name, value);
	}

	public static String getHomeDirectory() {
		if (serverProperties == null) {
			loadServerProperties();
		}
		return home;
	}

	public static void setConfigName(String configName) {
		SERVER_CONFIG_FILENAME = configName;
	}

	public static String getConfigName() {
		return SERVER_CONFIG_FILENAME;
	}
}
