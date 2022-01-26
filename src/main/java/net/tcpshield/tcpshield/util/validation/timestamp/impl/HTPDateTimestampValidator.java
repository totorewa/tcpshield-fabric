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
package net.tcpshield.tcpshield.util.validation.timestamp.impl;

import net.tcpshield.tcpshield.TCPShieldPlugin;
import net.tcpshield.tcpshield.util.exception.phase.InitializationException;
import net.tcpshield.tcpshield.util.validation.timestamp.TimestampValidator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;


/**
 * A timestamp validator that offsets system time with Google's to sync
 * tiemstamps
 */
public class HTPDateTimestampValidator extends TimestampValidator {

	private volatile long htpDateOffset = 0;

	public HTPDateTimestampValidator(TCPShieldPlugin plugin) {
		super(plugin);

		ForkJoinPool.commonPool().execute(() -> {
			try {
				updateHTPDateOffset();
			} catch (Exception e) {
				throw new InitializationException(e);
			}
		});
	}


	private void updateHTPDateOffset() throws IOException {
		Socket socket = new Socket("google.com", 80);

		String payload = "HEAD http://google.com/ HTTP/1.1\r\nHost: google.com\r\nUser-Agent: tcpshield/1.0\r\nPragma: no-cache\r\nCache-Control: no-cache\r\nConnection: close\r\n\r\n";
		socket.getOutputStream().write(payload.getBytes());

		long readTime = System.currentTimeMillis(); // assuming server -> client time is negligible

		List<String> response = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))
				.lines()
				.collect(Collectors.toList());

		Date serverDate = parseDate(response);

		htpDateOffset = Math.round((serverDate.getTime() - readTime) / 1000.0) * 1000; // the HTTP protocol only returns time in seconds; round offset
	}

	private Date parseDate(List<String> response) {
		for (String line : response) {
			if (!line.startsWith("Date: "))
				continue;

			SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
			try {
				return sdf.parse(line.substring("Date: ".length()));
			} catch (ParseException e) {
				throw new IllegalStateException(e);
			}
		}

		throw new IllegalArgumentException("no date line found - response: " + response);
	}

	@Override
	public long getUnixTime() {
		return (System.currentTimeMillis() + htpDateOffset) / 1000;
	}

}
