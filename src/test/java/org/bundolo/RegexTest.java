package org.bundolo;

import org.junit.Assert;
import org.junit.Test;

public class RegexTest {

    // TODO fix tests
    public static final String[] SHOULD_MATCH_URL = { "kibla", "kib la", "kib lšća Не даш се надћутати" };
    public static final String[] SHOULD_NOT_MATCH_URL = { "ki~bla", "kibla'", ":kibla", "kibl{a", "kibl\\a", "kibl+a",
	    "kibl#a", "kibl^a", "kibl*a", "kibl\\/a", "kibl[]a", "kibl''a" };
    public static final String[] SHOULD_MATCH_USERNAME = { "kibla", "kib la" };
    public static final String[] SHOULD_NOT_MATCH_USERNAME = { "ki~bla", "kibla'", ":kibla", "kibl{a", "kibl\\a",
	    "kibl+a", "kibl#a", "kibl^a", "kibl*a", "kibl\\/a", "kibl[]a", "kibl''a", "kibl.a",
	    "kibla Не даш се надћутати" };

    @Test
    public void urlShouldMatch() {
	for (String text : SHOULD_MATCH_URL) {
	    Assert.assertTrue("does not match", text.matches(Constants.URL_SAFE_REGEX));
	}
    }

    @Test
    public void urlShouldNotMatch() {
	for (String text : SHOULD_NOT_MATCH_URL) {
	    Assert.assertFalse("does match", text.matches(Constants.URL_SAFE_REGEX));
	}
    }

    @Test
    public void usernameShouldMatch() {
	for (String text : SHOULD_MATCH_USERNAME) {
	    Assert.assertTrue("does not match", text.matches(Constants.USERNAME_SAFE_REGEX));
	}
    }

    @Test
    public void usernameShouldNotMatch() {
	for (String text : SHOULD_NOT_MATCH_USERNAME) {
	    Assert.assertFalse("does match", text.matches(Constants.USERNAME_SAFE_REGEX));
	}
    }

}