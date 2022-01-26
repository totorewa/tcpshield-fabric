_This mod is experimental and should be used at your own risk. 
I have tested the mod to see that it parses and validates the TCPShield payload 
and replaces the IP address with the "real" IP address provided by TCPShield. 
Beyond this, the effects are unknown._

# TCPShield for Fabric
This is a fork of the [TCPShield plugin](https://github.com/TCPShield/RealIP) for the same named DDoS mitigation service 
[TCPShield](https://tcpshield.com).

The plugin is responsible for validating clients join via the TCPShield network.
It also parses passed IP addresses so the server is aware of the real player IP address.

This mod aims to port the plugin to the Fabric loader to support Fabric servers.

### Compatibility

This mod requires the [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api) 
and the [Fabric loader](https://fabricmc.net/).

For compatibility with CraftBukkit, Spigot, Paper, BungeeCord and Velocity,
visit the [original plugin](https://github.com/TCPShield/RealIP).
