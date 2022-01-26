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
package net.tcpshield.tcpshield.util;

import net.tcpshield.tcpshield.TCPShieldPlugin;

import java.util.logging.Logger;

/**
 * A util for debugging
 */
public abstract class Debugger {

	/**
	 * Creates a debugger instance according to
	 * the plugin's configuration options
	 * @param plugin The TCPShield plugin
	 * @return A debugger instance from plugin's configuration
	 */
	public static Debugger createDebugger(TCPShieldPlugin plugin) {
		if (plugin.getConfigProvider().doDebug())
			/*
			 * A non-empty debugger using the plugin's logger
			 */
			return new Debugger(plugin.getLogger()) {

				@Override
				public void info(String format, Object... formats) {
					this.logger.info("Debug : " + String.format(format, formats));
				}

				@Override
				public void warn(String format, Object... formats) {
					this.logger.warning("Debug : " + String.format(format, formats));
				}

				@Override
				public void error(String format, Object... formats) {
					this.logger.severe("Debug : " + String.format(format, formats));
				}

				@Override
				public void exception(Exception exception) {
					exception.printStackTrace();
				}

			};
		else
			/*
			 * An empty debugger
			 */
			return new Debugger(null) {

				@Override
				public void info(String format, Object... formats) {

				}

				@Override
				public void warn(String format, Object... formats) {

				}

				@Override
				public void error(String format, Object... formats) {

				}

				@Override
				public void exception(Exception exception) {

				}

			};
	}


	protected final Logger logger;

	/**
	 * Non-accessable constructor for creating a debugger
	 * @param logger The plugin's logger
	 */
	private Debugger(Logger logger) {
		this.logger = logger;
	}


	/**
	 * Outputs debug information with log level "INFO"
	 * @param format The output string to be formatted
	 * @param formats The formarts for the string
	 */
	public abstract void info(String format, Object... formats);

	/**
	 * Outputs debug information with log level "WARNING"
	 * @param format The output string to be formatted
	 * @param formats The formarts for the string
	 */
	public abstract void warn(String format, Object... formats);

	/**
	 * Outputs debug information with log level "SEVERE"
	 * @param format The output string to be formatted
	 * @param formats The formarts for the string
	 */
	public abstract void error(String format, Object... formats);

	/**
	 * Outputs the exception through the debugger
	 * @param exception The exception to be outputted
	 */
	public abstract void exception(Exception exception);

}
