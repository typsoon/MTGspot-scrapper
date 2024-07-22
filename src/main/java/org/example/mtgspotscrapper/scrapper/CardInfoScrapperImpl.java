package org.example.mtgspotscrapper.scrapper;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class CardInfoScrapperImpl implements CardInfoScrapper {
    @Override
    public Double getCardPrice(String cardName) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run in headless mode

        // Create a new instance of the Chrome driver
        WebDriver driver = new ChromeDriver(options);
//        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Navigate to the page with the form
            driver.get("https://mtgspot.pl/");

            Thread.sleep(250);

            // Wait and click the search lens icon
            String searchLensXPath = "//*[@id=\"__nuxt\"]/div/div[1]/header/div/div/div[2]/ul/li[1]";
            WebElement searchLens = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(searchLensXPath)));
            searchLens.click();

            Thread.sleep(250);

            // Wait for the search field to be visible and interactable
            String searchFieldId = "advancedSearch";
            WebElement searchField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(searchFieldId)));
            searchField.sendKeys(cardName);

            Thread.sleep(250);

            // Wait and click the submit button
            String submitButtonXPath = "//*[@id=\"__nuxt\"]/div/div[1]/header/div/div/div[1]/form/div/div/div[2]/div[7]/button/div[1]";
            WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(submitButtonXPath)));
            submitButton.click();

            Thread.sleep(750);

            String showFiltersXPath = "//*[@id=\"__nuxt\"]/div/div[1]/div[2]/div[3]";
            WebElement showFilters = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(showFiltersXPath)));
            if (showFilters.isDisplayed()) {
                showFilters.click();
            }

            String onlyAvailableButtonXPath = "//*[@id=\"__nuxt\"]/div/div[1]/div[2]/div[2]/div/form/div[1]/div/div";
            WebElement onlyAvailable = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(onlyAvailableButtonXPath)));
            onlyAvailable.click();

            WebElement howManyToShowSelectField = driver.findElement(By.xpath("/html/body/div/div/div[1]/div[2]/div[4]/div[1]/div[1]/select"));
            Select howManyToShow = new Select(howManyToShowSelectField);
            howManyToShow.selectByVisibleText("Wszystkie");

            WebElement sortBySelectField = driver.findElement(By.xpath("/html/body/div/div/div[1]/div[2]/div[4]/div[1]/div[2]/select"));
            Select sortBy = new Select(sortBySelectField);
            sortBy.selectByVisibleText("Cena rosnąco");

            Thread.sleep(4000);

            WebElement elementsTable = driver.findElement(By.xpath("//*[@id=\"__nuxt\"]/div/div[1]/div[2]/div[1]"));
            List<WebElement> elements = elementsTable.findElements(By.tagName("a"));

            double minPrice = Double.POSITIVE_INFINITY;

            System.out.println("Number of elements found: " + elements.size());
            for (WebElement element : elements) {
                List<WebElement> children = element.findElements(By.xpath("./*"));
                String[] data = children.getFirst().getText().split("\n");
                for (String attribute : data) {
                    System.out.print(attribute + ", ");
                }
                if (data.length >= 5 && data[0].equals(cardName)) {
                    minPrice = Math.min(minPrice, Double.parseDouble(data[4].substring(0, data[4].length() - 2)));
                }
                System.out.println();
            }

            Thread.sleep(250);

            // Print the current page URL
            System.out.println("Current page URL: " + driver.getCurrentUrl());

            if (minPrice == Double.POSITIVE_INFINITY) {
                return null;
            }
            return minPrice;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } finally {
            // Close the WebDriver
            driver.quit();
        }
    }

    public static void main(String[] args) {
        var scrapper = new CardInfoScrapperImpl();
        System.out.println(scrapper.getCardPrice("Llanowar Elves"));
    }
}
