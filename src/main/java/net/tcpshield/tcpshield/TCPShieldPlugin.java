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

import net.tcpshield.tcpshield.provider.ConfigProvider;
import net.tcpshield.tcpshield.util.Debugger;

import java.util.logging.Logger;

/**
 * The base/provider for all entry points/main classes
 */
public interface TCPShieldPlugin {

	/**
	 * Gets the plugin's config provider
	 *
	 * @return The plugin's config provider
	 */
	ConfigProvider getConfigProvider();

	/**
	 * Gets the plugin's logger
	 *
	 * @return The plugin's logger
	 */
	Logger getLogger();

	/**
	 * Gets the packet handler
	 *
	 * @return The packet handler
	 */
	TCPShieldPacketHandler getPacketHandler();

	/**
	 * Gets the plugin's debugger
	 *
	 * @return The plugin's debugger
	 */
	Debugger getDebugger();


	/**
	 * Default initialization of TCPShield, called after interface defaults are set
	 */
	default void initialization() {
		String jvmVersion = System.getProperty("java.version");
		try {
			// Java 11 check/warning (Will eventually force Java 16 without warning)
			String[] versionParts = jvmVersion.split("\\.");
			int baseVersion = Integer.parseInt(versionParts[0]);
			if (baseVersion < 11) // Java 8, and below, starts with 1, but since we are using Java 11 we can ignore sub values
				this.getDebugger().warn("The Java version you are running is outdated for TCPShield and may cause issues. Update to atleast Java 11. Your version: Java %s", jvmVersion);
		} catch (Throwable t) {
			this.getDebugger().error("Failed to check java version for string: " + jvmVersion);
		}
	}

}
