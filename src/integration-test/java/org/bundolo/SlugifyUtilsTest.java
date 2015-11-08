package org.bundolo;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext.xml")
@ImportResource("/applicationContext.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class SlugifyUtilsTest {

    @Autowired
    private SlugifyUtils slugifyUtils;

    @SuppressWarnings("serial")
    @Before
    public void setUp() {
	slugifyUtils.setCustomReplacements(new HashMap<String, String>() {
	    {
		put("leet", "1337");
	    }
	});
    }

    @Test
    public void testBasic() {
	String s = "Hello world";
	assertEquals("hello-world", slugifyUtils.slugify(s));
    }

    @Test
    public void testSpaces() {
	String s = "\tHello  \t world ";
	assertEquals("hello-world", slugifyUtils.slugify(s));
    }

    @Test
    public void testPrintableASCII() {
	String s = " !\"#$%&'()*+,-./0123456789:;<=>?@" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`"
		+ "abcdefghijklmnopqrstuvwxyz{|}~";

	String expected = "+-0123456789-" + "abcdefghijklmnopqrstuvwxyz-_-" + "abcdefghijklmnopqrstuvwxyz";

	assertEquals(expected, slugifyUtils.slugify(s));
    }

    @Test
    public void testExtendedASCII() {
	String s = "€‚ƒ„…†‡ˆ‰Š‹ŒŽ‘”•–—˜™š›œžŸ¡¢£¤¥¦§¨©ª«¬®¯°±²³´µ¶"
		+ "·¸¹º»¼½¾¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæç" + "èéêëìíîïðñòóôõö÷øùúûüýþÿ";

	String expected = "szszyaaaaaeaceeeeiiiinoooooeuuuueyssaaaaaeaceeeeiiiinoooooeuuuueyy";

	assertEquals(expected, slugifyUtils.slugify(s));
    }

    @Test
    public void testReplacements() {
	slugifyUtils.setLowerCase(false);
	String s = "ÄÖÜäöüß";
	assertEquals("AeOeUeaeoeuess", slugifyUtils.slugify(s));
	slugifyUtils.setLowerCase(true);
    }

    @Test
    public void testCustomReplacements() {
	slugifyUtils.setLowerCase(false);
	String s = "Hello leet!";
	assertEquals("Hello-1337", slugifyUtils.slugify(s));
	slugifyUtils.setLowerCase(true);
    }

    @Test
    public void testCyrillic() {
	String s = "Околостомачни панталодржач";
	assertEquals("okolostomacni-pantalodrzac", slugifyUtils.slugify(s));
    }
}
