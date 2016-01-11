package org.campooo.app.info.network;

/**
 * ckb on 15/12/1.
 */
public enum NetworkType {

    NONE("None", false),
    MOBILE("Mobile", true),
    WIFI("Wifi", true),
    ETHERNET("Ethernet", true),
    OTHER("Other", true);


    private String name;
    private boolean available;

    NetworkType(String name, boolean available) {
        setName(name);
        setAvailable(available);
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    boolean isAvailable() {
        return available;
    }

    void setAvailable(boolean available) {
        this.available = available;
    }
}
