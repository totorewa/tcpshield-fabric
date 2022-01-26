package github.totorewa.tcpshield.fabric.impl.tcpshield.handler;

import github.totorewa.tcpshield.fabric.event.Handshake;
import github.totorewa.tcpshield.fabric.event.HandshakeContext;
import github.totorewa.tcpshield.fabric.impl.tcpshield.provider.FabricPacketAdapter;
import github.totorewa.tcpshield.fabric.impl.tcpshield.provider.FabricPlayerAdapter;
import net.tcpshield.tcpshield.TCPShieldPlugin;
import net.tcpshield.tcpshield.util.exception.phase.HandshakeException;

public class FabricHandshakeHandler implements Handshake {
    private final TCPShieldPlugin mod;

    public FabricHandshakeHandler(TCPShieldPlugin mod) {
        this.mod = mod;
    }

    @Override
    public void handle(HandshakeContext context) {
        FabricPacketAdapter packet = new FabricPacketAdapter(context.getPacket());
        FabricPlayerAdapter player = new FabricPlayerAdapter(context);

        try {
            this.mod.getPacketHandler().handleHandshake(packet, player);
        } catch (HandshakeException e) {
            this.mod.getDebugger().exception(e);
        }
    }
}
