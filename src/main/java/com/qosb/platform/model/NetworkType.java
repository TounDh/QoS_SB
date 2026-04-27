package com.qosb.platform.model;

public enum NetworkType {
    NETWORK_2G("2G"),
    NETWORK_3G("3G"),
    NETWORK_4G("4G"),
    NETWORK_5G("5G");

    private final String label;

    NetworkType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
