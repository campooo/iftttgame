package org.campooo.server.session;

import java.net.UnknownHostException;
import java.util.Date;

public interface Session {

	public static final int VERSION = 1;

	public static final int STATUS_CLOSED = -1;
	public static final int STATUS_CONNECTED = 1;
	public static final int STATUS_AUTHENTICATED = 3;

	public int getStatus();

	public String getServerName();

	public Date getCreationDate();

	public Date getLastActiveDate();

	public void close();

	public boolean isClosed();

	public boolean isSecure();

	public String getHostAddress() throws UnknownHostException;

	public String getHostName() throws UnknownHostException;

	public void deliverRawText(String text);

	public boolean validate();
}
