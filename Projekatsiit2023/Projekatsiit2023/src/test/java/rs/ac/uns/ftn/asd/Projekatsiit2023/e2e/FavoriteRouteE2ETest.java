package rs.ac.uns.ftn.asd.Projekatsiit2023.e2e;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FavoriteRouteE2ETest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeAll
    void setup() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    void teardown() {
        driver.quit();
    }

    @Test
    @DisplayName("E2E: Order ride from favorites and manage favorites")
    void favoriteRideFlowE2E() {

        // Login
        driver.get("http://localhost:4200/login");
        driver.findElement(By.name("email")).sendKeys("user2@example.com");
        driver.findElement(By.name("password")).sendKeys("password123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        // sanity wait for login to complete
        wait.until(ExpectedConditions.urlContains("/registered-home"));

        // Go to history
        driver.get("http://localhost:4200/history");

        List<WebElement> rows = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.cssSelector("table.ride-table tbody tr")));

        WebElement firstRow = rows.get(0);
        WebElement heartBtn = firstRow.findElement(By.cssSelector("button[data-testid='favorite-toggle']"));
        String routeId = heartBtn.getAttribute("data-route-id");

        // Favorite if needed
        if (!heartBtn.getAttribute("class").contains("favorited")) {
            heartBtn.click();
            wait.until(ExpectedConditions.attributeContains(heartBtn, "class", "favorited"));
        }

        // Open favorites page directly
        driver.get("http://localhost:4200/favorites");

        List<WebElement> cards = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.cssSelector("[data-testid='favorites-list'] app-route-card")));

        WebElement targetCard = null;
        for (WebElement card : cards) {
            if (routeId.equals(card.getAttribute("data-route-id"))) {
                targetCard = card;
                break;
            }
        }

        Assertions.assertNotNull(targetCard, "Favorite route must exist");

        // Book ride
        targetCard.findElement(By.cssSelector(".btn-book")).click();

        // small sanity wait
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {
        }

        // Unfavorite in history
        driver.get("http://localhost:4200/history");

        List<WebElement> historyRows = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.cssSelector("table.ride-table tbody tr")));

        WebElement targetRow = null;
        for (WebElement row : historyRows) {
            WebElement btn = row.findElement(By.cssSelector("button[data-testid='favorite-toggle']"));
            if (routeId.equals(btn.getAttribute("data-route-id"))) {
                targetRow = row;
                break;
            }
        }

        Assertions.assertNotNull(targetRow);

        WebElement heartBtn2 = targetRow.findElement(
                By.cssSelector("button[data-testid='favorite-toggle']"));

        // debug BEFORE
        String before = heartBtn2.getAttribute("class");
        wait.until(ExpectedConditions.attributeContains(
                heartBtn2,
                "class",
                "favorited"));

        // click using JS to ensure it triggers Angular
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();",
                heartBtn2);

        // wait until Angular updates it
        wait.until(ExpectedConditions.not(
                ExpectedConditions.attributeContains(
                        heartBtn2,
                        "class",
                        "favorited")));

        // debug AFTER
        String after = heartBtn2.getAttribute("class");

        // WAIT until the route is truly gone from favorites
        driver.get("http://localhost:4200/favorites");

        wait.until(driver1 -> {
            List<WebElement> currentCards = driver1.findElements(
                    By.cssSelector("[data-testid='favorites-list'] app-route-card"));

            // success if routeId is no longer present
            for (WebElement card : currentCards) {
                if (routeId.equals(card.getAttribute("data-route-id"))) {
                    return false; // still present → keep waiting
                }
            }
            return true; // gone → success
        });

        // Final assertion to confirm it's gone
        List<WebElement> finalCards = driver.findElements(
                By.cssSelector("[data-testid='favorites-list'] app-route-card"));

        Assertions.assertTrue(finalCards.stream()
                .noneMatch(c -> routeId.equals(c.getAttribute("data-route-id"))),
                "Route should be removed from favorites");
    }
}
