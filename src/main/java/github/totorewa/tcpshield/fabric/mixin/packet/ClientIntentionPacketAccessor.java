package github.totorewa.tcpshield.fabric.mixin.packet;

import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientIntentionPacket.class)
public interface ClientIntentionPacketAccessor {
    @Accessor
    String getHostName();
}
