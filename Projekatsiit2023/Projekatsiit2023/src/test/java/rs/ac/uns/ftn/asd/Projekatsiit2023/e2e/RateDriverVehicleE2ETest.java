package rs.ac.uns.ftn.asd.Projekatsiit2023.e2e;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import rs.ac.uns.ftn.asd.Projekatsiit2023.e2e.pages.LoginPage;
import rs.ac.uns.ftn.asd.Projekatsiit2023.e2e.pages.PassengerHomePage;
import rs.ac.uns.ftn.asd.Projekatsiit2023.e2e.pages.PassengerRideHistoryPage;
import rs.ac.uns.ftn.asd.Projekatsiit2023.e2e.pages.UnregisteredHomePage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RateDriverVehicleE2ETest {

    private static WebDriver driver;
    private UnregisteredHomePage unregisteredHomePage;
    private LoginPage loginPage;
    private PassengerHomePage passengerHomePage;
    private PassengerRideHistoryPage passengerRideHistoryPage;

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.get("http://localhost:4200");

        unregisteredHomePage = new UnregisteredHomePage(driver);
        loginPage = new LoginPage(driver);
        passengerHomePage = new PassengerHomePage(driver);
        passengerRideHistoryPage = new PassengerRideHistoryPage(driver);
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testRateRide() {
        unregisteredHomePage.clickProfileButton();

        assertTrue(loginPage.isPageOpen(), "Login form should be present");
        loginPage.login("user2@example.com", "password123");

        assertTrue(passengerHomePage.isPageOpen(), "Passenger home page should be open after login");
        passengerHomePage.openRideHistory();

        assertTrue(passengerRideHistoryPage.isPageOpen(), "Passenger ride history page should be open");

        Assumptions.assumeTrue(passengerRideHistoryPage.openRateForm(), "No rides available for rating");

        int ridesToRate = passengerRideHistoryPage.getRidesForRating();

        assertTrue(passengerRideHistoryPage.isRateFormOpen(), "Rate form should be open");
        passengerRideHistoryPage.rate(4, 4, "Ride was good!");

        passengerRideHistoryPage.waitForDialogClosed();
        int remainingRidesToRate = passengerRideHistoryPage.getRidesForRating();

        assertEquals(remainingRidesToRate, ridesToRate - 1);
    }
}
