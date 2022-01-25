package github.totorewa.tcpshield.fabric;

import github.totorewa.tcpshield.fabric.config.ConfigManager;
import github.totorewa.tcpshield.fabric.event.Handshake;
import github.totorewa.tcpshield.fabric.tcpshield.handler.FabricHandshakeHandler;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.tcpshield.tcpshield.TCPShieldPacketHandler;
import net.tcpshield.tcpshield.TCPShieldPlugin;
import net.tcpshield.tcpshield.provider.ConfigProvider;
import net.tcpshield.tcpshield.util.Debugger;
import net.tcpshield.tcpshield.util.exception.phase.InitializationException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class TcpShieldMod implements DedicatedServerModInitializer, TCPShieldPlugin {
    private static final Logger LOGGER;
    private ConfigProvider configProvider;
    private TCPShieldPacketHandler packetHandler;
    private Debugger debugger;

    @Override
    public void onInitializeServer() {
        try {
            this.configProvider = new ConfigManager(Paths.get("./config"));
            this.configProvider.reload();
            this.debugger = Debugger.createDebugger(this);
            this.packetHandler = new TCPShieldPacketHandler(this);
        } catch (Exception e) {
            throw new InitializationException(e);
        }

        Handshake.EVENT.register(new FabricHandshakeHandler(this));
    }

    @Override
    public ConfigProvider getConfigProvider() {
        return this.configProvider;
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public TCPShieldPacketHandler getPacketHandler() {
        return this.packetHandler;
    }

    @Override
    public Debugger getDebugger() {
        return this.debugger;
    }

    static {
        LOGGER = new Logger(TcpShieldMod.class.getCanonicalName(), null) {
            @Override
            public void log(LogRecord record) {
                record.setMessage("[TCPShield] " + record.getMessage());
                super.log(record);
            }
        };
    }
}
