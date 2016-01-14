/*
 * manage all modules
 * 
 * @author ckb
 * 
 * @date 2015年11月8日 下午4:13:59
 */
package org.campooo.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.campooo.server.nio.ConnectionManagerImpl;
import org.campooo.server.utils.Globals;

public final class PushServer {

	private static final Logger Log = Logger.getLogger(PushServer.class);

	private static PushServer instance;

	private String name;
	private String host;
	private Date startDate;
	private boolean initialized = false;
	private boolean started = false;
	private String serverName;

	private Map<Class<?>, Module> modules = new LinkedHashMap<Class<?>, Module>();

	private List<ServerListener> listeners = new CopyOnWriteArrayList<ServerListener>();

	private static final String STARTER_CLASSNAME = "org.campooo.server.starter.ServerStarter";

	private static final String WARPPER_CLASSNAME = "";

	private boolean shuttingDown = false;
	private File serverHome;
	private ClassLoader loader;

	public static PushServer getInstance() {
		return instance;
	}

	/**
	 * 构造函数 只能调用一次
	 */
	public PushServer() {
		if (instance != null) {
			throw new IllegalStateException("A server is already running");
		}
		instance = this;
		start();
	}

	public void start() {
		Log.info("server starting...");
		try {
			startDate = new Date();

			initialize();

			if (initialized) {
				// 检查数据库连接是否正常
				verifyDataSource();

				loadModules();

				initModules();

				startModules();
			}

			started = true;

			for (ServerListener listener : listeners) {
				listener.serverStarted();
			}
			Log.info("server started");
		} catch (Exception e) {
			Log.error("server start error", e);
			shutdownServer();
		}
	}

	// 初始化
	private void initialize() {
		try {
			locateServer();
			name = System.getProperty("user.name", "PushServer").toLowerCase();
			try {
				host = InetAddress.getLocalHost().getHostName();
				serverName = InetAddress.getLocalHost().getHostName();
				Log.info("server name : " + serverName + " , host : " + host);
			} catch (UnknownHostException ex) {
				Log.warn("unknow host ", ex);
			}
			// 为程序退出添加钩子，如果独立运行于JVM中，则直接退出即可
			if (isStandAlone()) {
				Runtime.getRuntime().addShutdownHook(new ShutDownHookThread());
			}

			loader = Thread.currentThread().getContextClassLoader();

			initialized = true;

		} catch (Exception e) {
			Log.error("server init error", e);
		}

	}

	private void locateServer() throws FileNotFoundException {
		String serverConfigName = "conf" + File.separator + "server.xml";
		if (serverHome == null) {
			URL locateUrl = getClass().getResource("");
			if (locateUrl != null) {
				String clazzStr = locateUrl.toString();

				int start = clazzStr.lastIndexOf("file:") + "file:".length();
				int end = clazzStr.lastIndexOf("PushServer") + "PushServer".length();
				String homePath = clazzStr.substring(start, end);

				if (homePath != null) {
					serverHome = verifyHome(homePath, serverConfigName);
					Log.info("home dir : " + homePath);
				}

			}
		}
		if (serverHome == null) {
			Log.error("could not locate home");
			return;
		}
		Globals.setHomeDirectory(serverHome.toString());
		Globals.setConfigName(serverConfigName);

	}

	/**
	 * 确认是否为根目录
	 * 
	 * @param homeGuess
	 *            根目录
	 * @param serverConfigName
	 *            配置文件名称
	 * @return 根目录 File
	 * @throws FileNotFoundException
	 */
	private File verifyHome(String homeGuess, String serverConfigName) throws FileNotFoundException {
		// 根目录
		File homeDir = new File(homeGuess);
		// 相对根目录的配置文件
		File configFile = new File(homeDir, serverConfigName);
		if (!configFile.exists()) {
			throw new FileNotFoundException(configFile.getAbsolutePath());
		} else {
			try {
				return new File(homeDir.getCanonicalPath());
			} catch (Exception ex) {
				throw new FileNotFoundException();
			}
		}
	}

	/**
	 * 判断服务是否为独立进程在JVM中执行
	 * 
	 * 去JVM中查找已加载的类，如果服务单独执行一定加载过starter
	 * 
	 * 如果JVM中没有starter类，说明服务是被其他程序调用，即不是独占进程
	 * 
	 * @return 服务是否独占进程（JVM）
	 */
	public boolean isStandAlone() {
		boolean standAlone = false;
		try {
			standAlone = Class.forName(STARTER_CLASSNAME) != null;
		} catch (ClassNotFoundException e) {
			standAlone = false;
		}
		return standAlone;
	}

	/**
	 * 确定数据源是否可用
	 */
	private boolean verifyDataSource() {
		return true;
	}

