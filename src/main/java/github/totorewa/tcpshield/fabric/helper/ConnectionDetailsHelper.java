package github.totorewa.tcpshield.fabric.helper;

import github.totorewa.tcpshield.fabric.api.network.ConnectionDetails;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class ConnectionDetailsHelper {
    public static ConnectionDetails fromSocketAddress(SocketAddress address) {
        if (address instanceof InetSocketAddress) {
            InetSocketAddress socketAddress = (InetSocketAddress) address;
            return new ConnectionDetails(socketAddress.getHostString(), socketAddress.getPort());
        }

        return new ConnectionDetails(InetAddress.getLoopbackAddress().getHostAddress(), 0);
    }
}
