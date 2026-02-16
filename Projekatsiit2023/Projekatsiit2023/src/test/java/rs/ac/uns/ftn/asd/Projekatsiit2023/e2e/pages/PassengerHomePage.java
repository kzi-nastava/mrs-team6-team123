package rs.ac.uns.ftn.asd.Projekatsiit2023.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class PassengerHomePage {
    private WebDriver driver;

    public PassengerHomePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean isPageOpen() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement heading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h2")));
            return heading.getText().equals("Schedule a ride");
        } catch (TimeoutException e) {
            return false;
        }
    };

    public void openRideHistory() {
        WebElement rideHistoryButton = driver.findElement(By.cssSelector("img[src='history.png"));
        WebDriverWait waitLoad = new WebDriverWait(driver, Duration.ofSeconds(10));
        waitLoad.until(driver -> rideHistoryButton.isEnabled());
        rideHistoryButton.click();
    }
}
