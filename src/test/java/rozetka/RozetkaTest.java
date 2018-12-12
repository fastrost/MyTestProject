package rozetka;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import rozetka.pages.ComparePage;
import rozetka.pages.MainPage;
import rozetka.pages.NotebooksPage;
import rozetka.pages.NotebooksWithSsdPage;
import support.Browser;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static support.Browser.findElement;

public class RozetkaTest {

    private WebDriver driver = Browser.launch();
    private Actions actions = new Actions(driver);
    private MainPage mainPage = new MainPage();
    private NotebooksPage notebooksPage = new NotebooksPage();
    private NotebooksWithSsdPage notebooksWithSsdPage = new NotebooksWithSsdPage();
    private ComparePage comparePage = new ComparePage();
    private WebDriverWait wait = new WebDriverWait(driver,7);


    private Logger logger = Logger.getLogger("MyLog");
    private File logFile = new File("bin/LogFile.log");
    private FileHandler fh;
    {
        try {
            fh = new FileHandler(logFile.getAbsolutePath());
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            logger.setUseParentHandlers(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void rozetkaTest(){

        logger.info("Open browser " + Browser.browser.toString());

        driver.manage().window().maximize();
        driver.get("https://rozetka.com.ua");

        logger.info("Navigate to \"https://rozetka.com.ua\"");

        WebElement sidebarNotebooksAndComputers = findElement(mainPage.sidebarNotebooksAndComputers);
        actions.moveToElement(sidebarNotebooksAndComputers).build().perform();
        actions.moveToElement(sidebarNotebooksAndComputers).perform();
        logger.info("Focused on \"notebooks and computers\" tab");

        wait.until(ExpectedConditions.attributeContains(findElement(mainPage.popUpMenu),"class", "hover-layer"));

        findElement(mainPage.tabNotebooks).click();
        logger.info("Clicked on \"notebooks\" tab");

        WebElement notebooksWithSsd = findElement("");
        int indexTypeNotebook = 0;
        while (!driver.findElements(By.cssSelector(notebooksPage.listNotebookType)).get(indexTypeNotebook).getAttribute("innerText").toLowerCase().contains("ssd")) {
            indexTypeNotebook++;
            notebooksWithSsd = driver.findElements(By.cssSelector(notebooksPage.listNotebookType)).get(indexTypeNotebook);
        }
        notebooksWithSsd.click();
        logger.info("Clicked on \"notebooks with SSD\" tab");

        Assert.assertTrue(findElement(notebooksPage.checkNotebookType).getAttribute("innerText").toLowerCase().contains("ssd"));

        List<WebElement> buttonsCompare = driver.findElements(By.cssSelector(notebooksWithSsdPage.listButtonsCompare));

        actions.moveToElement(buttonsCompare.get(0)).perform();
        logger.info("Focused on the first notebook");
        wait.until(ExpectedConditions.visibilityOf(buttonsCompare.get(0)));
        buttonsCompare.get(0).click();
        wait.until(ExpectedConditions.attributeContains(findElement(notebooksWithSsdPage.checkInComparison),"class","incomparison"));
        logger.info("Added the first notebook to comparison");

        wait.until(ExpectedConditions.attributeContains(driver.findElement(By.cssSelector(comparePage.numberOfComparativeProducts)), "innerHTML", "1"));

        actions.moveToElement(buttonsCompare.get(1)).perform();
        logger.info("Focused on the second notebook");
        wait.until(ExpectedConditions.visibilityOf(buttonsCompare.get(1)));
        wait.until(ExpectedConditions.visibilityOf(buttonsCompare.get(1)));
        buttonsCompare.get(1).click();
        logger.info("Added the second notebook to comparison");

        driver.navigate().refresh();
        wait.until(ExpectedConditions.attributeContains(driver.findElement(By.cssSelector(comparePage.numberOfComparativeProducts)), "innerHTML", "2"));

        driver.findElement(By.cssSelector(notebooksWithSsdPage.headerButtonCompare)).click();
        logger.info("Clicked on \"compare\" button at the header");
        driver.findElement(By.cssSelector(comparePage.buttonCompareTheseProducts)).click();
        logger.info("Clicked on \"compare these products\" button");

        int counter = 0;
        List<WebElement> listCellsCompare = driver.findElements(By.cssSelector(comparePage.cellsCompare));
        for (int i = 0; i < listCellsCompare.size(); i+=2) {
            if (!listCellsCompare.get(i).getText().equals(listCellsCompare.get(i + 1).getText())) {
                counter++;
            }
        }

        findElement(comparePage.buttonOnlyDifferences).click();
        logger.info("Clicked on \"only the differences\" button");

        int countOfDifferences = driver.findElements(By.cssSelector(comparePage.rowsCompareDifferences)).size();
        Assert.assertEquals(countOfDifferences,counter);

    }

    @After
    public void quitBrowser() {
        driver.quit();
        driver = null;
    }

}
