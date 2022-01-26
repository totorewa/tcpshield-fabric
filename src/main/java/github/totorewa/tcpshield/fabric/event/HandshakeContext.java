package github.totorewa.tcpshield.fabric.event;

import github.totorewa.tcpshield.fabric.api.network.ConnectionDetails;
import github.totorewa.tcpshield.fabric.api.network.HandshakePacket;
import net.tcpshield.tcpshield.util.exception.manipulate.PlayerManipulationException;

import java.net.InetSocketAddress;
import java.util.UUID;

public class HandshakeContext {
    private final UUID id;
    private final HandshakePacket packet;
    private final ConnectionDetails connectionDetails;
    private String hostAddress;
    private boolean connectionRejected;
    private boolean hostChanged;

    public HandshakeContext(HandshakePacket packet, ConnectionDetails connectionDetails) {
        this.packet = packet;
        this.connectionDetails = connectionDetails;
        this.hostAddress = this.connectionDetails.getHostName();
        id = UUID.randomUUID();
    }

    public boolean isRejected() {
        return connectionRejected;
    }

    public boolean hasHostAddressChanged() {
        return this.hostChanged;
    }

    public UUID getId() {
        return id;
    }

    public String getOriginalHostAddress() {
        return this.connectionDetails.getHostName();
    }

    public int getOriginalPort() {
        return this.connectionDetails.getPort();
    }

    public HandshakePacket getPacket() {
        return packet;
    }

    public String getHostAddress() {
        return this.hostAddress;
    }

    public String getName() {
        return this.connectionDetails.getName();
    }

    public void setHostAddress(InetSocketAddress ip) throws PlayerManipulationException {
        this.hostAddress = ip.getAddress().getHostAddress();
        this.hostChanged = true;
    }

    public void reject() {
        connectionRejected = true;
    }
}
