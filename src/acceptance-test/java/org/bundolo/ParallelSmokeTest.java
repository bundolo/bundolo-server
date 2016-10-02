package org.bundolo;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class ParallelSmokeTest {

	static {
		System.setProperty("webdriver.chrome.driver", "D:/projects/bundolo/chromedriver.exe");
	}

	@Test
	public void parallelSmokeTest() throws Exception {
		MyRunnable myRunnable = new MyRunnable();
		Thread[] threads = { new Thread(myRunnable), new Thread(myRunnable), new Thread(myRunnable) };
		for (Thread t : threads) {
			t.start();
		}
		for (Thread t : threads) {
			t.join();
		}
	}

	public class MyRunnable implements Runnable {

		@Override
		public void run() {
			try {
				ChromeDriver driver = new ChromeDriver();
				driver.get("http://localhost");
				System.out.println("driver.getCurrentUrl(): " + driver.getCurrentUrl());
				Assert.assertEquals("Page title not as expected.", "bundolo", driver.getTitle());

				WebElement element = driver.findElement(By.cssSelector(".sidebar .texts_button"));
				element.click();
				Thread.sleep(500);
				element = driver.findElement(By.cssSelector(".slider .texts_button>a"));
				element.click();
				Thread.sleep(500);

				element = driver.findElement(By.id("login_username"));
				element.sendKeys("aaa");

				element = driver.findElement(By.id("login_password"));
				element.sendKeys("aaa");

				element = driver.findElement(By.cssSelector("[title='prijava']"));
				element.click();
				Thread.sleep(1000);
				element = driver.findElement(By.cssSelector(".username_bar>div"));
				Assert.assertEquals("username not as expected.", "aaa", element.getText());

				element = driver.findElement(By.id("add_menu"));
				element.click();
				element = driver.findElement(By.cssSelector("[aria-labelledby='add_menu']>li>a"));
				element.click();
				Thread.sleep(500);
				element = driver.findElement(By.id("edit_title"));
				String textTitle = "gelender" + (int) (Math.random() * 50000 + 1);
				element.sendKeys(textTitle);

				element = driver.findElement(By.id("edit_description"));
				element.sendKeys("fender" + (int) (Math.random() * 50000 + 1));
				element = driver.findElement(By.className("note-editable"));
				element.sendKeys("bender" + (int) (Math.random() * 50000 + 1));
				element = driver.findElement(By.cssSelector("[title='snimi']"));
				element.click();

				Thread.sleep(1000);
				element = driver.findElement(By.cssSelector(".content h3:nth-of-type(2)"));
				Assert.assertEquals("text not properly saved", textTitle, element.getText());
				driver.quit();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}