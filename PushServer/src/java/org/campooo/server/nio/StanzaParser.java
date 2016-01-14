/*
 *
 * @author ckb
 * 
 * @date 2015年12月11日 上午12:19:18
 */
package org.campooo.server.nio;

import org.apache.log4j.Logger;
import org.campooo.server.stanza.RawTextStanza;
import org.campooo.server.stanza.Stanza;

import com.google.gson.Gson;

public class StanzaParser {
	private static final Logger Log = Logger.getLogger(StanzaParser.class);

	private Gson gsonParser = new Gson();

	public Stanza parse(String stanzaStr) {
		Stanza stanza = gsonParser.fromJson(stanzaStr, RawTextStanza.class);

		return stanza;
	}
}
