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

import net.tcpshield.tcpshield.TCPShieldPlugin;
import net.tcpshield.tcpshield.util.exception.phase.CIDRException;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.util.*;

/**
 * A CIDR validator for TCPShield
 */
public class CIDRValidator {

	private final TCPShieldPlugin plugin;

	private final File ipWhitelistFolder;

	private final List<CIDRMatcher> cidrMatchers;
	private final Set<String> cache = new HashSet<>(); // Max potential size is equilivent to the amount of whitelisted IP's

	public CIDRValidator(TCPShieldPlugin plugin) throws CIDRException {
		this.plugin = plugin;

		ipWhitelistFolder = new File(plugin.getConfigProvider().getDataFolder(), "ip-whitelist");
		if (!ipWhitelistFolder.exists())
			ipWhitelistFolder.mkdir();

		try {
			List<String> whitelists = loadWhitelists();
			cidrMatchers = loadCIDRMatchers(whitelists);
		} catch (Exception e) {
			throw new CIDRException(e);
		}
	}


	private List<CIDRMatcher> loadCIDRMatchers(List<String> whitelists) {
		List<CIDRMatcher> matchers = new ArrayList<>();

		for (String whitelist : whitelists)
			try {
				matchers.add(CIDRMatcher.create(whitelist));
			} catch (Exception e) {
				plugin.getDebugger().warn("Exception occured while creating CIDRMatcher for \"%s\". Ignoring it.", whitelist);
				plugin.getDebugger().exception(e);
			}

		return matchers;
	}

	private List<String> loadWhitelists() throws FileNotFoundException {
		List<String> whitelists = new ArrayList<>();

		for (File file : Objects.requireNonNull(ipWhitelistFolder.listFiles())) {
			if (file.isDirectory())
				continue;

			try (Scanner scanner = new Scanner(file)) {
				while (scanner.hasNextLine()) {
					String cidrEntry = scanner.nextLine();
					whitelists.add(cidrEntry);
				}
			}
		}

		return whitelists;
	}

	/**
	 * Validates an InetAddress with CIDR matchers
	 * @param inetAddress The InetAddress to validate
	 * @return Boolean stating if the InetAddress is validated with CIDR
	 */
	public boolean validate(InetAddress inetAddress) {
		String ip = inetAddress.getHostAddress();

		if (cache.contains(ip))
			return true;

		for (CIDRMatcher cidrMatcher : cidrMatchers)
			if (cidrMatcher.match(inetAddress)) {
				cache.add(ip);
				return true;
			}

		return false;
	}

}
