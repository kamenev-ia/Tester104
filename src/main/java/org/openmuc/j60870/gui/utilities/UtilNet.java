package org.openmuc.j60870.gui.utilities;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;

public class UtilNet {
    public void pingIp(String ip) {
        try {
            Runtime.getRuntime().exec("cmd.exe /c start ping " + ip + " -t");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void changeLocalIp(String newIp) {
        try {
            String interfaceName = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getName();
            Runtime.getRuntime().exec("cmd.exe /c start netsh interface ipv4 set address name=\"" + interfaceName + "\" static " + newIp + " 255.255.255.0");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
