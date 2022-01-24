package github.totorewa.tcpshield.fabric.api.network;

public class ConnectionDetails {
    private final String name;
    private final String hostName;

    public ConnectionDetails(String name, String hostName) {

        this.name = name;
        this.hostName = hostName;
    }

    public ConnectionDetails(String hostName) {
        this("unknown", hostName);
    }

    public String getName() {
        return name;
    }

    public String getHostName() {
        return hostName;
    }
}
