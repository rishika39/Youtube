package demo;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.time.Duration;
import java.util.List;
import java.util.logging.Level;

import demo.utils.ExcelDataProvider;
// import io.github.bonigarcia.wdm.WebDriverManager;
import demo.wrappers.Wrappers;

public class TestCases extends ExcelDataProvider{ // Lets us read the data
        ChromeDriver driver;

@Test
public void testCase01() {
    Wrappers wrappers = new Wrappers(driver);

    driver.get("https://www.youtube.com");
    Assert.assertEquals(driver.getCurrentUrl(), "https://www.youtube.com/", "URL is not correct");

    WebElement aboutbtn = driver.findElement(By.xpath("//button[@id='button' and @class='style-scope yt-icon-button' and @aria-label='Guide']"));
    wrappers.clickElement(aboutbtn);
    
    WebElement abt = driver.findElement(By.xpath("//*[@id='guide-links-primary']/a[1]"));
    wrappers.clickElement(abt);
    //System.out.println(wrappers.getText(messageElement));

    // Print the "About YouTube" message
    WebElement aboutSection = driver.findElement(By.xpath("//section[@class='ytabout__content']"));
    WebElement title = aboutSection.findElement(By.xpath("./h1"));
    WebElement mission = aboutSection.findElement(By.xpath("./p[1]"));
    WebElement description = aboutSection.findElement(By.xpath("./p[2]"));

    System.out.println("Title: " + title.getText());
    System.out.println("Mission: " + mission.getText());
    System.out.println("Description: " + description.getText());
}

@Test
public void testCase02() throws InterruptedException {
    Wrappers wrappers = new Wrappers(driver);
    SoftAssert softAssert = new SoftAssert();
    driver.get("https://www.youtube.com");

    WebElement aboutbtn = driver.findElement(By.xpath("//button[@id='button' and @class='style-scope yt-icon-button' and @aria-label='Guide']"));
    wrappers.clickElement(aboutbtn);

     // Click on the "Films" or "Movies" tab
     WebElement filmsTab = driver.findElement(By.xpath("(//*[@id='endpoint']/tp-yt-paper-item)[9]"));
     wrappers.clickElement(filmsTab);
 Thread.sleep(3000);


    // Locate the "Top Selling" section
    WebElement topSellingSection = driver.findElement(By.xpath("(//*[@id=\"items\"])[16]"));

    WebElement rightArrowButton = driver.findElement(By.xpath("//*[@id='right-arrow']/ytd-button-renderer/yt-button-shape/button"));

    // Keep clicking the right arrow button until it's disabled or invisible
    while (rightArrowButton.isDisplayed() && rightArrowButton.isEnabled()) {
        wrappers.clickElement(rightArrowButton);
        Thread.sleep(2000);  // Allow some time for the scroll to complete
    }

    // Find the last movie element after scrolling to the extreme right using the provided XPath
    WebElement lastMovieElement = driver.findElement(By.xpath("(//*[@id='thumbnail'])[18]"));

    // Apply Soft Assert on movie details for the last movie
    String movieRating = wrappers.getText(lastMovieElement.findElement(By.xpath(".//ancestor::ytd-grid-movie-renderer//ytd-badge-supported-renderer/div[2]/p")));
    wrappers.softAssertText(softAssert, movieRating, "U", "The last movie is not marked as 'A' for Mature");

    String movieCategory = wrappers.getText(lastMovieElement.findElement(By.xpath(".//ancestor::ytd-grid-movie-renderer//span[@class='grid-movie-renderer-metadata style-scope ytd-grid-movie-renderer']")));
    softAssert.assertTrue(movieCategory.contains("Comedy") || movieCategory.contains("Animation") || movieCategory.contains("Drama"), "Category is missing for the last movie");

    softAssert.assertAll();
}

@Test
public void testCase03() throws InterruptedException {
    Wrappers wrappers = new Wrappers(driver);
    SoftAssert softAssert = new SoftAssert();
    driver.get("https://www.youtube.com");
    WebElement aboutbtn = driver.findElement(By.xpath("//button[@id='button' and @class='style-scope yt-icon-button' and @aria-label='Guide']"));
    wrappers.clickElement(aboutbtn);
    // Click on the "Music" tab
    WebElement musicTab = driver.findElement(By.xpath("(//*[@id='endpoint']/tp-yt-paper-item)[8]"));
    wrappers.clickElement(musicTab);
    Thread.sleep(3000);  // Wait for the tab to load
WebElement scr= driver.findElement(By.xpath("(//*[@id='dismissible'])[1]"));
wrappers.scrollToElement(scr);
    // Locate the right arrow button in the 1st section
    WebElement rightBtn = driver.findElement(By.xpath("(//*[@id=\"right-arrow\"]/ytd-button-renderer/yt-button-shape/button)[1]"));

    // Scroll to the extreme right by clicking the right arrow button until it's no longer clickable
    while (rightBtn.isDisplayed() && rightBtn.isEnabled()) {
        wrappers.clickElement(rightBtn);
        Thread.sleep(2000);  // Allow time for scrolling
    }

    // Wait for the elements to be visible after scrolling
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebElement playlistElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='items']/ytd-compact-station-renderer[9]")));

