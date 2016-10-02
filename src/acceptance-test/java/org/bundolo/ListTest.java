package org.bundolo;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

public class ListTest extends TestBase {

	@Test
	public void textListTest() throws Exception {
		getDriver().get(TestConstants.TESTING_ROOT + "/" + TestConstants.TEXTS);
		System.out.println("driver.getCurrentUrl(): " + getDriver().getCurrentUrl());
		Thread.sleep(500);
		Assert.assertEquals("Texts page title not as expected.", "tekstovi - bundolo", getDriver().getTitle());

		List<WebElement> rows = getDriver().findElement(By.id(TestConstants.TEXTS)).findElement(By.tagName("tbody"))
				.findElements(By.tagName("tr"));
		Assert.assertEquals("Wrong number of texts.", 25, rows.size());

		((JavascriptExecutor) getDriver()).executeScript("$('#" + TestConstants.TEXTS + ">tbody').scrollTop(300)");
		Thread.sleep(500);
		rows = getDriver().findElement(By.id(TestConstants.TEXTS)).findElement(By.tagName("tbody"))
				.findElements(By.tagName("tr"));
		Assert.assertEquals("Wrong number of texts after scrolling.", 35, rows.size());
		String textTitle = rows.get(26).findElements(By.tagName("td")).get(1).findElement(By.tagName("a")).getText()
				+ " - " + rows.get(26).findElements(By.tagName("td")).get(0).findElement(By.tagName("a")).getText();
		rows.get(26).click();
		Thread.sleep(500);
		Assert.assertEquals("Text page title not as expected.", "tekst - " + textTitle + " - bundolo",
				getDriver().getTitle());
	}

	@Test
	public void serialListTest() throws Exception {
		getDriver().get(TestConstants.TESTING_ROOT + "/" + TestConstants.SERIALS);
		System.out.println("driver.getCurrentUrl(): " + getDriver().getCurrentUrl());
		Thread.sleep(500);
		Assert.assertEquals("Serials page title not as expected.", "serije - bundolo", getDriver().getTitle());

		List<WebElement> rows = getDriver().findElement(By.id(TestConstants.SERIALS)).findElement(By.tagName("tbody"))
				.findElements(By.tagName("tr"));
		Assert.assertEquals("Wrong number of serials.", 25, rows.size());

		((JavascriptExecutor) getDriver()).executeScript("$('#" + TestConstants.SERIALS + ">tbody').scrollTop(300)");
		Thread.sleep(500);
		rows = getDriver().findElement(By.id(TestConstants.SERIALS)).findElement(By.tagName("tbody"))
				.findElements(By.tagName("tr"));
		Assert.assertEquals("Wrong number of serials after scrolling.", 35, rows.size());
		String serialTitle = rows.get(34).findElements(By.tagName("td")).get(0).findElement(By.tagName("a")).getText();
		rows.get(34).click();
		Thread.sleep(500);
		Assert.assertEquals("Serial page title not as expected.", "serija - " + serialTitle + " - bundolo",
				getDriver().getTitle());
	}

	@Test
	public void authorListTest() throws Exception {
		getDriver().get(TestConstants.TESTING_ROOT + "/" + TestConstants.AUTHORS);
		System.out.println("driver.getCurrentUrl(): " + getDriver().getCurrentUrl());
		Thread.sleep(500);
		Assert.assertEquals("Authors page title not as expected.", "autori - bundolo", getDriver().getTitle());

		List<WebElement> rows = getDriver().findElement(By.id(TestConstants.AUTHORS)).findElement(By.tagName("tbody"))
				.findElements(By.tagName("tr"));
		Assert.assertEquals("Wrong number of authors.", 25, rows.size());

		((JavascriptExecutor) getDriver()).executeScript("$('#" + TestConstants.AUTHORS + ">tbody').scrollTop(300)");
		Thread.sleep(500);
		rows = getDriver().findElement(By.id(TestConstants.AUTHORS)).findElement(By.tagName("tbody"))
				.findElements(By.tagName("tr"));
		Assert.assertEquals("Wrong number of authors after scrolling.", 35, rows.size());

		((JavascriptExecutor) getDriver()).executeScript("$('#" + TestConstants.AUTHORS + ">tbody').scrollTop(600)");
		Thread.sleep(500);
		rows = getDriver().findElement(By.id(TestConstants.AUTHORS)).findElement(By.tagName("tbody"))
				.findElements(By.tagName("tr"));
		Assert.assertEquals("Wrong number of authors after scrolling.", 45, rows.size());

		String authorTitle = rows.get(43).findElements(By.tagName("td")).get(0).findElement(By.tagName("a")).getText();
		rows.get(43).click();
		Thread.sleep(500);
		Assert.assertEquals("Author page title not as expected.", "autor - " + authorTitle + " - bundolo",
				getDriver().getTitle());
	}

