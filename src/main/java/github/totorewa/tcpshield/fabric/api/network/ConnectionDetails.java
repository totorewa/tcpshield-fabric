package github.totorewa.tcpshield.fabric.api.network;

public class ConnectionDetails {
    private final String name;
    private final String hostName;
    private final int port;

    public ConnectionDetails(String name, String hostName, int port) {
        this.name = name;
        this.hostName = hostName;
        this.port = port;
    }

    public ConnectionDetails(String hostName, int port) {
        this("unknown", hostName, port);
    }

    public String getName() {
        return name;
    }

    public String getHostName() {
        return hostName;
    }

    public int getPort() {
        return port;
    }
}
