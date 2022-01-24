package github.totorewa.tcpshield.fabric.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface Handshake {
    Event<Handshake> EVENT = EventFactory.createArrayBacked(Handshake.class,
            listeners -> packet -> {
                for (Handshake listener : listeners) {
                    listener.handle(packet);
                }
            });

    void handle(HandshakeContext context);
}
