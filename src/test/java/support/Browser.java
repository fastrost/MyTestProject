package support;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class Browser {

    public static final int DEFAULT_TIMEOUT = 5;
    public static WebDriver browser;

    private static final String browserName;

    static {
        try {
            Properties properties = new Properties();
            properties.load(Browser.class.getClassLoader().getResourceAsStream("selenium.properties"));
            browserName = properties.getProperty("browser");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static WebDriver launch() {
        browser = createDriver();
        browser.manage().timeouts().implicitlyWait(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        return browser;
    }


    public static WebElement findElement(String css) {
        return findChildElement(browser.findElement(By.cssSelector("html")), css);
    }

    public static WebElement findChildElement(WebElement parent, String css) {
        try {
            return parent.findElement(By.cssSelector(css));
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return null;
        }
    }

    private static WebDriver createDriver() {
        if (browserName.equals("chrome")) {
            try {
                return new ChromeDriver();
            } catch (RuntimeException e) {
                String path;
                if (System.getProperty("os.name").contains("win")) {
                    path = "bin/chromedriver.exe";
                } else {
                    path = "bin/chromedriver";
                }
                File file = new File(path);
                System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());
                return new ChromeDriver();
            }
        } else {
            throw new RuntimeException("Unrecognized system property 'browser': " + browserName);
        }
    }

}
