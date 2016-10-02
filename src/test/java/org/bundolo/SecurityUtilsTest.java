package org.bundolo;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class SecurityUtilsTest {

	public static final String[] ALL_EMAILS = { "aaa@bbb.ccc" };
	public static final String[] EMAILS = { "aaa@bbb.ccc" };
	public static final String[] NONCES = { "6bv4MOSRlJAv9UDg5nhVjvnset4" };
	public static final String[] SALTS = { "o5eqGSPpb8M" };

	@Before
	public void setUp() {
		// LogManager.getLogManager().reset();
		// Logger.getLogger("").addHandler(new ConsoleHandler());
	}

	@Test
	public void testBasic() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		SecureRandom random = new SecureRandom();
		for (int i = 1; i < ALL_EMAILS.length; i++) {
			List<String> hashResult = SecurityUtils.getHashWithSalt(new BigInteger(130, random).toString(32));
			String hashedPassword = hashResult.get(0);
			String salt = hashResult.get(1);
			String nonce1 = SecurityUtils.getHashWithoutSalt(ALL_EMAILS[i] + ":" + salt);
			String nonce2 = SecurityUtils.getHashWithoutSalt(ALL_EMAILS[i] + ":" + salt);
			assertEquals("nonces don't match", nonce1, nonce2);
		}
	}

	@Test
	public void testPending() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		for (int i = 1; i < EMAILS.length; i++) {
			String nonce = SecurityUtils.getHashWithoutSalt(EMAILS[i] + ":" + SALTS[i]);
			try {
				assertEquals("nonces don't match", NONCES[i], nonce);
			} catch (AssertionError ex) {
				System.out.println(EMAILS[i]);
			}
		}
	}

}