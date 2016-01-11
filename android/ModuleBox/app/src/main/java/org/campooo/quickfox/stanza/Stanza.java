package org.campooo.quickfox.stanza;

import org.campooo.quickfox.util.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * ckb on 15/11/17.
 */
public class Stanza {

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);

    private static long id = 0;

    protected String stanzaId = "";
    protected String type = "";
    protected String service = "";
    protected String from = "";
    protected String to = "";

    private static final String prefix = StringUtils.randomString(5) + "-";

    private static String nextID() {
        return prefix + Long.toString(id++);
    }

    public String getStanzaId() {
        if (StringUtils.isEmpty(stanzaId)) {
            stanzaId = nextID();
        }
        return stanzaId;
    }

    public Stanza(String src) {
        setService(src);
    }

    public void setStanzaId(String stanzaId) {
        this.stanzaId = stanzaId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
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

        if (stanzaId != null ? !stanzaId.equals(stanza.getStanzaId()) : stanza.getStanzaId() != null) {
            return false;
        }
        if (type != null ? !type.equals(stanza.getType()) : stanza.getType() != null) {
            return false;
        }
        if (service != null ? !service.equals(stanza.getService()) : stanza.getService() != null) {
            return false;
        }
        if (from != null ? !from.equals(stanza.getFrom()) : stanza.getFrom() != null) {
            return false;
        }
        return (to != null ? !to.equals(stanza.getTo()) : stanza.getTo() != null);
    }

    @Override
    public String toString() {
        return "Stanza{" +
                "stanzaId='" + stanzaId + '\'' +
                ", type='" + type + '\'' +
                ", service='" + service + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                '}';
    }
}
