package org.bundolo;

import org.junit.Assert;
import org.junit.Test;

public class MailingUtilsTest {

    @Test
    public void shouldFormat() {
	Assert.assertEquals("Parameters not inserted correctly.", "Hello, world.",
		MailingUtils.format("Hello, {0}.", "world"));
    }

}
