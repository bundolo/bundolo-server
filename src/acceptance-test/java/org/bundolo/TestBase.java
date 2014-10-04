package org.bundolo;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class TestBase {

    protected ThreadLocal<WebDriver> threadDriver = null;

    static {
	System.setProperty("webdriver.chrome.driver", "D:/projects/bundolo/chromedriver.exe");
    }

    @Before
    public void setUp() {
	threadDriver = new ThreadLocal<WebDriver>();
	threadDriver.set(new ChromeDriver());
    }

    public WebDriver getDriver() {
	return threadDriver.get();
    }

    @After
    public void closeBrowser() {
	getDriver().quit();

    }
}