package github.totorewa.tcpshield.fabric.mixin.listener;

import github.totorewa.tcpshield.fabric.api.network.HandshakePacket;
import github.totorewa.tcpshield.fabric.event.Handshake;
import github.totorewa.tcpshield.fabric.event.HandshakeContext;
import github.totorewa.tcpshield.fabric.helper.ConnectionDetailsHelper;
import github.totorewa.tcpshield.fabric.impl.network.HandshakePacketAdapter;
import github.totorewa.tcpshield.fabric.mixin.ClientConnectionAccessor;
import github.totorewa.tcpshield.fabric.mixin.packet.ClientIntentionPacketAccessor;
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
import java.net.SocketAddress;

@Mixin(ServerHandshakePacketListenerImpl.class)
public class ServerHandshakePacketListenerImplMixin {
    @Shadow
    @Final
    private Connection connection;

    @Inject(method = "handleIntention", at = @At("HEAD"), cancellable = true)
    private void tcpShield$handleIntention(ClientIntentionPacket packet, CallbackInfo ci) {
        if (packet.getIntention() != ConnectionProtocol.LOGIN) return;
        SocketAddress originalSocket = this.connection.getRemoteAddress();

        HandshakeContext context = new HandshakeContext(
                new HandshakePacketAdapter((ClientIntentionPacketAccessor) packet),
                ConnectionDetailsHelper.fromSocketAddress(originalSocket));
        Handshake.EVENT.invoker().handle(context);

        if (context.isRejected()) {
            this.connection.setProtocol(ConnectionProtocol.LOGIN);
            TranslatableComponent component = new TranslatableComponent("multiplayer.status.cannot_connect");
            this.connection.send(new ClientboundLoginDisconnectPacket(component));
            this.connection.disconnect(component);
            ci.cancel();
        } else if (context.hasHostAddressChanged()) {
            ((ClientConnectionAccessor)this.connection).setAddress(
                    new InetSocketAddress(context.getHostAddress(), context.getOriginalPort()));
        }
    }
}
