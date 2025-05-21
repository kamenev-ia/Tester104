package org.openmuc.j60870.gui.utilities;

import org.openmuc.j60870.gui.customUI.DifferentDisplayComboBox;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

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

    public List<DifferentDisplayComboBox.Item> getInterfacesIpItemsForComboBox() {
        List<DifferentDisplayComboBox.Item> activeNetInterfacesList = new ArrayList<>();
        String ipAddress = "";
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isUp() && !networkInterface.isLoopback()) {
                    Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress inetAddress = addresses.nextElement();
                        if (inetAddress instanceof Inet4Address) {
                            ipAddress = inetAddress.getHostAddress();
                        }
                    }
                    DifferentDisplayComboBox.Item item = new DifferentDisplayComboBox.Item(networkInterface.getName()
                            + " (" + networkInterface.getDisplayName() + ")", ipAddress);
                    activeNetInterfacesList.add(item);
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return activeNetInterfacesList;
    }
}
