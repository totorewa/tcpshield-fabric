package github.totorewa.tcpshield.fabric.mixin.listener;

import github.totorewa.tcpshield.fabric.api.network.ConnectionDetails;
import github.totorewa.tcpshield.fabric.api.network.HandshakePacket;
import github.totorewa.tcpshield.fabric.event.Handshake;
import github.totorewa.tcpshield.fabric.event.HandshakeContext;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.server.network.ServerHandshakePacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetSocketAddress;

@Mixin(ServerHandshakePacketListenerImpl.class)
public class ServerHandshakePacketListenerImplMixin {
    @Shadow
    @Final
    private Connection connection;

    @Inject(method = "handleIntention", at = @At("HEAD"), cancellable = true)
    private void tcpShield$handleIntention(ClientIntentionPacket packet, CallbackInfo ci) {
        if (packet.getIntention() != ConnectionProtocol.LOGIN) return;
        HandshakeContext context = new HandshakeContext((HandshakePacket) packet, tcpShield$createConnectionDetails());
        Handshake.EVENT.invoker().handle(context);
        if (context.isRejected()) {
            this.connection.setProtocol(ConnectionProtocol.LOGIN);
            TranslatableComponent component = new TranslatableComponent("multiplayer.status.cannot_connect");
            this.connection.send(new ClientboundLoginDisconnectPacket(component));
            this.connection.disconnect(component);
            ci.cancel();
        }
    }

    private ConnectionDetails tcpShield$createConnectionDetails() {
        InetSocketAddress address = (InetSocketAddress) this.connection.getRemoteAddress();
        return new ConnectionDetails(address.getAddress().getHostAddress());
    }
}
