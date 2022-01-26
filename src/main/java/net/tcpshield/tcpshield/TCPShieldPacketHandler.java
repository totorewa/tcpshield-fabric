/*
MIT License

Copyright (c) 2020 TCPShield

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package net.tcpshield.tcpshield;

import net.tcpshield.tcpshield.provider.PacketProvider;
import net.tcpshield.tcpshield.provider.PlayerProvider;
import net.tcpshield.tcpshield.util.exception.parse.InvalidPayloadException;
import net.tcpshield.tcpshield.util.exception.parse.SignatureValidationException;
import net.tcpshield.tcpshield.util.exception.parse.TimestampValidationException;
import net.tcpshield.tcpshield.util.exception.phase.HandshakeException;
import net.tcpshield.tcpshield.util.exception.phase.InvalidSecretException;
import net.tcpshield.tcpshield.util.validation.SignatureValidator;
import net.tcpshield.tcpshield.util.validation.cidr.CIDRValidator;
import net.tcpshield.tcpshield.util.validation.timestamp.TimestampValidator;
import net.tcpshield.tcpshield.util.validation.timestamp.impl.HTPDateTimestampValidator;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

/**
 * Packet handler for TCPShield's plugins
 */
public class TCPShieldPacketHandler {

	private final TCPShieldPlugin plugin;

	private TimestampValidator timestampValidator;
	private SignatureValidator signatureValidator;
	private CIDRValidator cidrValidator;

	/**
	 * Creates an instance of the TCPShieldPacketHandler
	 *
	 * @param plugin The TCPShield plugin
	 */
	public TCPShieldPacketHandler(TCPShieldPlugin plugin) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
		this.plugin = plugin;

		initValidators();
	}

	/**
	 * Initiates the validators
	 *
	 * @throws NoSuchAlgorithmException SignatureValidator exception
	 * @throws IOException              SignatureValidator exception
	 * @throws InvalidKeySpecException  SignatureValidator exception
	 */
	private void initValidators() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
		signatureValidator = new SignatureValidator();

		switch (plugin.getConfigProvider().getTimestampValidationMode().toLowerCase()) {
			case "system": {
				timestampValidator = TimestampValidator.createDefault(plugin);
				break;
			}
			case "htpdate": {
				timestampValidator = new HTPDateTimestampValidator(plugin);
				break;
			}
			case "off": {
				timestampValidator = TimestampValidator.createEmpty(plugin);
				break;
			}
			default: {
				plugin.getDebugger().warn(
						"Unknown timestamp validation mode \"%s\"! Disabling timestamp validation.",
						plugin.getConfigProvider().getTimestampValidationMode());
				timestampValidator = TimestampValidator.createEmpty(plugin);
				break;
			}
		}

		cidrValidator = new CIDRValidator(plugin);
	}

	/**
	 * Handles the packet and player manipulation
	 * for the initial handshake from TCPShield
	 * servers
	 *
	 * @param packet The handshake packet
	 * @param player The involved player
	 */
	public void handleHandshake(PacketProvider packet, PlayerProvider player) throws HandshakeException {
		try {
			InetAddress inetAddress = InetAddress.getByName(player.getIP());

			String extraData = null;
			String[] payload = packet.getPayloadString().split("///");

			if (payload.length != 4)
				if (cidrValidator.validate(inetAddress))
					return; // Allow connection with no processing
				else
					throw new InvalidPayloadException("length: " + payload.length + ", payload: " + Arrays.toString(payload) + ", raw payload: " + packet.getPayloadString());

			int nullIndex;
			if ((nullIndex = payload[3].indexOf('\0')) != -1) { // FML tagged payload
				String originalData = payload[3];
				payload[3] = originalData.substring(0, nullIndex);
				extraData = originalData.substring(nullIndex);
			}

			String hostname = payload[0];
			String ipData = payload[1];
			int timestamp = Integer.parseInt(payload[2]);
			String signature = payload[3];

			String[] ipParts;
			String host;
			int port;

			// Toto start
			/*
			if (timestamp == 0 && GeyserUtils.GEYSER_SUPPORT_ENABLED) {
				// Remap the altered layout
				ipData = payload[0];
				signature = payload[1];
				hostname = payload[3];

				// This is annoying having to have this in both blocks but w/e
				ipParts = ipData.split(":");
				host = ipParts[0];
				port = Integer.parseInt(ipParts[1]);

				if (!signature.equals(GeyserUtils.SESSION_SECRET)) {
					throw new InvalidSecretException("Invalid secret: " + signature);
				}
			} else {*/
				ipParts = ipData.split(":");
				host = ipParts[0];
				port = Integer.parseInt(ipParts[1]);
				String reconstructedPayload = hostname + "///" + host + ":" + port + "///" + timestamp;

				if (!timestampValidator.validate(timestamp))
					throw new TimestampValidationException(timestampValidator, timestamp);


				if (!signatureValidator.validate(reconstructedPayload, signature))
					throw new SignatureValidationException();
			// }
			// Toto end
			InetSocketAddress newIP = new InetSocketAddress(host, port);
			player.setIP(newIP);

			if (extraData != null) hostname = hostname + extraData;


			packet.setPacketHostname(hostname);
		} catch (NumberFormatException | InvalidPayloadException e) {
			plugin.getDebugger().warn(
					"%s[%s/%s] was disconnected because no proxy info was received and only-allow-proxy-connections is enabled. Raw payload = \"%s\"",
					player.getName(), player.getUUID(), player.getIP(), packet.getPayloadString());
			if (plugin.getConfigProvider().isOnlyProxy())
				player.disconnect();

			if (!(e instanceof InvalidPayloadException))
				throw new HandshakeException(new InvalidPayloadException(e));
			else
				throw new HandshakeException(e);
		} catch (TimestampValidationException e) {
			plugin.getDebugger().warn(
					"%s[%s/%s] provided valid handshake information, but timestamp was not valid. " +
							"Provided timestamp: %d vs. system timestamp: %d. Please check your machine time. Timestamp validation mode: %s",
					player.getName(), player.getUUID(), player.getIP(), e.getTimestamp(), e.getTimestampValidator().getUnixTime(), plugin.getConfigProvider().getTimestampValidationMode());
			if (plugin.getConfigProvider().isOnlyProxy())
				player.disconnect();

			// Toto start
			throw new HandshakeException(
					String.format("Provided timestamp: %d vs. system timestamp: %d.",
							e.getTimestamp(),
							e.getTimestampValidator().getUnixTime()),
					e);
			// Toto end
		} catch (SignatureValidationException e) {
			plugin.getDebugger().warn(
					"%s[%s/%s] provided valid handshake information, but signing check failed. Raw payload = \"%s\"",
					player.getName(), player.getUUID(), player.getIP(), packet.getPayloadString());
			if (plugin.getConfigProvider().isOnlyProxy())
				player.disconnect();

			throw new HandshakeException(e);
		} catch (UnknownHostException e) {
			if (plugin.getConfigProvider().isOnlyProxy())
				player.disconnect();

			throw new HandshakeException(e);
		} catch (InvalidSecretException e) {
			plugin.getDebugger().warn(
					"%s[%s/%s] provided an invalid session secret when authenticating a geyser connection.",
					player.getName(), player.getUUID(), player.getIP());
			if (plugin.getConfigProvider().isOnlyProxy())
				player.disconnect();

			throw new HandshakeException(e);
		} catch (Exception e) {
			if (plugin.getConfigProvider().isOnlyProxy())
				player.disconnect();

			if (!(e instanceof HandshakeException))
				throw new HandshakeException(e);
			else
				throw e;
		}
	}

}
