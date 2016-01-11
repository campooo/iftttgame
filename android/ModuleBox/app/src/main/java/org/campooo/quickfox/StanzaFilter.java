package org.campooo.quickfox;

import org.campooo.quickfox.stanza.Stanza;

public interface StanzaFilter {

	public boolean accept(Stanza stanza);

}