	/**
	 * 注册所有模块
	 */
	private void loadModules() {
		loadModule(ConnectionManagerImpl.class.getName());
		loadModule(SessionManager.class.getName());
		loadModule(StanzaDelivererImpl.class.getName());
		loadModule(RoutingTableImpl.class.getName());
		loadModule(StanzaRouterImpl.class.getName());
	}

	/**
	 * 加载一个模块
	 */
	private void loadModule(String module) {
		try {
			Class<?> modClass = loader.loadClass(module);
			Module mod = (Module) modClass.newInstance();
			this.modules.put(modClass, mod);
		} catch (Exception e) {
			Log.error(" module load error [" + module + "]", e);
			e.printStackTrace();
		}
	}

	private void initModules() {
		for (Module module : modules.values()) {
			boolean isInitialized = false;
			try {
				module.initialize(this);
				isInitialized = true;
			} catch (Exception e) {
				Log.error("module init error [" + module.getName() + "]", e);
				this.modules.remove(module.getClass());
				if (isInitialized) {
					module.stop();
					module.destroy();
				}
			}
		}
	}

	private void startModules() {
		for (Module module : modules.values()) {
			boolean started = false;
			try {
				module.start();
			} catch (Exception e) {
				Log.error("module start error [" + module.getName() + "]", e);
				if (started && module != null) {
					module.stop();
					module.destroy();
				}
			}
		}
	}

	public String getName() {
		return name;
	}

	public void stop() {
		if (isStandAlone()) {
			if (isRestartable()) {
				try {
					Class<?> warpperClass = Class.forName(WARPPER_CLASSNAME);
					// 告知容器服务停止，并传入int类型状态码
					Method stopMethod = warpperClass.getMethod("stop", Integer.TYPE);
					stopMethod.invoke(null, 0);
				} catch (Exception e) {
					Log.error("server stop error", e);
				}
			} else {
				shutdownServer();
				Thread shutdownThread = new ShutdownThread();
				shutdownThread.setDaemon(true);
				shutdownThread.start();
			}
		} else {
			shutdownServer();
		}

	}

	// 服务器监听管理
	public void addServerListener(ServerListener listener) {
		listeners.add(listener);
	}

	public void removeServerListener(ServerListener listener) {
		listeners.remove(listener);
	}

	/**
	 * 是否可以被重启 1.0版本默认服务直接运行，今后可以为服务添加一个容器，服务在容器中可以被启动或停止，
	 * 容器持有服务的状态信息，例如双机热备份需要容器把服务状态保存，并共享给其他节点
	 * 
	 * @return
	 */
	public boolean isRestartable() {
		boolean restartable = false;
		try {
			restartable = Class.forName(WARPPER_CLASSNAME) != null;
		} catch (ClassNotFoundException e) {
			restartable = false;
		}
		return restartable;
	}

	public void restart() {
		if (isStandAlone() && isRestartable()) {
			try {
				Class<?> warpperClass = Class.forName(WARPPER_CLASSNAME);
				Method restartMethod = warpperClass.getMethod("restart", (Class[]) null);
				restartMethod.invoke(null, (Object[]) null);
			} catch (Exception e) {
				Log.error("restart error", e);
			}
		}
	}

	private class ShutDownHookThread extends Thread {

		@Override
		public void run() {
			shutdownServer();
			Log.error("eerver halted");
		}
	}

	private class ShutdownThread extends Thread {

		@Override
		public void run() {
			try {
				Thread.sleep(5000);
				System.exit(0);
			} catch (InterruptedException e) {
				// ignore
			}
		}

	}

	private void shutdownServer() {
		shuttingDown = true;
		for (ServerListener listener : listeners) {
			listener.serverStoping();
		}
		if (modules.isEmpty()) {
			return;
		}
		for (Module module : modules.values()) {
			module.stop();
			module.destroy();
		}

		modules.clear();
		// TODO:释放数据源链接池
		Log.info("Server stoped");

	}

	public boolean isShuttingDown() {
		return shuttingDown;
	}

	public String getHost() {
		return host;
	}

	public String getServerName() {
		return serverName;
	}

	public Date getStartDate() {
		return startDate;
	}

	public boolean isStarted() {
		return started;
	}

	public ConnectionManager getConnectionManager() {
		return (ConnectionManager) modules.get(ConnectionManagerImpl.class);
	}

	public StanzaDeliverer getStanzaDeliverer() {
		return (StanzaDeliverer) modules.get(StanzaDelivererImpl.class);
	}

	public SessionManager getSessionManager() {
		return (SessionManager) modules.get(SessionManager.class);
	}

	public StanzaRouter getStanzaRouter() {
		return (StanzaRouter) modules.get(StanzaRouterImpl.class);
	}

	public RoutingTable getRoutingTable() {
		return (RoutingTable) modules.get(RoutingTableImpl.class);
	}

}
