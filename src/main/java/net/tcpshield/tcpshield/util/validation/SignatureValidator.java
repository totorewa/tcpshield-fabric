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
package net.tcpshield.tcpshield.util.validation;

import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * A signature validator using the TCPShield public key
 */
public class SignatureValidator {

	private final PublicKey publicKey;

	public SignatureValidator() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] encodedKey = ByteStreams.toByteArray(SignatureValidator.class.getResourceAsStream("/signing_pub.key"));
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encodedKey);

		KeyFactory keyFactory = KeyFactory.getInstance("EC");
		publicKey = keyFactory.generatePublic(keySpec);
	}


	/**
	 * Validates a String and Signature pair
	 * @param str The data in the form of a string
	 * @param signature The provided signature
	 * @return Boolean stating if it's a valid signature
	 */
	public boolean validate(String str, String signature) {
		return validate(str.getBytes(StandardCharsets.UTF_8), signature);
	}

	/**
	 * Validates a byte[] and Signature pair
	 * @param data The data in the form of a byte array
	 * @param signature The provided signature
	 * @return Boolean stating if it's a valid signature
	 */
	private boolean validate(byte[] data, String signature) {
		try {
			byte[] decodedSignature = Base64.getDecoder().decode(signature);

			Signature sig = Signature.getInstance("SHA512withECDSA");
			sig.initVerify(publicKey);
			sig.update(data);

			return sig.verify(decodedSignature);
		} catch (IllegalArgumentException | SignatureException | NoSuchAlgorithmException | InvalidKeyException e) {
			return false;
		}
	}

}
