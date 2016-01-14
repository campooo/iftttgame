package org.campooo.quickfox.stanza;

import com.google.gson.Gson;

import org.campooo.quickfox.log.Logger;
import org.campooo.quickfox.log.QLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class StanzaParser {

    public static final Logger Log = QLog.getLogger(StanzaParser.class);

    private BufferedReader reader;

    private static final Gson GSON = new Gson();

    public StanzaParser(BufferedReader reader) {

        setReader(reader);

    }

    public Stanza parseStanza() {
        Stanza stanza = null;
        try {
            String line;
            while ((line = reader.readLine()) == null) {
                //no-op
            }
            stanza = parseStanza(line);
            Log.debug(stanza.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stanza;
    }

    private Stanza parseStanza(String src) {
        return GSON.fromJson(src, RawTextStanza.class);
    }

    public void setReader(BufferedReader reader) {
        this.reader = reader;
    }
}
