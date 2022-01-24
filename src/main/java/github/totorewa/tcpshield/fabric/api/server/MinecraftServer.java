package github.totorewa.tcpshield.fabric.api.server;

import java.util.logging.Logger;

public class MinecraftServer {
    private final Logger logger;

    public MinecraftServer(Logger logger) {
        this.logger = logger;
    }

    public Logger getLogger() {
        return logger;
    }
}
