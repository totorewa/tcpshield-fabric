package github.totorewa.tcpshield.fabric.mixin;

import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.net.SocketAddress;

@Mixin(Connection.class)
public interface ClientConnectionAccessor {
    @Accessor
    void setAddress(SocketAddress socketAddress);
}
