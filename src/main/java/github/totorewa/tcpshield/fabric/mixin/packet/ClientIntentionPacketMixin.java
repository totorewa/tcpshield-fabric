package github.totorewa.tcpshield.fabric.mixin.packet;

import github.totorewa.tcpshield.fabric.api.network.HandshakePacket;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientIntentionPacket.class)
public class ClientIntentionPacketMixin implements HandshakePacket {
    @Shadow
    private String hostName;

    @Override
    public String tcpShield$getHostName() {
        return this.hostName;
    }

    @Override
    public void tcpShield$setHostName(String hostname) {
        this.hostName = hostname;
    }
}
