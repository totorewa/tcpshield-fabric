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
package net.tcpshield.tcpshield.util.validation.timestamp;

import net.tcpshield.tcpshield.TCPShieldPlugin;

/**
 * Base for timestamp validators
 */
public abstract class TimestampValidator {

	/**
	 * Creates an empty validator that always returns true
	 * @return An empty validator always returning true
	 */
	public static TimestampValidator createEmpty(TCPShieldPlugin plugin) {
		return new TimestampValidator(plugin) {
			@Override
			public boolean validate(long timestamp) {
				return true;
			}
		};
	}

	/**
	 * Creates a system validator that uses default validation
	 * @return A system validator
	 */
	public static TimestampValidator createDefault(TCPShieldPlugin plugin) {
		return new TimestampValidator(plugin) {
			// Uses default methods
		};
	}


	protected final TCPShieldPlugin plugin;

	public TimestampValidator(TCPShieldPlugin plugin) {
		this.plugin = plugin;
	}


	/**
	 * Validates given timestamp to see if it's within a reasonable
	 * time range
	 * @param timestamp The timestamp
	 * @return Boolean stating if its valid
	 */
	public boolean validate(long timestamp) {
		return Math.abs(getUnixTime() - timestamp) <= plugin.getConfigProvider().getMaxTimestampDifference();
	}


	/**
	 * Returns the validator's current Unix time
	 * @return The current time in Unix format
	 * @default Returns the system time in Unix format
	 */
	public long getUnixTime() {
		return System.currentTimeMillis() / 1000;
	}

}
