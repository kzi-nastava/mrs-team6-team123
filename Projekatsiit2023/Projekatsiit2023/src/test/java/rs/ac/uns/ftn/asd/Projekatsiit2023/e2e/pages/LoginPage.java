package rs.ac.uns.ftn.asd.Projekatsiit2023.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {
    private WebDriver driver;
    private WebElement emailInput;
    private WebElement passwordInput;
    private WebElement loginButton;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean isPageOpen() {
        return driver.findElement(By.tagName("h1")).getText().equals("Login");
    }

    public void login(String email, String password) {
        emailInput = driver.findElement(By.id("email-input"));
        passwordInput = driver.findElement(By.id("password-input"));
        loginButton = driver.findElement(By.id("login-button"));

        emailInput.sendKeys(email);
        passwordInput.sendKeys(password);
        WebDriverWait waitLoad = new WebDriverWait(driver, Duration.ofSeconds(10));
        waitLoad.until(driver -> loginButton.isEnabled());
        loginButton.click();
    }
}
