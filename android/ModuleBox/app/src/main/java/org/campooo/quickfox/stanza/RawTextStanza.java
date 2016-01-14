package org.campooo.quickfox.stanza;


public class RawTextStanza extends Stanza {

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
