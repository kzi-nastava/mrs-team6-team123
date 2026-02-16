package rs.ac.uns.ftn.asd.Projekatsiit2023.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class UnregisteredHomePage {
    private WebDriver driver;

    public UnregisteredHomePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void clickProfileButton() {
        WebElement profileButton = driver.findElement(By.cssSelector("img[src='user.png']"));
        WebDriverWait waitLoad = new WebDriverWait(driver, Duration.ofSeconds(10));
        waitLoad.until(driver -> profileButton.isEnabled());
        profileButton.click();
    }
}
