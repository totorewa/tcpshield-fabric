package github.totorewa.tcpshield.fabric.tcpshield.provider;

import github.totorewa.tcpshield.fabric.event.HandshakeContext;
import net.tcpshield.tcpshield.provider.PlayerProvider;
import net.tcpshield.tcpshield.util.exception.manipulate.PlayerManipulationException;

import java.net.InetSocketAddress;

public class FabricPlayerAdapter implements PlayerProvider {
    private final HandshakeContext context;

    public FabricPlayerAdapter(HandshakeContext context) {
        this.context = context;
    }

    @Override
    public String getUUID() {
        return this.context.getId().toString();
    }

    @Override
    public String getName() {
        return this.context.getName();
    }

    @Override
    public String getIP() {
        return this.context.getHostAddress();
    }

    @Override
    public void setIP(InetSocketAddress ip) throws PlayerManipulationException {
        this.context.setHostAddress(ip);
    }

    @Override
    public void disconnect() {
        this.context.reject();
    }
}