    // Print the name of the playlist
    WebElement playlistNameElement = playlistElement.findElement(By.xpath(".//h3"));
    String playlistName = wrappers.getText(playlistNameElement);
    System.out.println("Playlist Name: " + playlistName);

    if (playlistName.isEmpty()) {
        System.out.println("Error: Playlist name is empty.");
    }

    // Get the number of tracks in the playlist
    WebElement trackCountElement = playlistElement.findElement(By.xpath(".//p[@id='video-count-text']"));
    String trackCountText = wrappers.getText(trackCountElement);
    System.out.println("Track Count Text: " + trackCountText);

    if (trackCountText.isEmpty()) {
        System.out.println("Error: Track count text is empty.");
    }

    try {
        int trackCount = Integer.parseInt(trackCountText.split(" ")[0]);
        softAssert.assertTrue(trackCount <= 50, "Track count exceeds 50");
    } catch (NumberFormatException e) {
        softAssert.fail("Failed to parse track count: " + trackCountText);
    }

    softAssert.assertAll();
}

@Test
public void testCase04() throws InterruptedException {
    Wrappers wrappers = new Wrappers(driver);
    SoftAssert softAssert = new SoftAssert();
    int totalLikes = 0;
    driver.get("https://www.youtube.com");
    WebElement aboutbtn = driver.findElement(By.xpath("//button[@id='button' and @class='style-scope yt-icon-button' and @aria-label='Guide']"));
    wrappers.clickElement(aboutbtn);
    // Click on the "Music" tab
    WebElement newsTab = driver.findElement(By.xpath("(//*[@id='endpoint']/tp-yt-paper-item)[12]"));
    wrappers.clickElement(newsTab);
    Thread.sleep(3000);  // Wait for the tab to load
    WebElement scr= driver.findElement(By.xpath("(//*[@id=\"dismissible\"])[30]"));
    wrappers.scrollToElement(scr);
    for (int i = 1; i <= 3; i++) {
        // Print the title
        WebElement titleElement = driver.findElement(By.xpath("(//*[@id='home-content-text'])["+ i +"]"));
        String title = wrappers.getText(titleElement);
        System.out.println("Title " + i + ": " + title);
        
        WebElement likesElement = driver.findElement(By.xpath("(//*[@id='vote-count-middle'])["+ i +"]"));
        String likesText = wrappers.getText(likesElement);
        int likes = 0;
        try {
            if (!likesText.isEmpty()) {
                likes = Integer.parseInt(likesText.replaceAll("[^0-9]", ""));
            }
        } catch (NumberFormatException e) {
            System.out.println("Error parsing likes for post " + i + ": " + likesText);
        }

        totalLikes += likes;
    }
    System.out.println("Total Likes: " + totalLikes);
    softAssert.assertAll();
}

@Test(dataProvider = "excelData", dataProviderClass = ExcelDataProvider.class)
    public void testCase05(String searchItem) throws InterruptedException {
        Wrappers wrappers = new Wrappers(driver);
        SoftAssert softAssert = new SoftAssert();
        driver.get("https://www.youtube.com");

        // Perform search for each item
        WebElement searchBox = driver.findElement(By.xpath("//input[@id='search']"));
        searchBox.sendKeys(searchItem);
        WebElement searchButton = driver.findElement(By.xpath("//button[@id='search-icon-legacy']"));
        wrappers.clickElement(searchButton);

        long totalViews = 0;
        boolean reachedTarget = false;
        // Scroll and accumulate views
        while (!reachedTarget) {
            List<WebElement> videoElements = driver.findElements(By.xpath("//*[@id=\"contents\"]/ytd-video-renderer"));
            for (WebElement videoElement : videoElements) {
                String viewsText = videoElement.getText().replaceAll("[^\\d]", ""); // Remove non-numeric characters
                try {
                    long views = Long.parseLong(viewsText);
                    totalViews += views;
                    if (totalViews >= 100_000_000) { // 10 Crore (100 million)
                        reachedTarget = true;
                        break;
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid numbers
                }
            }
            if (!reachedTarget) {
                WebElement rightBtn = driver.findElement(By.xpath("//button[@aria-label='Next']")); // Adjust XPath if necessary
                if (rightBtn.isDisplayed() && rightBtn.isEnabled()) {
                    wrappers.clickElement(rightBtn);
                    Thread.sleep(2000); // Allow time for scrolling
                } else {
                    break; // Exit if no more pages
                }
            }
        }

        System.out.println("Total Views: " + totalViews);
        softAssert.assertTrue(totalViews >= 100_000_000, "Total views did not reach 10 Crore (100 million)");

        softAssert.assertAll();
    }
        @BeforeTest
        public void startBrowser() {
                System.setProperty("java.util.logging.config.file", "logging.properties");

                // NOT NEEDED FOR SELENIUM MANAGER
                // WebDriverManager.chromedriver().timeout(30).setup();

                ChromeOptions options = new ChromeOptions();
                LoggingPreferences logs = new LoggingPreferences();

                logs.enable(LogType.BROWSER, Level.ALL);
                logs.enable(LogType.DRIVER, Level.ALL);
                options.setCapability("goog:loggingPrefs", logs);
                options.addArguments("--remote-allow-origins=*");

                System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log");

                driver = new ChromeDriver(options);

                driver.manage().window().maximize();
        }

        @AfterTest
        public void endTest() {
                driver.close();
                driver.quit();

        }
}