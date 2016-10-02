package org.bundolo;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

	private final static int ITERATION_NUMBER = 987;

	// this one is really safe
	public static List<String> getHashWithSalt(String textToHash)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		List<String> result = new ArrayList<String>();
		// Uses a secure Random not a simple Random
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		// Salt generation 64 bits long
		byte[] bSalt = new byte[8];
		random.nextBytes(bSalt);
		// Digest computation
		byte[] bDigest = getHash(ITERATION_NUMBER, textToHash, bSalt);
		// String sDigest = byteToBase64(bDigest);
		String sDigest = Base64.encodeBase64URLSafeString(bDigest);
		// String sSalt = byteToBase64(bSalt);
		String sSalt = Base64.encodeBase64URLSafeString(bSalt);
		result.add(sDigest);
		result.add(sSalt);
		return result;
	}

	// not so safe but ok for some uses
	// TODO might improve it later
	public static String getHashWithoutSalt(String textToHash)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		// Digest computation
		byte[] bDigest = getHash(textToHash);
		// return byteToBase64(bDigest);
		return Base64.encodeBase64URLSafeString(bDigest);
	}

	public static String getHashWithPredefinedSalt(String textToHash, String salt)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		byte[] bSalt = Base64.decodeBase64(salt);
		byte[] bDigest = getHash(ITERATION_NUMBER, textToHash, bSalt);
		return Base64.encodeBase64URLSafeString(bDigest);
	}

	/**
	 * From a password, a number of iterations and a salt, returns the
	 * corresponding digest
	 *
	 * @param iterationNb
	 *            int The number of iterations of the algorithm
	 * @param textToHash
	 *            String The text to encrypt
	 * @param salt
	 *            byte[] The salt
	 * @return byte[] The digested text
	 * @throws NoSuchAlgorithmException
	 *             If the algorithm doesn't exist
	 * @throws UnsupportedEncodingException
	 */
	private static byte[] getHash(int iterationNb, String textToHash, byte[] salt)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		digest.reset();
		digest.update(salt);
		byte[] input = digest.digest(textToHash.getBytes("UTF-8"));
		for (int i = 0; i < iterationNb; i++) {
			digest.reset();
			input = digest.digest(input);
		}
		return input;
	}

	/**
	 * From a password, and a number of iterations, returns the corresponding
	 * digest
	 *
	 * @param iterationNb
	 *            int The number of iterations of the algorithm
	 * @param textToHash
	 *            String The text to encrypt
	 * @return byte[] The digested text
	 * @throws NoSuchAlgorithmException
	 *             If the algorithm doesn't exist
	 * @throws UnsupportedEncodingException
	 */
	private static byte[] getHash(String textToHash) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		digest.reset();
		return digest.digest(textToHash.getBytes("UTF-8"));
	}

	public static String getUsername() {
		UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder
				.getContext().getAuthentication();
		if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))) {
			return (String) authentication.getPrincipal();
		} else {
			return null;
		}
	}

	public static String removeBotSuffix(String path) {
		path = StringUtils.removeEnd(path, Constants.BOT_REQUEST_SUFFIX);
		path = StringUtils.removeEnd(path, Constants.BOT_REQUEST_SUFFIX_ESCAPED);
		return path;
	}

}
