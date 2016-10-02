package org.bundolo;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class TestBase {

	protected ThreadLocal<WebDriver> threadDriver = null;

	static {
		System.out.println("setProperty: webdriver.chrome.driver: " + TestConstants.CHROME_DRIVER_LOCATION);
		System.setProperty("webdriver.chrome.driver", TestConstants.CHROME_DRIVER_LOCATION);
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