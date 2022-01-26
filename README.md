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

## Disclaimer
This is a third-party mod **not** in affiliation with TCPShield and you should **not**
expect TCPShield to offer support for any problems you may encounter relating to the use of this mod.

## Getting started

You can either clone this repository and build the mod yourself, or you can download
a prebuilt jar from https://github.com/totorewa/tcpshield-fabric/tags

### Compatibility

This mod requires the [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api) 
and the [Fabric loader](https://fabricmc.net/). **Geyser is not supported.**

For compatibility with CraftBukkit, Spigot, Paper, BungeeCord and Velocity,
visit the [original plugin](https://github.com/TCPShield/RealIP).

### Configuring the mod

The mod can be configured with the config file generated at `config/tcpshield.yml`.  
The configuration is mostly identical to [the configuration](https://docs.tcpshield.com/panel/tcpshield-plugin) offered by the TCPShield plugin,
however, Geyser support is removed.

The config file has the following options:
```yaml
# Only allow players to connect through TCPShield and not to the server directly
only-allow-proxy-connections: true

# Validates the timestamp sent to prevent replay attacks. 
# Available modes: system (uses the system time), htpdate (uses a synchronized date) & off (deactivates timestamp validation)
timestamp-validation: 'htpdate'

# Turn on to diagnose connection issues
debug-mode: false
```
