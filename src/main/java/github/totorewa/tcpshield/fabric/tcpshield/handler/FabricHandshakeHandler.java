package github.totorewa.tcpshield.fabric.tcpshield.handler;

import github.totorewa.tcpshield.fabric.event.Handshake;
import github.totorewa.tcpshield.fabric.event.HandshakeContext;
import github.totorewa.tcpshield.fabric.tcpshield.provider.FabricPacketAdapter;
import github.totorewa.tcpshield.fabric.tcpshield.provider.FabricPlayerAdapter;
import net.tcpshield.tcpshield.TCPShieldPlugin;

public class FabricHandshakeHandler implements Handshake {
    private final TCPShieldPlugin mod;

    public FabricHandshakeHandler(TCPShieldPlugin mod) {
        this.mod = mod;
    }

    @Override
    public void handle(HandshakeContext context) {
        FabricPacketAdapter packet = new FabricPacketAdapter(context.getPacket());
        FabricPlayerAdapter player = new FabricPlayerAdapter(context);

        this.mod.getPacketHandler().handleHandshake(packet, player);
    }
}
