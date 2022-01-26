package github.totorewa.tcpshield.fabric.impl.network;

import github.totorewa.tcpshield.fabric.api.network.HandshakePacket;
import github.totorewa.tcpshield.fabric.mixin.packet.ClientIntentionPacketAccessor;

public class HandshakePacketAdapter implements HandshakePacket {
    private final ClientIntentionPacketAccessor accessor;

    public HandshakePacketAdapter(ClientIntentionPacketAccessor accessor) {
        this.accessor = accessor;
    }

    @Override
    public String getHostName() {
        return this.accessor.getHostName();
    }

    @Override
    public void setHostName(String hostname) {
    }
}
