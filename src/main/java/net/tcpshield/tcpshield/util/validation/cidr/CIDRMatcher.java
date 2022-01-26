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
package net.tcpshield.tcpshield.util.validation.cidr;

import net.tcpshield.tcpshield.util.exception.phase.CIDRException;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * CIDR matcher for the CIDR validator
 */
public abstract class CIDRMatcher {

	/**
	 * Creates a CIDRMatcher from a CIDR matching regex
	 * @param cidrMatchString The string containing the CIDR matching regex
	 * @return The corrosponding CIDRMatcher
	 */
	public static CIDRMatcher create(String cidrMatchString) {
		return new CIDRMatcher(cidrMatchString) {
			@Override
			public boolean match(InetAddress inetAddress) {
				return super.match(inetAddress);
			}
		};
	}


	private final int maskBits;
	private final int maskBytes;
	private final boolean simpleCIDR;
	private final InetAddress cidrAddress;

	private CIDRMatcher(String cidrMatchString) {
		String[] split = cidrMatchString.split("/");

		String parsedIPAddress;
		if (split.length != 0) {
			parsedIPAddress = split[0];

			this.maskBits = Integer.parseInt(split[1]);
			this.simpleCIDR = maskBits == 32;
		} else {
			parsedIPAddress = cidrMatchString;

			this.maskBits = -1;
			this.simpleCIDR = true;
		}

		this.maskBytes = simpleCIDR ? -1 : maskBits / 8;

		try {
			cidrAddress = InetAddress.getByName(parsedIPAddress);
		} catch (UnknownHostException e) {
			throw new CIDRException(e);
		}
	}

	/**
	 * Compares the provided InetAddress with the CIDR for a match
	 * @param inetAddress The InetAddress to match with the CIDR
	 * @return Boolean stating if the provided InetAddress matches the CIDR
	 */
	public boolean match(InetAddress inetAddress) {
		if (!cidrAddress.getClass().equals(inetAddress.getClass())) return false; // check if IP is IPv4 or IPv6

		if (simpleCIDR) return inetAddress.equals(cidrAddress); // check for equality if it's a simple CIDR

		byte[] inetAddressBytes = inetAddress.getAddress();
		byte[] requiredAddressBytes = cidrAddress.getAddress();

		byte finalByte = (byte) (0xFF00 >> (maskBits & 0x07));

		for (int i = 0; i < maskBytes; i++) {
			if (inetAddressBytes[i] != requiredAddressBytes[i]) return false;
		}

		if (finalByte != 0)
			return (inetAddressBytes[maskBytes] & finalByte) == (requiredAddressBytes[maskBytes] & finalByte);

		return true;
	}

}
