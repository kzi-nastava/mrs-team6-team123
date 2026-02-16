package rs.ac.uns.ftn.asd.Projekatsiit2023.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class PassengerRideHistoryPage {
    private WebDriver driver;

    public PassengerRideHistoryPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean isPageOpen() {
        return driver.findElement(By.tagName("h1")).getText().equals("My Ride History");
    }

    public boolean openRateForm() {
        WebDriverWait waitLoad = new WebDriverWait(driver, Duration.ofSeconds(10));
        waitLoad.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".ride-table")));
        List<WebElement> rateButtons = driver.findElements(By.cssSelector(".ride-table .rate-btn"));
        if (rateButtons.isEmpty()) {
            return false;
        }
        rateButtons.get(0).click();
        return true;
    }

    public boolean isRateFormOpen() {
        WebDriverWait waitLoad = new WebDriverWait(driver, Duration.ofSeconds(10));
        waitLoad.until(ExpectedConditions.visibilityOfElementLocated(By.className("rate-dialog")));
        WebElement dialog = driver.findElement(By.className("rate-dialog"));
        return dialog.findElement(By.tagName("h1")).getText().equals("Rate Drive");
    }

    public void rate(int driverRating, int vehicleRating, String comment) {
        WebDriverWait waitLoad = new WebDriverWait(driver, Duration.ofSeconds(10));
        waitLoad.until(ExpectedConditions.visibilityOfElementLocated(By.className("rate-dialog")));
        WebElement dialog = driver.findElement(By.className("rate-dialog"));

        waitLoad = new WebDriverWait(driver, Duration.ofSeconds(10));
        waitLoad.until(ExpectedConditions.visibilityOfElementLocated(By.className("stars")));

        List<WebElement> ratingSections = dialog.findElements(By.className("stars"));

        List<WebElement> driverStars = ratingSections.get(0).findElements(By.className("star"));
        driverStars.get(driverRating - 1).click();

        List<WebElement> vehicleStars = ratingSections.get(1).findElements(By.className("star"));
        vehicleStars.get(vehicleRating - 1).click();

        WebElement commentBox = dialog.findElement(By.tagName("textarea"));
        commentBox.sendKeys(comment);

        WebElement rateButton = dialog.findElement(By.className("rate-btn"));
        rateButton.click();
    }

    public void waitForDialogClosed() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("rate-dialog")));
    }

    public int getRidesForRating() {
        List<WebElement> rateButtons = driver.findElements(By.cssSelector(".ride-table .rate-btn"));
        return rateButtons.size();
    }
}
