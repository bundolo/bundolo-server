package org.bundolo;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class SmokeTest extends TestBase {

    @Test
    public void smokeTest() throws Exception {
	getDriver().get(TestConstants.TESTING_ROOT);
	System.out.println("driver.getCurrentUrl(): " + getDriver().getCurrentUrl());
	Assert.assertEquals("Page title not as expected.", "bundolo", getDriver().getTitle());

	WebElement element = getDriver().findElement(By.cssSelector(".sidebar .texts_button"));
	element.click();
	Thread.sleep(500);
	element = getDriver().findElement(By.cssSelector(".slider .texts_button>a"));
	element.click();
	Thread.sleep(500);

	element = getDriver().findElement(By.id("login_username"));
	element.sendKeys("aaa");

	element = getDriver().findElement(By.id("login_password"));
	element.sendKeys("aaa");

	element = getDriver().findElement(By.cssSelector("[title='prijava']"));
	element.click();
	Thread.sleep(1000);
	element = getDriver().findElement(By.cssSelector(".username_bar>div"));
	Assert.assertEquals("username not as expected.", "aaa", element.getText());

	element = getDriver().findElement(By.id("add_menu"));
	element.click();
	element = getDriver().findElement(By.cssSelector("[aria-labelledby='add_menu']>li>a"));
	element.click();
	Thread.sleep(500);
	element = getDriver().findElement(By.id("edit_title"));
	String textTitle = TestConstants.TEXT_TITLE_PREFIX + (int) (Math.random() * 50000 + 1);
	element.sendKeys(textTitle);

	element = getDriver().findElement(By.id("edit_description"));
	element.sendKeys("fender" + (int) (Math.random() * 50000 + 1));
	element = getDriver().findElement(By.className("note-editable"));
	element.sendKeys("bender" + (int) (Math.random() * 50000 + 1));
	element = getDriver().findElement(By.cssSelector("[title='snimi']"));
	element.click();

	Thread.sleep(1000);
	element = getDriver().findElement(By.cssSelector(".content h3:nth-of-type(2)"));
	Assert.assertEquals("text not properly saved", textTitle, element.getText());
    }
}