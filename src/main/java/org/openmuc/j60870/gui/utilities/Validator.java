package org.openmuc.j60870.gui.utilities;

public class Validator {
    private static final String IP_PATTERN = "^{0}$|^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
    private static final String PORT_PATTERN = "^\\d{0,5}$";

    public boolean validateIpAddress(String ip) {
        return ip.matches(IP_PATTERN);
    }

    public boolean validatePort(String port) {
        return (port.matches(PORT_PATTERN) & !(Integer.parseInt(port) < 1 || Integer.parseInt(port) > 65535));
    }
}
