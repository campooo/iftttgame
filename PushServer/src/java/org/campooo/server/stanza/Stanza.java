package org.campooo.server.stanza;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.campooo.server.utils.StringUtils;

public abstract class Stanza {

	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);

	private static long nextId = 0;

	protected String id = "";
	protected String from = "";
	protected String to = "";

	protected String streamId = "";

	private static final String prefix = StringUtils.randomString(5) + "-";

	private static String nextID() {
		return prefix + Long.toString(nextId++);
	}

	public String getStreamId() {
		return streamId;
	}

	public void setStreamId(String streamId) {
		this.streamId = streamId;
	}

	public String getId() {
		if (StringUtils.isEmpty(id)) {
			id = nextID();
		}
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Stanza stanza = (Stanza) o;

		if (id != null ? !id.equals(stanza.getId()) : stanza.getId() != null) {
			return false;
		}
		if (from != null ? !from.equals(stanza.getFrom()) : stanza.getFrom() != null) {
			return false;
		}
		return (to != null ? !to.equals(stanza.getTo()) : stanza.getTo() != null);
	}

}