	@Test
	public void announcementListTest() throws Exception {
		getDriver().get(TestConstants.TESTING_ROOT + "/" + TestConstants.ANNOUNCEMENTS);
		System.out.println("driver.getCurrentUrl(): " + getDriver().getCurrentUrl());
		Thread.sleep(500);
		Assert.assertEquals("Announcements page title not as expected.", "vesti - bundolo", getDriver().getTitle());

		List<WebElement> rows = getDriver().findElement(By.id(TestConstants.ANNOUNCEMENTS))
				.findElement(By.tagName("tbody")).findElements(By.tagName("tr"));
		Assert.assertEquals("Wrong number of announcements.", 25, rows.size());

		((JavascriptExecutor) getDriver())
				.executeScript("$('#" + TestConstants.ANNOUNCEMENTS + ">tbody').scrollTop(300)");
		Thread.sleep(500);
		rows = getDriver().findElement(By.id(TestConstants.ANNOUNCEMENTS)).findElement(By.tagName("tbody"))
				.findElements(By.tagName("tr"));
		Assert.assertEquals("Wrong number of announcements after scrolling.", 35, rows.size());
		String announcementTitle = rows.get(10).findElements(By.tagName("td")).get(0).findElement(By.tagName("a"))
				.getText();
		rows.get(10).click();
		Thread.sleep(500);
		Assert.assertEquals("Announcement page title not as expected.", "vest - " + announcementTitle + " - bundolo",
				getDriver().getTitle());
	}

	@Test
	public void topicListTest() throws Exception {
		getDriver().get(TestConstants.TESTING_ROOT + "/" + TestConstants.TOPICS);
		System.out.println("driver.getCurrentUrl(): " + getDriver().getCurrentUrl());
		Thread.sleep(500);
		Assert.assertEquals("Topics page title not as expected.", "diskusije - bundolo", getDriver().getTitle());

		List<WebElement> rows = getDriver().findElement(By.id(TestConstants.TOPICS)).findElement(By.tagName("tbody"))
				.findElements(By.tagName("tr"));
		Assert.assertEquals("Wrong number of topics.", 25, rows.size());

		((JavascriptExecutor) getDriver()).executeScript("$('#" + TestConstants.TOPICS + ">tbody').scrollTop(400)");
		Thread.sleep(500);
		rows = getDriver().findElement(By.id(TestConstants.TOPICS)).findElement(By.tagName("tbody"))
				.findElements(By.tagName("tr"));
		Assert.assertEquals("Wrong number of topics after scrolling.", 35, rows.size());
		String topicTitle = rows.get(18).findElements(By.tagName("td")).get(0).findElement(By.tagName("a")).getText();
		rows.get(18).click();
		Thread.sleep(500);
		Assert.assertEquals("Topic page title not as expected.", "diskusija - " + topicTitle + " - bundolo",
				getDriver().getTitle());
	}

	@Test
	public void contestListTest() throws Exception {
		getDriver().get(TestConstants.TESTING_ROOT + "/" + TestConstants.CONTESTS);
		System.out.println("driver.getCurrentUrl(): " + getDriver().getCurrentUrl());
		Thread.sleep(500);
		Assert.assertEquals("Contests page title not as expected.", "konkursi - bundolo", getDriver().getTitle());

		List<WebElement> rows = getDriver().findElement(By.id(TestConstants.CONTESTS)).findElement(By.tagName("tbody"))
				.findElements(By.tagName("tr"));
		Assert.assertEquals("Wrong number of contests.", 8, rows.size());

		// ((JavascriptExecutor) getDriver()).executeScript("$('#" +
		// TestConstants.CONTESTS +
		// ">tbody').scrollTop(300)");
		// Thread.sleep(500);
		// rows =
		// getDriver().findElement(By.id(TestConstants.CONTESTS)).findElement(By.tagName("tbody"))
		// .findElements(By.tagName("tr"));
		// Assert.assertEquals("Wrong number of contests after scrolling.", 35,
		// rows.size());
		String contestTitle = rows.get(0).findElements(By.tagName("td")).get(0).findElement(By.tagName("a")).getText();
		rows.get(0).click();
		Thread.sleep(500);
		Assert.assertEquals("Contest page title not as expected.", "konkurs - " + contestTitle + " - bundolo",
				getDriver().getTitle());
	}

	@Test
	public void connectiontListTest() throws Exception {
		System.out.println("driver: " + getDriver());
		getDriver().get(TestConstants.TESTING_ROOT + "/" + TestConstants.CONNECTIONS);
		System.out.println("driver.getCurrentUrl(): " + getDriver().getCurrentUrl());
		Thread.sleep(500);
		Assert.assertEquals("Connections page title not as expected.", "linkovi - bundolo", getDriver().getTitle());

		List<WebElement> rows = getDriver().findElement(By.id(TestConstants.CONNECTIONS))
				.findElement(By.tagName("tbody")).findElements(By.tagName("tr"));
		Assert.assertEquals("Wrong number of connections.", 25, rows.size());
		((JavascriptExecutor) getDriver())
				.executeScript("$('#" + TestConstants.CONNECTIONS + ">tbody').scrollTop(300)");
		Thread.sleep(500);
		rows = getDriver().findElement(By.id(TestConstants.CONNECTIONS)).findElement(By.tagName("tbody"))
				.findElements(By.tagName("tr"));
		Assert.assertEquals("Wrong number of connections after scrolling.", 35, rows.size());
		String connectionTitle = rows.get(24).findElements(By.tagName("td")).get(0).findElement(By.tagName("a"))
				.getText();
		rows.get(24).click();
		Thread.sleep(500);
		Assert.assertEquals("Connection page title not as expected.", "link - " + connectionTitle + " - bundolo",
				getDriver().getTitle());
	}
}