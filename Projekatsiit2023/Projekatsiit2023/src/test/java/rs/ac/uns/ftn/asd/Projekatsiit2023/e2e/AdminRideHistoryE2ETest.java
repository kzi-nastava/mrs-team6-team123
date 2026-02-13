package rs.ac.uns.ftn.asd.Projekatsiit2023.e2e;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdminRideHistoryE2ETest {

    private static WebDriver driver;
    private static WebDriverWait wait;
    private static WebDriverWait shortWait; 

    private static final String BASE_URL       = "http://localhost:4200";
    private static final String ADMIN_EMAIL    = "user1@example.com";
    private static final String ADMIN_PASSWORD = "password123";

    // ─────────────────────────── SETUP ───────────────────────────

    @BeforeAll
    static void setUp() {
        log("=== TEST SUITE START ===");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        driver    = new ChromeDriver(options);
        wait      = new WebDriverWait(driver, Duration.ofSeconds(15));
        shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
        log("ChromeDriver initialized");
    }

    @AfterAll
    static void tearDown() {
        log("=== TEST SUITE END ===");
        if (driver != null) driver.quit();
    }

    private static void log(String msg) {
        System.out.println("[E2E] " + msg);
    }

    private WebElement waitVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    private WebElement waitClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    private void waitForTextIn(By locator, String expectedText) {
        wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, expectedText));
    }

    private void waitForLoadingToFinish() {
        By spinner = By.cssSelector(".loading-container");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(1))
                    .until(ExpectedConditions.presenceOfElementLocated(spinner));
            wait.until(ExpectedConditions.invisibilityOfElementLocated(spinner));
            log("Spinner gone");
        } catch (TimeoutException e) {
            log("No spinner detected");
        }
    }

    private List<WebElement> waitForRows() {
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".ride-table tbody tr")));
        wait.until((ExpectedCondition<Boolean>) d -> {
            List<WebElement> rows = d.findElements(By.cssSelector(".ride-table tbody tr"));
            return !rows.isEmpty();
        });
        List<WebElement> rows = driver.findElements(By.cssSelector(".ride-table tbody tr"));
        log("Table rows: " + rows.size());
        return rows;
    }

    private String getCellText(int rowIndex, int colIndex) {
        List<WebElement> rows = driver.findElements(By.cssSelector(".ride-table tbody tr"));
        List<WebElement> cells = rows.get(rowIndex).findElements(By.tagName("td"));
        return cells.get(colIndex).getText().trim();
    }

    private WebElement getSortableHeader(String columnName) {
        return wait.until(d -> {
            List<WebElement> headers = d.findElements(By.cssSelector(".ride-table th.sortable"));
            for (WebElement h : headers) {
                try {
                    if (h.getText().contains(columnName)) return h;
                } catch (StaleElementReferenceException ignored) {}
            }
            return null;
        });
    }

    private void clickSortHeader(String columnName) {
        log("Clicking sort header: " + columnName);
        getSortableHeader(columnName).click();
        waitForLoadingToFinish();
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".ride-table tbody tr")));
        log("Sort applied for: " + columnName);
    }

    private WebElement waitForFilterApplyBtn() {
        String[] selectors = {
            "app-ride-history-filter .btn-search",
            "app-ride-history-filter button[type='submit']",
            "app-ride-history-filter .btn-primary",
            "app-ride-history-filter button:first-of-type"
        };
        for (String sel : selectors) {
            try {
                WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(2))
                        .until(ExpectedConditions.elementToBeClickable(By.cssSelector(sel)));
                log("Filter apply btn found via: " + sel);
                return btn;
            } catch (TimeoutException ignored) {}
        }
        return wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("app-ride-history-filter button")));
    }

    private WebElement waitForFilterClearBtn() {
        String[] selectors = {
            "app-ride-history-filter .btn-clear",
            "app-ride-history-filter button:last-of-type",
            "app-ride-history-filter .btn-secondary"
        };
        for (String sel : selectors) {
            try {
                WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(2))
                        .until(ExpectedConditions.elementToBeClickable(By.cssSelector(sel)));
                log("Filter clear btn found via: " + sel);
                return btn;
            } catch (TimeoutException ignored) {}
        }
        return wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("app-ride-history-filter button:last-child")));
    }
    private void setDateInput(By inputLocator, String isoDate) {
        WebElement input = waitClickable(inputLocator);
        ((JavascriptExecutor) driver).executeScript(
                "var el = arguments[0];" +
                "var nativeInputValueSetter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;" +
                "nativeInputValueSetter.call(el, arguments[1]);" +
                "el.dispatchEvent(new Event('input', {bubbles:true}));" +
                "el.dispatchEvent(new Event('change', {bubbles:true}));",
                input, isoDate);
        // Čeka da Angular procira promenu
        wait.until(ExpectedConditions.attributeToBe(input, "value", isoDate));
        log("Date input set: " + isoDate);
    }


    // ─────────────────────────── AUTH HELPERS ────────────────────

    private void clearAuthState() {
        log("Clearing auth state...");
        driver.get(BASE_URL + "/unregistered-home");
        wait.until(d -> ((JavascriptExecutor) d)
                .executeScript("return document.readyState").equals("complete"));
        ((JavascriptExecutor) driver).executeScript(
                "localStorage.removeItem('auth_token'); localStorage.removeItem('current_user');");
        driver.navigate().refresh();
        wait.until(d -> ((JavascriptExecutor) d)
                .executeScript("return document.readyState").equals("complete"));

        wait.until(d -> ((JavascriptExecutor) d)
                .executeScript("return localStorage.getItem('auth_token')") == null);
        log("Auth state cleared + Angular restarted");
    }

    private void loginAsAdmin() {
        clearAuthState();
        log("Navigating to /login...");
        driver.get(BASE_URL + "/login");

        waitVisible(By.cssSelector(".auth-form"));

        WebElement emailInput = waitClickable(
                By.cssSelector("input[type='email'], input[placeholder='Email']"));
        emailInput.clear();
        emailInput.sendKeys(ADMIN_EMAIL);

        WebElement passwordInput = waitClickable(
                By.cssSelector("input[type='password'], input[placeholder='Password']"));
        passwordInput.clear();
        passwordInput.sendKeys(ADMIN_PASSWORD);

        WebElement loginButton = waitClickable(By.xpath("//button[@type='submit']"));
        try {
            loginButton.click();
        } catch (ElementNotInteractableException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", loginButton);
        }

        wait.until(ExpectedConditions.urlContains("/admin/home"));
        log("Login OK — URL: " + driver.getCurrentUrl());
    }

    private void navigateToRideHistory() {
        log("Navigating to /admin/ride-history...");
        driver.get(BASE_URL + "/admin/ride-history");
        waitVisible(By.cssSelector("h1"));
        log("Ride history page ready");
    }

    // ─────────────────────────── PARSERS ─────────────────────────

    private LocalDate parseDate(String text) {
        try {
            return LocalDate.parse(text.trim());
        } catch (Exception e) {
            return LocalDate.parse(text.trim(),
                    java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        }
    }

    private double parseKm(String text) {
        return Double.parseDouble(text.replace(" km", "").trim());
    }

    private double parseRsd(String text) {
        return Double.parseDouble(text.replace(" RSD", "").trim());
    }

    // ═══════════════════════════════════════════════════════════════
    // LOGIN & ACCESS
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Login and access tests")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class LoginAndAccessTests {

        @Test
        @Order(1)
        @DisplayName("Should redirect to login when not authenticated")
        void shouldRedirectToLoginWhenNotAuthenticated() {
            log("--- TEST: shouldRedirectToLoginWhenNotAuthenticated ---");
            clearAuthState();
            driver.get(BASE_URL + "/admin/ride-history");
            wait.until(ExpectedConditions.urlContains("/login"));
            log("Redirected to: " + driver.getCurrentUrl());
            assertTrue(driver.getCurrentUrl().contains("/login"));
            log("PASS");
        }

        @Test
        @Order(2)
        @DisplayName("Admin should be able to login and access ride history")
        void adminShouldAccessRideHistory() {
            log("--- TEST: adminShouldAccessRideHistory ---");
            loginAsAdmin();
            navigateToRideHistory();
            WebElement heading = waitVisible(By.cssSelector("h1"));
            log("Heading: '" + heading.getText() + "'");
            assertEquals("Ride History Management", heading.getText());
            log("PASS");
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // PAGE ELEMENTS
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Page elements tests")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class PageElementTests {

        @BeforeEach
        void setup() {
            loginAsAdmin();
            navigateToRideHistory();
            waitForLoadingToFinish();
        }

        @Test
        @DisplayName("Should display page heading")
        void shouldDisplayHeading() {
            log("--- TEST: shouldDisplayHeading ---");
            WebElement heading = waitVisible(By.cssSelector("h1"));
            log("Heading: '" + heading.getText() + "'");
            assertEquals("Ride History Management", heading.getText());
            log("PASS");
        }

        @Test
        @DisplayName("Should display search section with user ID input")
        void shouldDisplaySearchSection() {
            log("--- TEST: shouldDisplaySearchSection ---");

            WebElement searchInput = waitVisible(
                    By.cssSelector(".search-input-group input[type='number']"));
            assertTrue(searchInput.isDisplayed());

            WebElement searchBtn = waitVisible(By.cssSelector(".btn-search"));
            log("Search button text: '" + searchBtn.getText() + "'");
            assertTrue(searchBtn.getText().contains("Search"));

            WebElement clearBtn = waitVisible(By.cssSelector(".btn-clear"));
            log("Clear button text: '" + clearBtn.getText() + "'");
            assertTrue(clearBtn.getText().contains("Show All"));
            log("PASS");
        }

        @Test
        @DisplayName("Should display ride count indicator")
        void shouldDisplayRideCount() {
            log("--- TEST: shouldDisplayRideCount ---");
            WebElement indicator = waitVisible(By.cssSelector(".view-indicator"));
            log("Indicator: '" + indicator.getText() + "'");
            assertTrue(indicator.getText().contains("rides"));
            log("PASS");
        }

        @Test
        @DisplayName("Should display table with correct headers")
        void shouldDisplayTableWithHeaders() {
            log("--- TEST: shouldDisplayTableWithHeaders ---");
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".ride-table th")));
            List<WebElement> headers = driver.findElements(By.cssSelector(".ride-table th"));
            log("Header count: " + headers.size());
            assertTrue(headers.size() >= 10);

            String all = headers.stream().map(WebElement::getText).reduce("", (a, b) -> a + " " + b);
            log("Headers: " + all);
            assertTrue(all.contains("Date"),      "Missing Date");
            assertTrue(all.contains("From"),      "Missing From");
            assertTrue(all.contains("To"),        "Missing To");
            assertTrue(all.contains("Price"),     "Missing Price");
            assertTrue(all.contains("Driver"),    "Missing Driver");
            assertTrue(all.contains("Cancelled"), "Missing Cancelled");
            assertTrue(all.contains("PANIC"),     "Missing PANIC");
            log("PASS");
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // SORTING
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Sorting tests")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class SortingTests {

        @BeforeEach
        void setup() {
            loginAsAdmin();
            navigateToRideHistory();
            waitForLoadingToFinish();
        }

        @Test
        @DisplayName("Should sort by Date ascending then descending")
        void shouldSortByDate() {
            log("--- TEST: shouldSortByDate ---");
            List<WebElement> initial = waitForRows();
            if (initial.size() < 2) { log("SKIP: < 2 rows"); return; }

            clickSortHeader("Date");
            if (waitForRows().size() >= 2) {
                LocalDate d1 = parseDate(getCellText(0, 0));
                LocalDate d2 = parseDate(getCellText(1, 0));
                log("Asc — [0]:" + d1 + " [1]:" + d2);
                assertFalse(d1.isAfter(d2), "Expected asc: " + d1 + " > " + d2);
            }

            clickSortHeader("Date");
            if (waitForRows().size() >= 2) {
                LocalDate d1 = parseDate(getCellText(0, 0));
                LocalDate d2 = parseDate(getCellText(1, 0));
                log("Desc — [0]:" + d1 + " [1]:" + d2);
                assertFalse(d1.isBefore(d2), "Expected desc: " + d1 + " < " + d2);
            }
            log("PASS");
        }

        @Test
        @DisplayName("Should sort by Price descending then ascending")
        void shouldSortByPrice() {
            log("--- TEST: shouldSortByPrice ---");
            if (waitForRows().size() < 2) { log("SKIP"); return; }


            clickSortHeader("Price");
            if (waitForRows().size() >= 2) {
                double p1 = parseRsd(getCellText(0, 5));
                double p2 = parseRsd(getCellText(1, 5));
                log("Desc — [0]:" + p1 + " [1]:" + p2);
                assertTrue(p1 >= p2, "Expected desc: " + p1 + " < " + p2);
            }

            clickSortHeader("Price");
            if (waitForRows().size() >= 2) {
                double p1 = parseRsd(getCellText(0, 5));
                double p2 = parseRsd(getCellText(1, 5));
                log("Asc — [0]:" + p1 + " [1]:" + p2);
                assertTrue(p1 <= p2, "Expected asc: " + p1 + " > " + p2);
            }
            log("PASS");
        }

        @Test
        @DisplayName("Should sort by Distance descending then ascending")
        void shouldSortByDistance() {
            log("--- TEST: shouldSortByDistance ---");
            if (waitForRows().size() < 2) { log("SKIP"); return; }

            List<WebElement> hdrs = driver.findElements(By.cssSelector(".ride-table th.sortable"));
            String allHdrs = hdrs.stream().map(WebElement::getText).reduce("", (a, b) -> a + "|" + b);
            log("Sortable headers: " + allHdrs);

            WebElement distHeader = wait.until(d -> {
                List<WebElement> hs = d.findElements(By.cssSelector(".ride-table th.sortable"));
                for (WebElement h : hs) {
                    try {
                        String t = h.getText();
                        if (t.contains("Distance") || t.contains("Dist") || t.contains("km")) return h;
                    } catch (StaleElementReferenceException ignored) {}
                }
                return null;
            });
            log("Distance header found: '" + distHeader.getText() + "'");

            distHeader.click();
            waitForLoadingToFinish();
            if (waitForRows().size() >= 2) {
                double k1 = parseKm(getCellText(0, 6));
                double k2 = parseKm(getCellText(1, 6));
                log("Desc — [0]:" + k1 + " [1]:" + k2);
                assertTrue(k1 >= k2, "Expected desc: " + k1 + " < " + k2);
            }

            wait.until(d -> {
                List<WebElement> hs = d.findElements(By.cssSelector(".ride-table th.sortable"));
                for (WebElement h : hs) {
                    try {
                        String t = h.getText();
                        if (t.contains("Distance") || t.contains("Dist") || t.contains("km")) {
                            h.click(); return true;
                        }
                    } catch (StaleElementReferenceException ignored) {}
                }
                return null;
            });
            waitForLoadingToFinish();
            if (waitForRows().size() >= 2) {
                double k1 = parseKm(getCellText(0, 6));
                double k2 = parseKm(getCellText(1, 6));
                log("Asc — [0]:" + k1 + " [1]:" + k2);
                assertTrue(k1 <= k2, "Expected asc: " + k1 + " > " + k2);
            }
            log("PASS");
        }

        @Test
        @DisplayName("Should display sort icon on active column")
        void shouldDisplaySortIcon() {
            log("--- TEST: shouldDisplaySortIcon ---");
            waitForRows();
            clickSortHeader("Date");

            wait.until(d -> {
                try {
                    String t = getSortableHeader("Date").getText();
                    return t.contains("↑") || t.contains("↓");
                } catch (Exception e) { return false; }
            });
            String txt = getSortableHeader("Date").getText();
            log("Date header: '" + txt + "'");
            assertTrue(txt.contains("↑") || txt.contains("↓"));
            log("PASS");
        }

        @Test
        @DisplayName("Should sort by Driver name descending")
        void shouldSortByDriver() {
            log("--- TEST: shouldSortByDriver ---");
            waitForRows();
            clickSortHeader("Driver");

            List<WebElement> rows = waitForRows();
            if (rows.size() >= 2) {
                String d1 = getCellText(0, 7);
                String d2 = getCellText(1, 7);
                log("Desc — [0]:'" + d1 + "' [1]:'" + d2 + "'");
                assertTrue(d1.compareToIgnoreCase(d2) >= 0,
                        "Expected desc: '" + d1 + "' < '" + d2 + "'");
            }
            log("PASS");
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // FILTERING
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Filtering tests")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class FilteringTests {

        @BeforeEach
        void setup() {
            loginAsAdmin();
            navigateToRideHistory();
            waitForLoadingToFinish();
        }

        @Test
        @DisplayName("Should filter rides by date range — all results within range")
        void shouldFilterByDateRange() {
            log("--- TEST: shouldFilterByDateRange ---");
            waitForRows();

            String fromDate = "2026-01-01";
            String toDate   = "2026-01-31";
            log("Filter: " + fromDate + " → " + toDate);

            By fromInput = By.cssSelector("app-ride-history-filter input[type='date']:first-of-type");
            By toInput   = By.cssSelector("app-ride-history-filter input[type='date']:last-of-type");

            wait.until(ExpectedConditions.visibilityOfElementLocated(fromInput));
            setDateInput(fromInput, fromDate);
            setDateInput(toInput, toDate);

            waitForFilterApplyBtn().click();
            waitForLoadingToFinish();

            wait.until(d -> {
                boolean empty = !d.findElements(By.cssSelector(".empty-state")).isEmpty();
                boolean hasRows = !d.findElements(By.cssSelector(".ride-table tbody tr")).isEmpty();
                return empty || hasRows;
            });

            boolean hasEmpty = !driver.findElements(By.cssSelector(".empty-state")).isEmpty();
            if (hasEmpty) {
                log("Empty state — no rides in January 2026");
                return;
            }

            List<WebElement> rows = waitForRows();
            LocalDate from = LocalDate.parse(fromDate);
            LocalDate to   = LocalDate.parse(toDate);
            log("Checking " + rows.size() + " rows...");
            for (int i = 0; i < rows.size(); i++) {
                LocalDate d = parseDate(getCellText(i, 0));
                log("  row[" + i + "]: " + d);
                assertFalse(d.isBefore(from), "Row " + i + ": " + d + " before " + from);
                assertFalse(d.isAfter(to),    "Row " + i + ": " + d + " after " + to);
            }
            log("PASS — " + rows.size() + " rows all in range");
        }

        @Test
        @DisplayName("Should show empty state for future date range")
        void shouldShowEmptyStateForNoResults() {
            log("--- TEST: shouldShowEmptyStateForNoResults ---");

            By fromInput = By.cssSelector("app-ride-history-filter input[type='date']:first-of-type");
            By toInput   = By.cssSelector("app-ride-history-filter input[type='date']:last-of-type");

            wait.until(ExpectedConditions.visibilityOfElementLocated(fromInput));
            setDateInput(fromInput, "2030-01-01");
            setDateInput(toInput,   "2030-12-31");
            log("Filter: 2030 (future)");

            waitForFilterApplyBtn().click();
            waitForLoadingToFinish();

            boolean result = false;
            long deadline  = System.currentTimeMillis() + 10_000;
            while (System.currentTimeMillis() < deadline) {
                boolean empty    = !driver.findElements(By.cssSelector(".empty-state")).isEmpty();
                boolean noRows   = driver.findElements(By.cssSelector(".ride-table tbody tr")).isEmpty();
                if (empty || noRows) { result = true; break; }
                try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            }
            log("Empty state or no rows: " + result);
            assertTrue(result, "Expected no results for year 2030");
            log("PASS");
        }

        @Test
        @DisplayName("Should clear filter and show all rides")
        void shouldClearFilter() {
            log("--- TEST: shouldClearFilter ---");
            waitForRows();

            By fromInput = By.cssSelector("app-ride-history-filter input[type='date']:first-of-type");
            By toInput   = By.cssSelector("app-ride-history-filter input[type='date']:last-of-type");

            wait.until(ExpectedConditions.visibilityOfElementLocated(fromInput));
            setDateInput(fromInput, "2025-01-01");
            setDateInput(toInput,   "2025-01-31");

            waitForFilterApplyBtn().click();
            waitForLoadingToFinish();
            log("Filter applied");

            waitForFilterClearBtn().click();
            waitForLoadingToFinish();
            log("Filter cleared");

            waitForTextIn(By.cssSelector(".view-indicator"), "Showing all rides");
            log("Indicator: '" + driver.findElement(By.cssSelector(".view-indicator")).getText() + "'");
            log("PASS");
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // SEARCH BY USER ID
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Search by user ID tests")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class SearchByUserTests {

        @BeforeEach
        void setup() {
            loginAsAdmin();
            navigateToRideHistory();
            waitForLoadingToFinish();
        }

        private boolean indicatorContainsUser1() {
            String txt = driver.findElement(By.cssSelector(".view-indicator")).getText();
            log("Indicator: '" + txt + "'");
            return txt.contains("User #1") || txt.contains("user #1")
                || txt.contains("User 1")  || txt.contains("user 1")
                || txt.contains("#1");
        }

        @Test
        @DisplayName("Should search rides by user ID")
        void shouldSearchByUserId() {
            log("--- TEST: shouldSearchByUserId ---");

            WebElement inp = waitClickable(By.cssSelector(".search-input-group input[type='number']"));
            inp.clear();
            inp.sendKeys("1");

            waitClickable(By.cssSelector(".btn-search")).click();
            waitForLoadingToFinish();

            wait.until(d -> indicatorContainsUser1());
            assertTrue(indicatorContainsUser1());
            log("PASS");
        }

        @Test
        @DisplayName("Should show all rides when clicking Show All")
        void shouldShowAllRides() {
            log("--- TEST: shouldShowAllRides ---");

            WebElement inp = waitClickable(By.cssSelector(".search-input-group input[type='number']"));
            inp.clear();
            inp.sendKeys("1");
            waitClickable(By.cssSelector(".btn-search")).click();
            waitForLoadingToFinish();
            log("Searched user 1");

            waitClickable(By.cssSelector(".btn-clear")).click();
            waitForLoadingToFinish();

            waitForTextIn(By.cssSelector(".view-indicator"), "Showing all rides");
            log("Indicator: '" + driver.findElement(By.cssSelector(".view-indicator")).getText() + "'");
            log("PASS");
        }

        @Test
        @DisplayName("Should search by pressing Enter in user ID field")
        void shouldSearchOnEnter() {
            log("--- TEST: shouldSearchOnEnter ---");

            WebElement inp = waitClickable(By.cssSelector(".search-input-group input[type='number']"));
            inp.clear();
            inp.sendKeys("1");
            inp.sendKeys(Keys.ENTER);
            waitForLoadingToFinish();

            wait.until(d -> indicatorContainsUser1());
            assertTrue(indicatorContainsUser1());
            log("PASS");
        }

        @Test
        @DisplayName("Should show error or empty state for non-existent user")
        void shouldShowErrorForNonExistentUser() {
            log("--- TEST: shouldShowErrorForNonExistentUser ---");

            WebElement inp = waitClickable(By.cssSelector(".search-input-group input[type='number']"));
            inp.clear();
            inp.sendKeys("99999");
            waitClickable(By.cssSelector(".btn-search")).click();
            waitForLoadingToFinish();

            wait.until(d -> {
                boolean err   = !d.findElements(By.cssSelector(".error-message")).isEmpty();
                boolean empty = !d.findElements(By.cssSelector(".empty-state")).isEmpty();
                boolean noRows = d.findElements(By.cssSelector(".ride-table tbody tr")).isEmpty();
                return err || empty || noRows;
            });

            boolean hasError   = !driver.findElements(By.cssSelector(".error-message")).isEmpty();
            boolean hasEmpty   = !driver.findElements(By.cssSelector(".empty-state")).isEmpty();
            boolean tableEmpty = driver.findElements(By.cssSelector(".ride-table tbody tr")).isEmpty();
            log("Error: " + hasError + ", Empty: " + hasEmpty + ", TableEmpty: " + tableEmpty);
            assertTrue(hasError || hasEmpty || tableEmpty);
            log("PASS");
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // TABLE CONTENT
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Table content tests")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class TableContentTests {

        @BeforeEach
        void setup() {
            loginAsAdmin();
            navigateToRideHistory();
            waitForLoadingToFinish();
        }

        @Test
        @DisplayName("Rides should display price with RSD suffix")
        void shouldDisplayPriceWithRSD() {
            log("--- TEST: shouldDisplayPriceWithRSD ---");
            waitForRows();
            String price = getCellText(0, 5);
            log("Price cell: '" + price + "'");
            assertTrue(price.contains("RSD"), "Expected RSD, got: " + price);
            log("PASS");
        }

        @Test
        @DisplayName("Rides should display distance with km suffix")
        void shouldDisplayDistanceWithKm() {
            log("--- TEST: shouldDisplayDistanceWithKm ---");
            waitForRows();
            String dist = getCellText(0, 6);
            log("Distance cell: '" + dist + "'");
            assertTrue(dist.contains("km"), "Expected km, got: " + dist);
            log("PASS");
        }

        @Test
        @DisplayName("Cancelled rides should have cancelled-row class and badge")
        void shouldHighlightCancelledRides() {
            log("--- TEST: shouldHighlightCancelledRides ---");
            waitForRows();
            List<WebElement> cancelledRows = driver.findElements(
                    By.cssSelector(".ride-table tbody tr.cancelled-row"));
            log("Cancelled rows: " + cancelledRows.size());
            for (int i = 0; i < cancelledRows.size(); i++) {
                WebElement badge = cancelledRows.get(i)
                        .findElement(By.cssSelector(".badge-cancelled"));
                log("  Row " + i + " badge: '" + badge.getText() + "'");
                assertTrue(badge.getText().contains("Yes"));
            }
            log("PASS");
        }

        @Test
        @DisplayName("PANIC rides should have panic-row class and badge")
        void shouldHighlightPanicRides() {
            log("--- TEST: shouldHighlightPanicRides ---");
            waitForRows();
            List<WebElement> panicRows = driver.findElements(
                    By.cssSelector(".ride-table tbody tr.panic-row"));
            log("Panic rows: " + panicRows.size());
            for (int i = 0; i < panicRows.size(); i++) {
                WebElement badge = panicRows.get(i)
                        .findElement(By.cssSelector(".badge-panic"));
                log("  Row " + i + " badge: '" + badge.getText() + "'");
                assertTrue(badge.getText().contains("Yes"));
            }
            log("PASS");
        }
    }
}