/*
 *
 * @author ckb
 * 
 * @date 2015年11月11日 上午11:09:39
 */
package org.campooo.server.nio;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.campooo.server.BaseModule;
import org.campooo.server.PushServer;
import org.campooo.server.ConnectionManager;
import org.campooo.server.ServerPort;
import org.campooo.server.utils.Globals;

public class ConnectionManagerImpl extends BaseModule implements ConnectionManager {

	private static final Logger Log = Logger.getLogger(ConnectionManagerImpl.class);

	private SocketAcceptor clientAcceptor;

	private PushServer server;
	private List<ServerPort> ports;
	private String serverName;

	private String localIPAddress;

	public ConnectionManagerImpl() {
		super("Connection Manager");
		ports = new ArrayList<ServerPort>(4);
	}

	private synchronized void createListeners() {
		createClientListeners(); // 客户端Acceptor
	}

	private synchronized void startListeners() {
		try {
			localIPAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			if (localIPAddress == null) {
				localIPAddress = "Unknown";
			}
			Log.error("unknow host");
		}
		startClientListeners(localIPAddress);
	}

	private void createClientListeners() {
		if (isClientListenerEnabled()) {
			clientAcceptor = buildSocketAcceptor();
			clientAcceptor.getFilterChain().addFirst("codec",
					new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"), LineDelimiter.CRLF.getValue(), LineDelimiter.CRLF.getValue())));
			clientAcceptor.getFilterChain().addAfter("codec", "keepalive", heartBeatFilter);

		}
	}

	private KeepAliveFilter heartBeatFilter = new KeepAliveFilter(new KeepAliveMessageFactory() {

		@Override
		public boolean isResponse(IoSession session, Object message) {
			return false;
		}

		@Override
		public boolean isRequest(IoSession session, Object message) {
			return "".equals((String) message);
		}

		@Override
		public Object getResponse(IoSession session, Object request) {
			return null;
		}

		@Override
		public Object getRequest(IoSession session) {
			return null;
		}
	}, IdleStatus.BOTH_IDLE);

	private void startClientListeners(String localIPAddress) {
		if (isClientListenerEnabled()) {
			int port = getClientListenerPort();
			try {
				clientAcceptor.setHandler(new ClientConnectionHandler(serverName));
				clientAcceptor.setDefaultLocalAddress(new InetSocketAddress(port));
				clientAcceptor.bind();
				Log.info("client listener start at " + localIPAddress + " : " + port);
				ports.add(new ServerPort(port, serverName, localIPAddress, false, null, ServerPort.Type.client));
			} catch (Exception e) {
				Log.error("client listener start error", e);
			}
		}
	}

	private void stopClientListeners() {
		if (clientAcceptor != null) {
			clientAcceptor.unbind();
			for (ServerPort port : ports) {
				if (port.isClientPort() && !port.isSecure()) {
					ports.remove(port);
					break;
				}
			}
			clientAcceptor = null;
		}
	}

	private SocketAcceptor buildSocketAcceptor() {
		SocketAcceptor socketAcceptor = new NioSocketAcceptor();
		SocketSessionConfig config = socketAcceptor.getSessionConfig();
		config.setReadBufferSize(8192);
		config.setSendBufferSize(8192);
		config.setSoLinger(-1);
		config.setReuseAddress(true);
		config.setIdleTime(IdleStatus.BOTH_IDLE, 10);
		config.setTcpNoDelay(config.isTcpNoDelay());
		return socketAcceptor;
	}

	@Override
	public Collection<ServerPort> getPorts() {
		return Collections.unmodifiableCollection(ports);
	}

	@Override
	public void enableClientListener(boolean enabled) {
		if (enabled == isClientListenerEnabled()) {
			return;
		}
		if (enabled) {
			Globals.setXMLProperty("client.listener.active", "true");
			createClientListeners();
			startClientListeners(localIPAddress);
		} else {
			Globals.setXMLProperty("client.listener.active", "false");
			stopClientListeners();
		}
	}

	@Override
	public boolean isClientListenerEnabled() {
		return Globals.getBooleanProperty("client.listener.active", true);
	}

	@Override
	public void setClientListenerPort(int port) {
		if (port == getClientListenerPort()) {
			return;
		}
		Globals.setXMLProperty("client.listener.port", String.valueOf(port));
		stopClientListeners();
		if (isClientListenerEnabled()) {
			createClientListeners();
			startClientListeners(localIPAddress);
		}
	}

	@Override
	public int getClientListenerPort() {
		return Globals.getIntProperty("client.listener.port", DEFAULT_PORT);
	}

	@Override
	public void initialize(PushServer server) {
		super.initialize(server);
		this.server = server;
		serverName = server.getServerName();
	}

	@Override
	public void start() {
		super.start();
		createListeners();
		startListeners();
	}

	@Override
	public void stop() {
		super.stop();
		stopClientListeners();
		serverName = null;
	}
}
