package org.campooo.server.session;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.campooo.server.Connection;
import org.campooo.server.SessionManager;
import org.campooo.server.stanza.Stanza;
import org.campooo.server.utils.StringUtils;

public abstract class LocalSession implements Session {

	private static final Logger Log = Logger.getLogger(LocalSession.class);

	private static long id = 0;

	private final String streamId = StringUtils.randomString(5) + "-" + Long.toString(id++);

	protected static String CHARSET = "UTF-8";

	private String address;

	protected int status = STATUS_CONNECTED;

	protected Connection conn;

	protected SessionManager sessionManager;

	private String serverName;

	private long startDate = System.currentTimeMillis();

	private long lastActiveDate;

	private final Map<String, Object> sessionData = new HashMap<String, Object>();

	public LocalSession(String serverName, Connection connection) {
		conn = connection;
		this.serverName = serverName;
		this.sessionManager = SessionManager.getInstance();
		try {
			address = conn.getHostAddress();
		} catch (UnknownHostException e) {
			Log.error("on create LocalSession", e);
		}
	}

	public String getStreamId() {
		return streamId;
	}

	public Connection getConnection() {
		return conn;
	}

	public void process(Stanza stanza) {
		if (canProcess(stanza)) {
			deliver(stanza);
		}
	}

	abstract boolean canProcess(Stanza packet);

	@Override
	public int getStatus() {
		return status;
	}

	abstract void deliver(Stanza packet);

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String getServerName() {
		return serverName;
	}

	@Override
	public Date getCreationDate() {
		return new Date(startDate);
	}

	@Override
	public Date getLastActiveDate() {
		return new Date(lastActiveDate);
	}

	public void setSessionData(String key, Object value) {
		synchronized (sessionData) {
			sessionData.put(key, value);
		}
	}

	public void removeSessionData(String key) {
		synchronized (sessionData) {
			sessionData.remove(key);
		}
	}

	public Object getSessionData(String key) {
		synchronized (sessionData) {
			return sessionData.get(key);
		}
	}

	@Override
	public void close() {
		if (conn != null) {
			conn.close();
		}
	}

	@Override
	public boolean isClosed() {
		return conn.isClosed();
	}

	@Override
	public boolean isSecure() {
		return conn.isSecure();
	}

	@Override
	public String getHostAddress() throws UnknownHostException {
		return conn.getHostAddress();
	}

	@Override
	public String getHostName() throws UnknownHostException {
		return conn.getHostName();
	}

	@Override
	public void deliverRawText(String text) {
		if (conn != null) {
			conn.deliverRawText(text);
		}
	}

	@Override
	public boolean validate() {
		return conn.validate();
	}

	public void active() {
		lastActiveDate = System.currentTimeMillis();
	}

	@Override
	public String toString() {
		return super.toString() + " status: " + status + " address: " + address;
	}

}
