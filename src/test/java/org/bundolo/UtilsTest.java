package org.bundolo;

import org.junit.Assert;
import org.junit.Test;

public class UtilsTest {

    @Test
    public void shouldFormat() {
	Assert.assertEquals("Parameters not inserted correctly.", "Hello, world.", Utils.format("Hello, {0}.", "world"));
    }

}
