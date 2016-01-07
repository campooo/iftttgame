package org.campooo.server.stanza;

import org.apache.log4j.Logger;

public class RawTextStanza extends Stanza  {

	private static final Logger Log = Logger.getLogger(RawTextStanza.class);

	private String text = "";

	public RawTextStanza() {
		getId();
	}

	

	public String getText() {
		return text;
	}



	public void setText(String text) {
		this.text = text;
	}

}
