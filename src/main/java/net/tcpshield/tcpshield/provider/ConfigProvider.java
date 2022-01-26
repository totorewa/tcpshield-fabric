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
package net.tcpshield.tcpshield.provider;

import net.tcpshield.tcpshield.util.exception.config.ConfigLoadException;
import net.tcpshield.tcpshield.util.exception.config.ConfigReloadException;
import net.tcpshield.tcpshield.util.exception.config.ConfigResetException;
import net.tcpshield.tcpshield.util.exception.phase.ConfigException;

import java.io.File;

/**
 * An abstract provider for TCPShield's options.
 */
public abstract class ConfigProvider {

	/*
	 * Configuration options
	 */
	protected boolean onlyProxy = true;
	protected String timestampValidationMode = "htpdate";
	protected boolean doDebug = true; // Fail-safe default set to true

	protected File dataFolder;
	protected File configFile;

	public boolean isOnlyProxy() {
		return onlyProxy;
	}

	public String getTimestampValidationMode() {
		return this.timestampValidationMode;
	}

	public boolean doDebug() {
		return doDebug;
	}

	public File getDataFolder() {
		return dataFolder;
	}

	public File getConfigFile() {
		return configFile;
	}

	/*
	 * Plugin Constants
	 */
	protected final long maxTimestampDifference = 3; // In Unix Timesteps (Seconds)


	public long getMaxTimestampDifference() {
		return maxTimestampDifference;
	}

	/*
	 * Required methods
	 */

	/**
	 * Deletes the current config saved to the disk and reinstalls the default config
	 * @throws ConfigResetException Thrown if resetting fails
	 */
	protected abstract void reset() throws ConfigResetException;

	/**
	 * Trys to load the options from the config, if failed, throws ConfigLoadException
	 * @throws ConfigLoadException Thrown if loading fails, reset should be called if thrown
	 */
	protected abstract void load() throws ConfigLoadException;

	/**
	 * Trys to reload the config, if failed, throws ConfigReloadException
	 * @throws ConfigReloadException Thrown if reloading fails
	 */
	public abstract void reload() throws ConfigReloadException;

	/**
	 * Checks the provided nodes to see if they exist in the config
	 * @param nodes The nodes to check
	 * @throws ConfigException Thrown when a node isnt found
	 */
	protected abstract void checkNodes(String... nodes) throws ConfigException;

}
