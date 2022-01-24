package github.totorewa.tcpshield.fabric.tcpshield.provider;

import github.totorewa.tcpshield.fabric.api.network.HandshakePacket;
import net.tcpshield.tcpshield.provider.PacketProvider;
import net.tcpshield.tcpshield.util.exception.manipulate.PacketManipulationException;

public class FabricPacketAdapter implements PacketProvider {
    private final HandshakePacket packet;

    public FabricPacketAdapter(HandshakePacket packet) {
        this.packet = packet;
    }

    @Override
    public String getPayloadString() {
        return this.packet.tcpShield$getHostName();
    }

    @Override
    public void setPacketHostname(String hostname) throws PacketManipulationException {
        this.packet.tcpShield$setHostName(hostname);
    }
}
