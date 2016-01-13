package org.campooo.quickfox.core;

import org.campooo.quickfox.stanza.Stanza;

public interface StanzaFilter {

	public boolean accept(Stanza stanza);

}
