package github.totorewa.tcpshield.fabric.api.network;

public interface HandshakePacket {
    String getHostName();
    void setHostName(String hostname);
}
