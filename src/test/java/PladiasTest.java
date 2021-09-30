import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.concurrent.*;
import javax.imageio.*;
import org.junit.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import com.opencsv.*;

import static org.junit.Assert.*;

public class PladiasTest {

    //aktuální datum
    static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    static DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    static DateTimeFormatter dtf3 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    //  String baseUrl = "http://192.168.3.241:9007/";
    static LocalDateTime now = LocalDateTime.now();
    //základní složka pro ukládání testů
    static String dirPath = "C:\\Java-Training\\Projects\\Pladias_testing\\Pladias_tests" + "_" + dtf.format(now) + "\\";
    //nastaveni pro vsechny testy obecne
    //deklarace ovladace, ktery ovlada prohlizec
    private static WebDriver driver;
    ChromeOptions MyChromeOptions;
    //zakladni adresa
    String baseUrl = "https://pladias.cz/";
    //základní složka pro stahování
    String downloadPath = "C:\\Java-Training\\Projects\\Pladias_testing\\selenium_downloads\\";

    @BeforeClass
    //nastaveni cesty k Chrome driver
    public static void setUp() {
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
    }

    @BeforeClass
    public static void createTestsDir() {
        //vytvoření složky pro testy
        boolean result;
        //vytvořím adresář pro testy
        File dir = new File(dirPath);
        //initialize File object and passing path as argument
        try {
            //create new file
            result = dir.mkdir(); //creates a new file

            if (result) {
                System.out.println("Directory created: " + dir.getCanonicalPath());
            } else {
                System.out.println("Directory already exist at location: " + dir.getCanonicalPath());
            }
        } catch (IOException e) {
            e.printStackTrace(); //prints exception if any
        }
    }

//    @BeforeClass
//    public static void pladiasLogin() {
//        //přihlášení se do černého Pladiasu
//        driver.get("https://pladias.ibot.cas.cz/login");
//        driver.findElement(By.id("inputEmail")).sendKeys("danmich@sci.muni.cz");
//        driver.findElement(By.id("inputPassword")).sendKeys("qwe753");
//        driver.findElement(By.xpath("//button[@type='submit']")).click();
//        System.out.println();
//        System.out.println("černý Pladias was logged: " + driver.getCurrentUrl());
//        System.out.println();
//
//        try {
//            driver.findElement(By.xpath("//*[text()='danmich@sci.muni.cz']"));
//        } catch (NoSuchElementException e) {
//            System.out.println("danmich@sci.muni.cz not logged");
//        }
//    }

    @AfterClass
    public static void tearDown() {
        driver.quit();
    }

    //metoda verifyLinkActive - ověří jeden odkaz
    public static void verifyLinkActive(String linkUrl) {

        //URL url;

        try {
            URL url = new URL(linkUrl);
            HttpURLConnection httpURLConnect = (HttpURLConnection) url.openConnection();
            //nastavim cekani 3 sekundy
            httpURLConnect.setConnectTimeout(3000);
            //pripojim se
            httpURLConnect.connect();
            //kdyz je stranka v poradku
            if (httpURLConnect.getResponseCode() == 200) {

                System.out.println(linkUrl + " - " +
                        httpURLConnect.getResponseMessage());
            }

            //kdyz stranka neni v poradku
            if (httpURLConnect.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                System.out.println(linkUrl + " - " + httpURLConnect.getResponseMessage()
                        + " - " + HttpURLConnection.HTTP_NOT_FOUND);
            }

            //assertTrue("Link is broken.", httpURLConnect.getResponseCode() == 200);
        } catch (Exception e) {
        }
    }

    @Before
    public void init() {
        // opens new Google Chrome window
        //nastavení vlastností prohlížeče - zákaz dialogu při stahování souborů,
        //složka pro stahování, stahování více souborů současně
        ChromeOptions options = new ChromeOptions();
        Map<String, Object> chromePrefs = new HashMap<String, Object>();
        chromePrefs.put("download.default_directory", downloadPath);
        chromePrefs.put("download.directory_upgrade", true);
        chromePrefs.put("download.prompt_for_download", false);
        chromePrefs.put("profile.content_settings.exceptions.automatic_downloads.*.setting", 1);
        chromePrefs.put("plugins.plugins_disabled", "Chrome PDF Viewer");
        chromePrefs.put("plugins.always_open_pdf_externally", true);
        chromePrefs.put("profile.default_content_settings.popups", 0);

        options.setExperimentalOption("prefs", chromePrefs);
//      driver = new ChromeDriver();

        driver = new ChromeDriver(options);
//        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);

        // maximize window
        driver.manage().window().maximize();

        //přihlášení se do černého Pladiasu
        driver.get("https://pladias.ibot.cas.cz/login");
        driver.findElement(By.id("inputEmail"));
        driver.findElement(By.id("inputEmail")).sendKeys("danmich@sci.muni.cz");
        driver.findElement(By.id("inputPassword")).sendKeys("qwe753");
        driver.findElement(By.xpath("//button[@type='submit']")).click();
        System.out.println();
        System.out.println("černý Pladias was logged: " + driver.getCurrentUrl());
        System.out.println();

        try {
            driver.findElement(By.xpath("//*[text()='danmich@sci.muni.cz']"));
        } catch (NoSuchElementException e) {
            System.out.println("danmich@sci.muni.cz not logged");
        }

    }

    @After
    public void pladiasLogout() {
        //odhlášení se z černého Pladiasu
        driver.get("https://pladias.ibot.cas.cz/login");
        String url = driver.findElement(By.xpath("//a[text()='Odhlásit se']")).getAttribute("href");
        driver.get(url);
        System.out.println();
        System.out.println("černý Pladias was logout: " + driver.getCurrentUrl());
        System.out.println();
    }

    @After
    public void closeDriver() {
        driver.close();
    }

    @After
    public void clearDownloadDirectory() {
        File downloadDirectory = new File(downloadPath);
        // Get all files in directory
        File[] files = downloadDirectory.listFiles();
        for (File file : files) {
            // Delete each file)
            if (!file.delete()) {

                // Failed to delete file
                System.out.println("Failed to delete: " + file);
            }
        }
    }

    @Test
    //Test of the main menu
    public void testMainMenu() {
        PrintStream console = System.out;
        String url;
        String expectedUrl;
        WebElement menuItem;
        LocalDateTime testStart = LocalDateTime.now();
        String testStartFormated = dtf3.format(testStart);

        writeTestResultToFile("testMainMenu.txt");

        System.out.println("Test of main menu: ");
        System.out.println("URL: https://pladias.cz/");
        System.out.println("Description: It tests if links in main menu work properly (tested links: Pladias logo, 'Druhy', " +
                "'Vegetace', 'Určování - Druhy', 'Určování - Vegetace', 'Ke stažení - Druhy a vlastnosti'," +
                "'Ke stažení - Fytogeografie a rozšíření druhů', 'Ke stažení - Vegetace', 'Ke stažení - Bibliografie'," +
                "'Kontakty', 'Přihlášení', flag - english version).");
        System.out.println("Test start: " + testStartFormated);
        System.out.println();
        System.out.println("Keywords to find errors: failed, incorrect, not found");
        System.out.println();

        driver.get(baseUrl);

        //find link to homepage

        try {
            //find link with PLADIAS logo
            menuItem = driver.findElement(By.xpath("//div[contains(@class,'menuLeft')]/a"));
            assertTrue("Link 'Pladias' is not displayed.", menuItem.isDisplayed());
            assertTrue("Link 'Pladias' is not enabled.", menuItem.isEnabled());

            url = menuItem.getAttribute("href");
            System.out.println("Link 'Domovská stránka': ");
            testPageInfo(url, baseUrl);

        } catch (NoSuchElementException e) {
            e.getStackTrace();
            System.out.println("Menu link 'Domovská stránka' was not found.");
        }

        //go back to the main page
        driver.get(baseUrl);

        try {
            //find link to Druhy and test it
            menuItem = driver.findElement(By.xpath("//li/a[contains(text(),'Druhy')]"));
            assertTrue("Link 'Druhy' in main menu is not displayed.", menuItem.isDisplayed());
            assertTrue("Link 'Druhy' in main menu is not enabled.", menuItem.isEnabled());
            url = menuItem.getAttribute("href");
            System.out.println("Link 'Druhy': ");
            expectedUrl = baseUrl + "taxon/";
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Menu link 'Druhy' was not found.");
        }

        //go back to the main page
        driver.get(baseUrl);

        //find link to Vegetace and test it
        try {
            menuItem = driver.findElement(By.linkText("Vegetace"));
            assertTrue("Link 'Vegetace' in main menu is not displayed.", menuItem.isDisplayed());
            assertTrue("Link 'Vegetace' in main menu is not enabled.", menuItem.isEnabled());
            url = menuItem.getAttribute("href");
            System.out.println("Link 'Vegetace': ");
            expectedUrl = baseUrl + "vegetation/";
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            e.getStackTrace();
            System.out.println("Menu link 'Vegetace' was not found.");
        }

        //go back to the main page
        driver.get(baseUrl);
        driver.findElement(By.xpath("//*[@id='navbarDropdown']")).click();

        //find dropdown menu item Určování - Druhy
        try {
            menuItem = driver.findElement(By.xpath("//div[contains(@class,'dropdown-menu')]" +
                    "[@aria-labelledby='navbarDropdown']/a[text()='Druhy']"));
            assertTrue("Link 'Určování - Druhy' in main menu is not displayed.", menuItem.isDisplayed());
            assertTrue("Link 'Určování - Druhy' in main menu is not enabled.", menuItem.isEnabled());
            url = menuItem.getAttribute("href");
            System.out.println("Link 'Určování - Druhy': ");
            expectedUrl = baseUrl + "plantkey/";
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            e.getStackTrace();
            System.out.println("Menu link 'Určování - Druhy' was not found.");
            System.out.println();
        }

        //go back to the main page
        driver.get(baseUrl);
        driver.findElement(By.xpath("//*[@id='navbarDropdown']")).click();

        //find dropdown menu item Určování - Vegetace
        try {
            menuItem = driver.findElement(By.xpath("//div[contains(@class,'dropdown-menu')]" +
                    "[@aria-labelledby='navbarDropdown']/a[text()='Vegetace']"));
            assertTrue("Link 'Určování - Vegetace' in main menu is not displayed.", menuItem.isDisplayed());
            assertTrue("Link 'Určování - Vegetace' in main menu is not enabled.", menuItem.isEnabled());
            url = menuItem.getAttribute("href");
            System.out.println("Link 'Určování - Vegetace': ");
            expectedUrl = baseUrl + "vegkey/";
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            e.getStackTrace();
            System.out.println("Menu link 'Určování - Vegetace' was not found.");
        }

        //go back to the main page
        driver.get(baseUrl);
        driver.findElement(By.xpath("//*[@id='navbarDropdown2']")).click();

        //find dropdown menu item Ke stažení - Druhy a vlastnosti
        try {
            menuItem = driver.findElement(By.xpath("//a[text()='Druhy a vlastnosti']"));
            assertTrue("Link 'Ke stažení - Druhy a vlastnosti' in main menu is not displayed.", menuItem.isDisplayed());
            assertTrue("Link 'Ke stažení - Druhy a vlastnosti' in main menu is not enabled.", menuItem.isEnabled());
            url = menuItem.getAttribute("href");
            System.out.println();
            System.out.println("Link 'Data - Druhy a vlastnosti': ");
            expectedUrl = baseUrl + "download/features";
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            e.getStackTrace();
            System.out.println("Menu link 'Data - Druhy a vlastnosti' was not found.");
        }

        //go back to the main page
        driver.get(baseUrl);
        driver.findElement(By.xpath("//*[@id='navbarDropdown2']")).click();

        //find dropdown menu item Ke stažení - fytogeografie a rozšíření druhů
        try {
            menuItem = driver.findElement(By.xpath("//a[contains(text(),'Fytogeografie')]"));
            assertTrue("Link 'Ke stažení - fytogeografie a rozšíření druhů' in main menu is not displayed.", menuItem.isDisplayed());
            assertTrue("Link 'Ke stažení - fytogeografie a rozšíření druhů' in main menu is not enabled.", menuItem.isEnabled());
            url = menuItem.getAttribute("href");
            System.out.println("Link 'Data - Fytogeografie a rozšíření druhů': ");
            expectedUrl = baseUrl + "download/phytogeography";
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            e.getStackTrace();
            System.out.println("Menu link 'Data - Fytogeografie a rozšíření druhů' was not found.");
        }

        //go back to the main page
        driver.get(baseUrl);
        driver.findElement(By.xpath("//*[@id='navbarDropdown2']")).click();

        //find dropdown menu item Ke stažení - Vegetace
        try {
            menuItem = driver.findElement(By.xpath("//a[text()='Vegetace'][@href='/download/vegetation']" +
                    "[contains(@href,'/download/vegetation')]"));
            assertTrue("Link 'Ke stažení - Vegetace' in main menu is not displayed.", menuItem.isDisplayed());
            assertTrue("Link 'Ke stažení - Vegetace' in main menu is not enabled.", menuItem.isEnabled());
            url = menuItem.getAttribute("href");
            System.out.println("Link 'Data - Vegetace': ");
            expectedUrl = baseUrl + "download/vegetation";
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            e.getStackTrace();
            System.out.println("Menu link 'Data - Vegetace' was not found.");
        }

        //go back to the main page
        driver.get(baseUrl);
        driver.findElement(By.xpath("//*[@id='navbarDropdown2']")).click();

        //find dropdown menu item Ke stažení - Bibliografie
        try {
            menuItem = driver.findElement(By.xpath("//a[text()='Bibliografie']"));
            assertTrue("Link 'Ke stažení - Bibliografie' in main menu is not displayed.", menuItem.isDisplayed());
            assertTrue("Link 'Ke stažení - Bibliografie' in main menu is not enabled.", menuItem.isEnabled());
            url = menuItem.getAttribute("href");
            System.out.println("Link 'Data - Bibliografie': ");
            expectedUrl = baseUrl + "download/bibliography";
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            e.getStackTrace();
            System.out.println("Menu link 'Data - Bibliografie' was not found.");
        }

        //go back to the main page
        driver.get(baseUrl);

        //find link to Kontakty and test it
        try {
            menuItem = driver.findElement(By.xpath("//a[text()='Kontakty']"));
            assertTrue("Link 'Kontakty' in main menu is not displayed.", menuItem.isDisplayed());
            assertTrue("Link 'Kontakty' in main menu is not enabled.", menuItem.isEnabled());
            url = menuItem.getAttribute("href");
            System.out.println("Link 'Kontakty': ");
            expectedUrl = baseUrl + "homepage/contact";
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            e.getStackTrace();
            System.out.println("Menu link 'Kontakty' was not found.");
        }

        //go back to the main page
        driver.get(baseUrl);

        //find link to Přihlášení and test it
        try {
            menuItem = driver.findElement(By.xpath("//a[text()=' Přihlášení']"));
            assertTrue("Link 'Přihlášení' in main menu is not displayed.", menuItem.isDisplayed());
            assertTrue("Link 'Přihlášení' in main menu is not enabled.", menuItem.isEnabled());
            url = menuItem.getAttribute("href");
            System.out.println("Link 'Přihlášení': ");
            expectedUrl = "https://pladias.ibot.cas.cz/";
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            e.getStackTrace();
            System.out.println("Menu link 'Přihlášení' was not found.");
        }

        //go back to the main page
        driver.get(baseUrl);

        //find link to eng version and test it
        try {
            menuItem = driver.findElement(By.xpath("//a[@id='cz']"));
            assertTrue("Link 'Anglická verze' in main menu is not displayed.", menuItem.isDisplayed());
            assertTrue("Link 'Anglická verze' in main menu is not enabled.", menuItem.isEnabled());
            url = menuItem.getAttribute("href");
            System.out.println("Link 'Anglická verze': ");
            expectedUrl = baseUrl + "en/";
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            e.getStackTrace();
            System.out.println("Menu link 'Anglická verze' was not found.");
        }

        //go back to the main page
        driver.get(baseUrl);

        //info about test (e.g. duration)
        getTestInfo(testStart);

        // Use stored value for output stream
        System.setOut(console);
        //System.out.println("This will be written on the console!");
    }

    @Test
    public void testHomepage() {
        PrintStream console = System.out;
        String url;
        String expectedUrl;
        WebElement link;
        LocalDateTime testStart = LocalDateTime.now();
        String testStartFormated = dtf3.format(testStart);

        writeTestResultToFile("testHomepage.txt");

        System.out.println("Test of homepage:");
        System.out.println("URL: https://pladias.cz/");
        System.out.println("Description It tests if the links on the homepage page ('Druhy','Určování','Vegetace','Ke stažení') work properly.");
        System.out.println("Test start: " + testStartFormated);
        System.out.println();
        System.out.println("Keywords to find errors: not found, failed, incorrect");
        System.out.println();

        driver.get(baseUrl);
        //Druhy
        try {
            link = driver.findElement(By.xpath("//a[contains(@class,'pieceA')]"));
            assertTrue("Link 'Druhy' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Druhy' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "taxon/";
            System.out.println("Link 'Druhy': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Druhy' was not found.");
        }

        //go back to the main page
        driver.get(baseUrl);

        //Určování druhů
        //původně byly odkazy na určování druhů a vegetace
//        try {
//            link = driver.findElement(By.linkText("druhů"));
//            url = link.getAttribute("href");
//            expectedUrl = baseUrl + "plantkey/";
//            System.out.println("Link Určování druhů: ");
//            testPageInfo(url, expectedUrl);
//        } catch (NoSuchElementException e) {
//            System.out.println("Link Určování druhů was not found.");
//        }

        //Určování vegetačních jednotek
        //původně byly odkazy na určování druhů a vegetace
//        try {
//            link = driver.findElement(By.xpath("//a[text()='vegetačních jednotek']"));
//            url = link.getAttribute("href");
//            expectedUrl = baseUrl + "vegkey/";
//            System.out.println("Link Určování vegetačních jednotek: ");
//            testPageInfo(url, expectedUrl);
//        } catch (NoSuchElementException e) {
//            System.out.println("Link Určování vegetačních jednotek was not found.");
//        }

        //Určování

        try {
            link = driver.findElement(By.xpath("//a[contains(@class,'pieceB')]"));
            assertTrue("Link 'Určování' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Určování' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "plantkey/links";
            System.out.println("Link 'Určování': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Určování' was not found.");
        }

        //go back to the main page
        driver.get(baseUrl);

        //Vegetace
        try {
            link = driver.findElement(By.xpath("//a[contains(@class,'pieceC')]"));
            assertTrue("Link 'Vegetace' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Vegetace' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "vegetation/";
            System.out.println("Link 'Vegetace': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Vegetace' was not found.");
        }

        //go back to the main page
        driver.get(baseUrl);

        //Ke stažení
        try {
            link = driver.findElement(By.xpath("//a[contains(@class,'pieceD')]"));
            assertTrue("Link 'Ke stažení' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Ke stažení' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "download/";
            System.out.println("Link 'Ke stažení': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Ke stažení' was not found.");
        }

        //info about test (eg. duration)
        getTestInfo(testStart);

        // Use stored value for output stream
        System.setOut(console);
        //System.out.println("This will be written on the console!");

    }

    @Test
    public void testTaxonSearch() throws InterruptedException {
        PrintStream console = System.out;
        WebElement element;
        ArrayList<String> textToSendCorrect = new ArrayList<String>();
        ArrayList<String> textToSendIncorrect = new ArrayList<String>();
        String url;
        String expectedUrl = baseUrl + "taxon/overview/Lamium%20album";
        WebElement notFoundMessage;
        LocalDateTime testStart = LocalDateTime.now();
        String testStartFormated = dtf3.format(testStart);

        writeTestResultToFile("testTaxonSearch.txt");

        System.out.println("Test of taxon search: ");
        System.out.println("URL: https://pladias.cz/taxon/");
        System.out.println("Description: It test how searching works if correct/incorrect text is sent via search form." +
                " As an example of correct taxon Lamium album was used.");
        System.out.println("Test start: " + testStartFormated);
        System.out.println();
        System.out.println("Keywords to find errors: not found, failed, incorrect, not displayed");
        System.out.println();

        //text to send correct
        textToSendCorrect.add("Lamium album");
        textToSendCorrect.add("LAMIUM ALBUM");
        textToSendCorrect.add("lamium album");
        textToSendCorrect.add(" lamium album");
        textToSendCorrect.add("lamium album ");
        textToSendCorrect.add(" lamium album ");
        textToSendCorrect.add(" lamium album ");
        //textToSendCorrect.add(" lamium   album ");
        textToSendCorrect.add("hluchavka bílá");
        textToSendCorrect.add("hluchavka bila");

        //text to send incorrect
        textToSendIncorrect.add("chyba");
        textToSendIncorrect.add("černý bez");
        textToSendIncorrect.add("fialka");
        textToSendIncorrect.add("brambora");
        textToSendIncorrect.add("123");
        textToSendIncorrect.add("-123");
        textToSendIncorrect.add("1.0");
        textToSendIncorrect.add("-1.0");

        //get pocka na nacteni stranky, prejdu na stranku s druhy
        driver.get(baseUrl + "taxon/");
        //na načtené stránce najdi pole pro zadavani taxonu

        //odesílá správné texty
        try {
            for (String text : textToSendCorrect) {
                element = driver.findElement(By.id("taxon-autocomplete"));
                assertTrue("Search field is not displayed.", element.isDisplayed());
                assertTrue("Search field is not enabled.", element.isEnabled());
                //zadej jmeno taxonu
                element.sendKeys(text);
                //Potvrd vyber pomoci Enter
                System.out.println("Tested text: " + text);
                element.sendKeys(Keys.ENTER);
                url = driver.getCurrentUrl();
                testPageInfo(url, expectedUrl);

                driver.get(baseUrl + "taxon/");
            }
        } catch (NoSuchElementException e) {
            System.out.println("Search field was not found.");
        }

        //get pocka na nacteni stranky, prejdu na stranku s druhy
        driver.get(baseUrl + "taxon/");

        try {
            //odesílá nesprávné texty
            for (String text : textToSendIncorrect) {
                element = driver.findElement(By.id("taxon-autocomplete"));
                assertTrue("Search field is not displayed.", element.isDisplayed());
                assertTrue("Search field is not enabled.", element.isEnabled());
                //zadej jmeno taxonu
                System.out.println("Tested text: " + text);
                element.sendKeys(text);
                //Potvrd vyber pomoci Enter
                element.sendKeys(Keys.ENTER);
                notFoundMessage = driver.findElement(By.xpath("//*[text()='Neznámý taxon']"));
                if (notFoundMessage.isDisplayed()) {
                    System.out.println("Page with 'Neznámý taxon' message was displayed.");
                } else {
                    System.out.println("Page with 'Neznámý taxon' message was not displayed.");
                }

                System.out.println();
                assertTrue("Page with 'Neznámý taxon' message was not displayed.",
                        notFoundMessage.isDisplayed());

                driver.get(baseUrl + "taxon/");
            }

        } catch (NoSuchElementException e) {
            System.out.println("Search field was not found.");
        }

        //info about test (e.g. duration)
        getTestInfo(testStart);

        // Use stored value for output stream
        System.setOut(console);
        //System.out.println("This will be written on the console!");
    }

    @Test
    public void testSyntaxonSearch() throws InterruptedException {
        PrintStream console = System.out;
        String actualUrl;
        WebElement element;
        ArrayList<String> textToSendCorrect = new ArrayList<String>();
        ArrayList<String> textToSendIncorrect = new ArrayList<String>();
        String url;
        String expectedUrl = baseUrl + "vegetation/overview/Iso%C3%ABtetum%20lacustris";
        WebElement notFoundMessage;
        LocalDateTime testStart = LocalDateTime.now();
        String testStartFormated = dtf3.format(testStart);

        writeTestResultToFile("testSyntaxonSearch.txt");

        System.out.println("Test of syntaxon search: ");
        System.out.println("URL: https://pladias.cz/vegetation/");
        System.out.println("Description: It tests how searching works if correct/incorrect text is sent via search form. As " +
                "an example of correct syntaxon Isoëtetum lacustris was used.");
        System.out.println("Test start: " + testStartFormated);
        System.out.println();
        System.out.println("Keywords to find errors: failed, incorrect, not displayed, not found");
        System.out.println();

        //text to send correct
        textToSendCorrect.add("Isoëtetum lacustris");
//        textToSendCorrect.add("Isoëtetum LACUSTRIS");
//        textToSendCorrect.add("isoëtetum lacustris");
//        textToSendCorrect.add(" Isoëtetum lacustris");
//        textToSendCorrect.add("Isoëtetum lacustris ");
//        textToSendCorrect.add(" Isoëtetum lacustris ");
//        textToSendCorrect.add("  Isoëtetum lacustris  ");
//        textToSendCorrect.add("Isoetetum lacustris");
//      textToSendCorrect.add("Isoëtetum  lacustris");

        //text to send incorrect
        textToSendIncorrect.add("chyba");
        textToSendIncorrect.add("černý les");
        textToSendIncorrect.add("123");
        textToSendIncorrect.add("-123");
        textToSendIncorrect.add("1.0");
        textToSendIncorrect.add("-1.0");

        //get pocka na nacteni stranky, prejdu na stranku s druhy
        driver.get(baseUrl + "vegetation/");
        //na načtené stránce najdi pole pro zadavani taxonu

        //odesílá správné texty
        try {
            for (String text : textToSendCorrect) {
                element = driver.findElement(By.id("input_syntaxon"));
                assertTrue("Search field is not displayed.", element.isDisplayed());
                assertTrue("Search field is not enabled.", element.isEnabled());
                //zadej jmeno syntaxonu
                element.sendKeys(text);
//                System.out.println();
                System.out.println("Tested text: " + text);
                //Potvrd vyber pomoci Enter
                element.sendKeys(Keys.ENTER);
                url = driver.getCurrentUrl();
                testPageInfo(url, expectedUrl);

                driver.get(baseUrl + "vegetation/");
            }
        } catch (NoSuchElementException e) {
            System.out.println("Search field was not found.");
        }

        //get pocka na nacteni stranky, prejdu na stranku s druhy
        driver.get(baseUrl + "vegetation/");

        try {
            //odesílá nesprávné texty
            for (String text : textToSendIncorrect) {
                element = driver.findElement(By.id("input_syntaxon"));
                assertTrue("Search field is not displayed.", element.isDisplayed());
                assertTrue("Search field is not enabled.", element.isEnabled());
                //zadej jmeno text
                System.out.println();
                System.out.println("Tested text: " + text);
                element.sendKeys(text);
                //Potvrd vyber pomoci Enter
                element.sendKeys(Keys.ENTER);
                notFoundMessage = driver.findElement(By.xpath("//*[text()='Neznámý syntaxon']"));
                if (notFoundMessage.isDisplayed()) {
                    System.out.println("Page with 'Neznámý sytaxon' message was displayed.");
                } else {
                    System.out.println("Page with 'Neznámý sytaxon' message was not displayed.");
                }

                assertTrue("Page with 'Neznámý sytaxon' message was not displayed.",
                        notFoundMessage.isDisplayed());

                driver.get(baseUrl + "vegetation/");
            }

        } catch (NoSuchElementException e) {
            System.out.println("Search field was not found.");
        }

        System.out.println();

        //info about test (e.g. duration)
        getTestInfo(testStart);

        // Use stored value for output stream
        System.setOut(console);
        //System.out.println("This will be written on the console!");
    }

    @Test
    public void testDeterminationLinks() {
        PrintStream console = System.out;
        WebElement element;
        String url;
        String expectedUrl;
        LocalDateTime testStart = LocalDateTime.now();
        String testStartFormated = dtf3.format(testStart);

        writeTestResultToFile("testDeterminationLinks.txt");

        System.out.println("Test of determination links:");
        System.out.println("URL: https://pladias.cz/plantkey/links");
        System.out.println("Description: It tests if links 'Určování druhů' a 'Určování vegetačních jednotek.'");
        System.out.println("Test start: " + testStartFormated);
        System.out.println();
        System.out.println("Keywords to find errors: failed, incorrect, not found");
        System.out.println();

        driver.get(baseUrl + "plantkey/links");

        //link 'Určování druhů'
        System.out.println("Link 'Určování druhů': ");
        expectedUrl = baseUrl + "plantkey/";
        try {
            element = driver.findElement(By.xpath("//*[text()='Určování druhů']/ancestor::a"));
            assertTrue("Link 'Určování druhů' is not displayed.", element.isDisplayed());
            assertTrue("Link 'Určování druhů' is not enabled.", element.isEnabled());
            if (element.isDisplayed()) {
                element.click();
                url = driver.getCurrentUrl();
                testPageInfo(url, expectedUrl);
            }
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Určování druhů' was not foud.");
        }

        driver.get(baseUrl + "plantkey/links");

        //link 'Určování vegetačních jednotek'
        System.out.println("Link 'Určování vegetačních jednotek': ");
        expectedUrl = baseUrl + "vegkey/";
        try {
            element = driver.findElement(By.xpath("//*[text()='Určování vegetačních jednotek']/ancestor::a"));
            assertTrue("Link 'Určování vegetačních jednotek' is not displayed.", element.isDisplayed());
            assertTrue("Link 'Určování vegetačních jednotek' is not enabled.", element.isEnabled());
            if (element.isDisplayed()) {
                element.click();
                url = driver.getCurrentUrl();
                testPageInfo(url, expectedUrl);
            }
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Určování vegetačních jednotek' was not foud.");
        }

        //info about test (e.g. duration)
        getTestInfo(testStart);

        // Use stored value for output stream
        System.setOut(console);
        //System.out.println("This will be written on the console!");
    }

    //TODO: připravit metodu na testování vzorových taxonů
    @Test
    public void testTaxonProfile() throws InterruptedException {
        String taxon = "Viola mirabilis";
        int taxonId = 5120;
        PrintStream console = System.out;
        String url;
        String expectedUrl;
        WebElement link;
        WebElement sectionTitle;
        WebElement button;
        WebElement legendItem;
        File latestFile;
        String fileName;
        String expectedFileName;

        LocalDateTime testStart = LocalDateTime.now();
        String testStartFormated = dtf3.format(testStart);

        writeTestResultToFile("testTaxonProfile.txt");

        System.out.println("Test of taxon profile: ");
        System.out.println("URL: https://pladias.cz/taxon/overview/Viola%20mirabilis");
        System.out.println("Description: It tests links and downoads in taxon profile. As an example Viola mirabilis was used.");
        System.out.println("Test start: " + testStartFormated);
        System.out.println();
        System.out.println("Keywords to find errors: not found, failed, incorrect, invalid");
        System.out.println();
        System.out.println("Tested taxa: " + taxon + ", id: " + taxonId);
        System.out.println();

        driver.get(baseUrl + "taxon/overview/" + taxon);

        //nové hledání
        try {
            link = driver.findElement(By.linkText("nové hledání"));
            assertTrue("Link 'nové hledání' is not displayed.", link.isDisplayed());
            assertTrue("Link 'nové hledání' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "taxon/";
            System.out.println("Button link 'nové hledání': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Button link 'nové hledání' was not found.");
        }

        driver.get(baseUrl + "taxon/overview/" + taxon);

        //Přehled
        try {
            link = driver.findElement(By.linkText("Přehled"));
            assertTrue("Tab 'Přehled' is not displayed.", link.isDisplayed());
            assertTrue("Tab 'Přehled' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "taxon/overview/" + taxon.replace(" ", "%20").replace("ë", "%C3%AB");
            System.out.println("Link 'Přehled': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Přehled' was not found.");
        }

        //Vlastnosti
        try {
            link = driver.findElement(By.linkText("Vlastnosti"));
            assertTrue("Tab 'Vlastnosti' is not displayed.", link.isDisplayed());
            assertTrue("Tab 'Vlastnosti' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "taxon/data/" + taxon.replace(" ", "%20");
            System.out.println("Link 'Vlastnosti': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Vlastnosti' was not found.");
        }

        //Rozšíření
        try {
            link = driver.findElement(By.linkText("Rozšíření"));
            assertTrue("Tab 'Rozšíření' is not displayed.", link.isDisplayed());
            assertTrue("Tab 'Rozšíření' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "taxon/distribution/" + taxon.replace(" ", "%20");
            System.out.println("Link 'Rozšíření': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Rozšíření' was not found.");
        }

        //Obrázky
        try {
            link = driver.findElement(By.linkText("Obrázky"));
            assertTrue("Tab 'Obrázky' is not displayed.", link.isDisplayed());
            assertTrue("Tab 'Obrázky' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "taxon/pictures/" + taxon.replace(" ", "%20");
            System.out.println("Link 'Obrázky': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Obrázky' was not found.");
        }

        //Květena ČR
        try {
            link = driver.findElement(By.linkText("Květena ČR"));
            assertTrue("Tab 'Květena' is not displayed.", link.isDisplayed());
            assertTrue("Tab 'Květena' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "taxon/flora/" + taxon.replace(" ", "%20");
            System.out.println("Link 'Květena ČR': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Květena ČR' was not found.");
        }

        //Nomenklatura
        try {
            link = driver.findElement(By.linkText("Nomenklatura"));
            assertTrue("Tab 'Nomenklatura' is not displayed.", link.isDisplayed());
            assertTrue("Tab 'Nomenklatura' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "taxon/nomenclature/" + taxon.replace(" ", "%20");
            System.out.println("Link 'Nomenklatura': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Nomenklatura' was not found.");
        }

        //Přehled - detail
        driver.get(baseUrl + "taxon/overview/" + taxon);

        System.out.println("Tab 'Přehled' - detail: ");
        System.out.println();

        //Přehled - odkaz na hlavní obrázek
        try {
            link = driver.findElement(By.xpath("//a[contains(@class,'mainPicture')]"));
            assertTrue("Link 'Přehled - hlavní obrázek' is not displayed.", link.isDisplayed());
            assertTrue("Tab 'Přehled - hlavní obrázek' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "taxon/pictures/" + taxon.replace(" ", "%20") + "#image1";
            System.out.println("Link 'Přehled - Hlavní obrázek': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Přehled - Hlavní obrázek' was not found.");
        }

        driver.get(baseUrl + "taxon/overview/" + taxon);

        //Přehled - odkaz na další obrázek (jen první)
        try {
            link = driver.findElement(By.xpath("//div[@class='picture']/a"));
            assertTrue("Link 'Přehled - další obrázek' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Přehled - další obrázek' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "taxon/pictures/" + taxon.replace(" ", "%20") + "#image2";
            System.out.println("Link 'Přehled - Další obrázek': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Přehled - Další obrázek' was not found.");
        }

        //Přehled - odkaz na mapku
        driver.get(baseUrl + "taxon/overview/" + taxon);

        try {
            link = driver.findElement(By.xpath("//div[contains(@class,'col-md-7')]/a"));
            assertTrue("Link 'Přehled - Mapka' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Přehled - Mapka' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "taxon/distribution/" + taxon.replace(" ", "%20");
            System.out.println("Link 'Přehled - Mapka': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Přehled - Mapka' was not found.");
        }

        driver.get(baseUrl + "taxon/overview/" + taxon);

        //Přehled - odkaz na Vlastnosti - Habitus a typ růstu
        try {
            link = driver.findElement(By.xpath("//a[contains(@class,'dataButton')][text()='Habitus a typ růstu']"));
            assertTrue("Link 'Přehled - Vlastnosti - Habitus a typ růstu' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Přehled - Vlastnosti - Habitus a typ růstu' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "taxon/data/" + taxon.replace(" ", "%20") + "#10";
            System.out.println("Link 'Přehled - Vlastnosti - Habitus a typ růstu': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Přehled - Vlastnosti - Habitus a typ růstu' was not found.");
        }

        driver.get(baseUrl + "taxon/overview/" + taxon);

        //Přehled - odkaz na Vlastnosti - List
        try {
            link = driver.findElement(By.xpath("//a[contains(@class,'dataButton')][text()='List']"));
            assertTrue("Link 'Přehled - Vlastnosti - List' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Přehled - Vlastnosti - List' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "taxon/data/" + taxon.replace(" ", "%20") + "#1";
            System.out.println("Link 'Přehled - Vlastnosti - List': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Přehled - Vlastnosti - List' was not found.");
        }

        driver.get(baseUrl + "taxon/overview/" + taxon);

        //Přehled - odkaz na Vlastnosti - Květ
        try {
            link = driver.findElement(By.xpath("//a[contains(@class,'dataButton')][text()='Květ']"));
            assertTrue("Link 'Přehled - Vlastnosti - Květ' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Přehled - Vlastnosti - Květ' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "taxon/data/" + taxon.replace(" ", "%20") + "#3";
            System.out.println("Link 'Přehled - Vlastnosti - Květ': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Přehled - Vlastnosti - Květ' was not found.");
        }

        driver.get(baseUrl + "taxon/overview/" + taxon);

        //Přehled - odkaz na Vlastnosti - Plod, semeno a šíření
        try {
            link = driver.findElement(By.xpath("//a[contains(@class,'dataButton')][text()='Plod, semeno a šíření']"));
            assertTrue("Link 'Přehled - Vlastnosti - Plod, semeno a šíření' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Přehled - Vlastnosti - Plod, semeno a šíření' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "taxon/data/" + taxon.replace(" ", "%20") + "#2";
            System.out.println("Link 'Přehled - Vlastnosti - Plod, semeno a šíření': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Přehled - Vlastnosti - Plod, semeno a šíření' was not found.");
        }

        driver.get(baseUrl + "taxon/overview/" + taxon);

        //Přehled - odkaz na Vlastnosti - Podzemní orgány a klonalita
        try {
            link = driver.findElement(By.xpath("//a[contains(@class,'dataButton')][text()='Podzemní orgány a klonalita']"));
            assertTrue("Link 'Přehled - Vlastnosti - Podzemní orgány a klonalita' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Přehled - Vlastnosti - Podzemní orgány a klonalita' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "taxon/data/" + taxon.replace(" ", "%20") + "#15";
            System.out.println("Link 'Přehled - Vlastnosti - Podzemní orgány a klonalita': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Přehled - Vlastnosti - Podzemní orgány a klonalita' was not found.");
        }

        driver.get(baseUrl + "taxon/overview/" + taxon);

        //Přehled - odkaz na Vlastnosti - Způsob výživy
        try {
            link = driver.findElement(By.xpath("//a[contains(@class,'dataButton')][text()='Způsob výživy']"));
            assertTrue("Link 'Přehled - Vlastnosti - Způsob výživy' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Přehled - Vlastnosti - Způsob výživy' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "taxon/data/" + taxon.replace(" ", "%20") + "#67";
            System.out.println("Link 'Přehled - Vlastnosti - Způsob výživy': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Přehled - Vlastnosti - Způsob výživy' was not found.");
        }

        driver.get(baseUrl + "taxon/overview/" + taxon);

        //Přehled - odkaz na Vlastnosti - Karyologie
        try {
            link = driver.findElement(By.xpath("//a[contains(@class,'dataButton')][text()='Karyologie']"));
            assertTrue("Link 'Přehled - Vlastnosti - Karyologie' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Přehled - Vlastnosti - Karyologie' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "taxon/data/" + taxon.replace(" ", "%20") + "#12";
            System.out.println("Link 'Přehled - Vlastnosti - Karyologie': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Přehled - Vlastnosti - Karyologie' was not found.");
        }

        driver.get(baseUrl + "taxon/overview/" + taxon);

        //Přehled - odkaz na Vlastnosti - Původ taxonu
        try {
            link = driver.findElement(By.xpath("//a[contains(@class,'dataButton')][text()='Původ taxonu']"));
            assertTrue("Link 'Přehled - Vlastnosti - Původ taxonu' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Přehled - Vlastnosti - Původ taxonu' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "taxon/data/" + taxon.replace(" ", "%20") + "#13";
            System.out.println("Link 'Přehled - Vlastnosti - Původ taxonu': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Přehled - Vlastnosti - Původ taxonu' was not found.");
        }

        driver.get(baseUrl + "taxon/overview/" + taxon);

        //Přehled - odkaz na Vlastnosti - Ekologické indikační hodnoty
        try {
            link = driver.findElement(By.xpath("//a[contains(@class,'dataButton')][text()='Ekologické indikační hodnoty']"));
            assertTrue("Link 'Přehled - Vlastnosti - Ekologické indikační hodnoty' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Přehled - Vlastnosti - Ekologické indikační hodnoty' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "taxon/data/" + taxon.replace(" ", "%20") + "#4";
            System.out.println("Link 'Přehled - Vlastnosti - Ekologické indikační hodnoty': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Přehled - Vlastnosti - Ekologické indikační hodnoty' was not found.");
        }

        driver.get(baseUrl + "taxon/overview/" + taxon);

        //Přehled - odkaz na Vlastnosti - Stanoviště a sociologie
        try {
            link = driver.findElement(By.xpath("//a[contains(@class,'dataButton')][text()='Stanoviště a sociologie']"));
            assertTrue("Link 'Přehled - Vlastnosti - Stanoviště a sociologie' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Přehled - Vlastnosti - Stanoviště a sociologie' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "taxon/data/" + taxon.replace(" ", "%20") + "#7";
            System.out.println("Link 'Přehled - Vlastnosti - Stanoviště a sociologie': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Přehled - Vlastnosti - Stanoviště a sociologie' was not found.");
        }

        driver.get(baseUrl + "taxon/overview/" + taxon);

        //Přehled - odkaz na Vlastnosti - Rozšíření a hojnost
        try {
            link = driver.findElement(By.xpath("//a[contains(@class,'dataButton')][text()='Rozšíření a hojnost']"));
            assertTrue("Link 'Přehled - Vlastnosti - Rozšíření a hojnost' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Přehled - Vlastnosti - Rozšíření a hojnost' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "taxon/data/" + taxon.replace(" ", "%20") + "#61";
            System.out.println("Link 'Přehled - Vlastnosti - Rozšíření a hojnost': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Přehled - Vlastnosti - Rozšíření a hojnost' was not found.");
        }

        driver.get(baseUrl + "taxon/overview/" + taxon);

        //Přehled - odkaz na Vlastnosti - Ohrožení a ochrana
        try {
            link = driver.findElement(By.xpath("//a[contains(@class,'dataButton')][text()='Ohrožení a ochrana']"));
            assertTrue("Link 'Přehled - Vlastnosti - Ohrožení a ochrana' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Přehled - Vlastnosti - Ohrožení a ochrana' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "taxon/data/" + taxon.replace(" ", "%20") + "#62";
            System.out.println("Link 'Přehled - Vlastnosti - Ohrožení a ochrana': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Přehled - Vlastnosti - Ohrožení a ochrana' was not found.");
        }

        driver.get(baseUrl + "taxon/overview/" + taxon);

        //tlačítko na stažení pdf - souhrn
        driver.get(baseUrl + "taxon/overview/" + taxon);
        link = driver.findElement(By.xpath("//a[contains(text(),'souhrn')]"));
        assertTrue("Button 'souhrn' is not displayed.", link.isDisplayed());
        assertTrue("Button 'souhrn' is not enabled.", link.isEnabled());
        url = link.getAttribute("href");
        System.out.println("Download pdf 'souhrn': ");
        System.out.println("url: " + url);

        //Download the file
        driver.get(url);

        //wait 10 seconds
        Thread.sleep(10000);

        latestFile = getLatestFilefromDir(downloadPath);
        fileName = latestFile.getName();
        System.out.println("name: " + fileName);
        if (fileName.equals(taxonId + ".pdf")) {
            System.out.println("File '" + taxon + " - souhrn' download - OK.");
            System.out.println();
        } else {
            System.out.println("File '" + taxon + " - souhrn' download - failed.");
            System.out.println();
        }

        assertTrue("Downloaded file name is not matching with expected file name.",
                fileName.equals(taxonId + ".pdf"));

        //tlačítko na stažení pdf - souhrn
//        try {
//            link = driver.findElement(By.xpath("//*[contains(text(),'souhrn')]"));
//            url = link.getAttribute("href");
//            expectedUrl = baseUrl + "factsheet/default/" + taxonId;
//            if (url.equals(expectedUrl)) {
//                System.out.println("Button link 'Přehled - souhrn' - OK");
//                System.out.println();
//            } else {
//                System.out.println("Button link 'Přehled - souhrn' is invalid.");
//            }
//            assertEquals("Button link 'Přehled - souhrn' is invalid.", expectedUrl, url);
//
//        } catch (NoSuchElementException e) {
//            System.out.println("Link 'Vegetace ČR' was not found.");
//        }

        //Vlastnosti - detail
        driver.get(baseUrl + "taxon/data/" + taxon);

        System.out.println("Tab 'Vlastnosti': ");
        System.out.println();

        //Vlastnosti - Vše
        //otestovat všechny sekce?
        try {
            link = driver.findElement(By.xpath("//a[contains(@class,'dataButton')][text()='Vše']"));
            assertTrue("Link 'Vlastnosti - vše' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Vlastnosti - vše' is not enabled.", link.isEnabled());
            link.click();
            sectionTitle = driver.findElement(By.xpath("//h3[@class='mb-0'][text()='Habitus a typ růstu']"));
            if (sectionTitle.isDisplayed()) {
                System.out.println("Link 'Vlastnosti - Vše' - OK");
            } else {
                System.out.println("Link 'Vlastnosti - Vše' is invalid");
            }

            assertTrue("Link 'Vlastnosti - Vše' is invalid", sectionTitle.isDisplayed());
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Vlastnosti - Vše' was not found.");
        }

        driver.get(baseUrl + "taxon/data/" + taxon);

        //Vlastnosti - Habitus a typ růstu

        try {
            link = driver.findElement(By.xpath("//a[contains(@class,'dataButton')][text()='Habitus a typ růstu']"));
            link.click();
            sectionTitle = driver.findElement(By.xpath("//h3[@class='mb-0'][text()='Habitus a typ růstu']"));
            if (sectionTitle.isDisplayed()) {
                System.out.println("Link 'Vlastnosti - Habitus a typ růstu' - OK");
            } else {
                System.out.println("Link 'Vlastnosti - Habitus a typ růstu' is invalid");
            }

            assertTrue("Link 'Vlastnosti - Habitus a typ růstu' is invalid", sectionTitle.isDisplayed());
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Vlastnosti - Habitus a typ růstu' was not found.");
        }

        driver.get(baseUrl + "taxon/data/" + taxon);

        //Vlastnosti - List

        try {
            link = driver.findElement(By.xpath("//a[contains(@class,'dataButton')][text()='List']"));
            assertTrue("Link 'Vlastnosti - List' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Vlastnosti - List' is not enabled.", link.isEnabled());
            link.click();
            sectionTitle = driver.findElement(By.xpath("//h3[@class='mb-0'][text()='List']"));
            if (sectionTitle.isDisplayed()) {
                System.out.println("Link 'Vlastnosti - List' - OK");
            } else {
                System.out.println("Link 'Vlastnosti - List' is invalid");
            }

            assertTrue("Link 'Vlastnosti - List' is invalid", sectionTitle.isDisplayed());
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Vlastnosti - List' was not found.");
        }

        //Vlastnosti - Květ

        try {
            link = driver.findElement(By.xpath("//a[contains(@class,'dataButton')][text()='Květ']"));
            assertTrue("Link 'Vlastnosti - Květ' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Vlastnosti - Květ' is not enabled.", link.isEnabled());
            link.click();
            sectionTitle = driver.findElement(By.xpath("//h3[@class='mb-0'][text()='Květ']"));
            if (sectionTitle.isDisplayed()) {
                System.out.println("Link 'Vlastnosti - Květ' - OK");
            } else {
                System.out.println("Link 'Vlastnosti - Květ' is invalid");
            }

            assertTrue("Link 'Vlastnosti - Květ' is invalid", sectionTitle.isDisplayed());
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Vlastnosti - Květ' was not found.");
        }

        //Vlastnosti - Plod, semeno a šíření

        try {
            link = driver.findElement(By.xpath("//a[contains(@class,'dataButton')][text()='Plod, semeno a šíření']"));
            assertTrue("Link 'Vlastnosti - Plod, semeno a šíření' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Vlastnosti - Plod, semeno a šíření' is not enabled.", link.isEnabled());
            link.click();
            sectionTitle = driver.findElement(By.xpath("//h3[@class='mb-0'][text()='Plod, semeno a šíření']"));
            if (sectionTitle.isDisplayed()) {
                System.out.println("Link 'Vlastnosti - Plod, semeno a šíření' - OK");
            } else {
                System.out.println("Link 'Vlastnosti - Plod, semeno a šíření' is invalid");
            }

            assertTrue("Link 'Vlastnosti - Plod, semeno a šíření' is invalid", sectionTitle.isDisplayed());
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Vlastnosti - Plod, semeno a šíření' was not found.");
        }

        //Vlastnosti - Podzemní orgány a klonalita

        try {
            link = driver.findElement(By.xpath("//a[contains(@class,'dataButton')][text()='Podzemní orgány a klonalita']"));
            assertTrue("Link 'Vlastnosti - Podzemní orgány a klonalita' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Vlastnosti - Podzemní orgány a klonalita' is not enabled.", link.isEnabled());
            link.click();
            sectionTitle = driver.findElement(By.xpath("//h3[@class='mb-0'][text()='Podzemní orgány a klonalita']"));
            if (sectionTitle.isDisplayed()) {
                System.out.println("Link 'Vlastnosti - Podzemní orgány a klonalita' - OK");
            } else {
                System.out.println("Link 'Vlastnosti - Podzemní orgány a klonalita' is invalid");
            }

            assertTrue("Link 'Vlastnosti - Podzemní orgány a klonalita' is invalid", sectionTitle.isDisplayed());
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Vlastnosti - Podzemní orgány a klonalita' was not found.");
        }

        //Vlastnosti - Způsob výživy

        try {
            link = driver.findElement(By.xpath("//a[contains(@class,'dataButton')][text()='Způsob výživy']"));
            assertTrue("Link 'Vlastnosti - Způsob výživy' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Vlastnosti - Způsob výživy' is not enabled.", link.isEnabled());
            link.click();
            sectionTitle = driver.findElement(By.xpath("//h3[@class='mb-0'][text()='Způsob výživy']"));
            if (sectionTitle.isDisplayed()) {
                System.out.println("Link 'Vlastnosti - Způsob výživy' - OK");
            } else {
                System.out.println("Link 'Vlastnosti - Způsob výživy' is invalid");
            }

            assertTrue("Link 'Vlastnosti - Způsob výživy' is invalid", sectionTitle.isDisplayed());
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Vlastnosti - Způsob výživy' was not found.");
        }

        //Vlastnosti - Karyologie

        try {
            link = driver.findElement(By.xpath("//a[contains(@class,'dataButton')][text()='Karyologie']"));
            assertTrue("Link 'Vlastnosti - Karyologie' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Vlastnosti - Karyologie' is not enabled.", link.isEnabled());
            link.click();
            sectionTitle = driver.findElement(By.xpath("//h3[@class='mb-0'][text()='Karyologie']"));
            if (sectionTitle.isDisplayed()) {
                System.out.println("Link 'Vlastnosti - Karyologie' - OK");
            } else {
                System.out.println("Link 'Vlastnosti - Karyologie' is invalid");
            }

            assertTrue("Link 'Vlastnosti - Karyologie' is invalid", sectionTitle.isDisplayed());
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Vlastnosti - Karyologie' was not found.");
        }

        //Vlastnosti - Původ taxonu

        try {
            link = driver.findElement(By.xpath("//a[contains(@class,'dataButton')][text()='Původ taxonu']"));
            assertTrue("Link 'Vlastnosti - Původ taxonu' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Vlastnosti - Původ taxonu' is not enabled.", link.isEnabled());
            link.click();
            sectionTitle = driver.findElement(By.xpath("//h3[@class='mb-0'][text()='Původ taxonu']"));
            if (sectionTitle.isDisplayed()) {
                System.out.println("Link 'Vlastnosti - Původ taxonu' - OK");
            } else {
                System.out.println("Link 'Vlastnosti - Původ taxonu' is invalid");
            }

            assertTrue("Link 'Vlastnosti - Původ taxonu' is invalid", sectionTitle.isDisplayed());
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Vlastnosti - Původ taxonu' was not found.");
        }

        //Vlastnosti - Ekologické indikační hodnoty

        try {
            link = driver.findElement(By.xpath("//a[contains(@class,'dataButton')][text()='Ekologické indikační hodnoty']"));
            assertTrue("Link 'Vlastnosti - Ekologické indikační hodnoty' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Vlastnosti - Ekologické indikační hodnoty' is not enabled.", link.isEnabled());
            link.click();
            sectionTitle = driver.findElement(By.xpath("//h3[@class='mb-0'][text()='Ekologické indikační hodnoty']"));
            if (sectionTitle.isDisplayed()) {
                System.out.println("Link 'Vlastnosti - Ekologické indikační hodnoty' - OK");
            } else {
                System.out.println("Link 'Vlastnosti - Ekologické indikační hodnoty' is invalid");
            }

            assertTrue("Link 'Vlastnosti - Ekologické indikační hodnoty' is invalid", sectionTitle.isDisplayed());
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Vlastnosti - Ekologické indikační hodnoty' was not found.");
        }

        //Vlastnosti - Stanoviště a sociologie

        try {
            link = driver.findElement(By.xpath("//a[contains(@class,'dataButton')][text()='Stanoviště a sociologie']"));
            assertTrue("Link 'Vlastnosti - Stanoviště a sociologie' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Vlastnosti - Stanoviště a sociologie' is not enabled.", link.isEnabled());
            link.click();
            sectionTitle = driver.findElement(By.xpath("//h3[@class='mb-0'][text()='Stanoviště a sociologie']"));
            if (sectionTitle.isDisplayed()) {
                System.out.println("Link 'Vlastnosti - Stanoviště a sociologie' - OK");
            } else {
                System.out.println("Link 'Vlastnosti - Stanoviště a sociologie' is invalid");
            }

            assertTrue("Link 'Vlastnosti - Stanoviště a sociologie' is invalid", sectionTitle.isDisplayed());
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Vlastnosti - Stanoviště a sociologie' was not found.");
        }

        //Vlastnosti - Rožšíření a hojnost

        try {
            link = driver.findElement(By.xpath("//a[contains(@class,'dataButton')][text()='Rozšíření a hojnost']"));
            assertTrue("Link 'Vlastnosti - Rozšíření a hojnost' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Vlastnosti - Rozšíření a hojnost' is not enabled.", link.isEnabled());
            link.click();
            sectionTitle = driver.findElement(By.xpath("//h3[@class='mb-0'][text()='Rozšíření a hojnost']"));
            if (sectionTitle.isDisplayed()) {
                System.out.println("Link 'Vlastnosti - Rozšíření a hojnost' - OK");
            } else {
                System.out.println("Link 'Vlastnosti - Rozšíření a hojnost' is invalid");
            }

            assertTrue("Link 'Vlastnosti - Rozšíření a hojnost' is invalid", sectionTitle.isDisplayed());
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Vlastnosti - Rozšíření a hojnost' was not found.");
        }

        //Vlastnosti - Ohrožení a ochrana

        try {
            link = driver.findElement(By.xpath("//a[contains(@class,'dataButton')][text()='Ohrožení a ochrana']"));
            assertTrue("Link 'Vlastnosti - Ohrožení a ochrana' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Vlastnosti - Ohrožení a ochrana' is not enabled.", link.isEnabled());
            link.click();
            sectionTitle = driver.findElement(By.xpath("//h3[@class='mb-0'][text()='Ohrožení a ochrana']"));
            if (sectionTitle.isDisplayed()) {
                System.out.println("Link 'Vlastnosti - Ohrožení a ochrana' - OK");
            } else {
                System.out.println("Link 'Vlastnosti - Ohrožení a ochrana' is invalid");
            }

            assertTrue("Link 'Vlastnosti - Ohrožení a ochrana' is invalid", sectionTitle.isDisplayed());
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Vlastnosti - Ohrožení a ochrana' was not found.");
        }

        //Rozšíření - detail
        driver.get(baseUrl + "taxon/distribution/" + taxon);

        System.out.println();
        System.out.println("Tab 'Rozšíření': ");
        System.out.println();
        try {
            legendItem = driver.findElement(By.xpath("//*[text()='revidovaný údaj']"));
            if (legendItem.isDisplayed()) {
                System.out.println("'Rozšíření - Legenda' - default - OK");
            } else {
                System.out.println("'Rozšíření - Legenda' - default - is invalid");
            }

            assertTrue("'Rozšíření - Legenda' - default - is invalid", legendItem.isDisplayed());
        } catch (NoSuchElementException e) {
            System.out.println("'Rozšíření - Legenda - revidovaný údaj' item was not found.");
        }

        //tlačítko - přepnout na mapu revizí
        try {
            button = driver.findElement(By.xpath("//button[text()='přepnout na mapu revizí']"));
            assertTrue("Button 'Přepnout na mapu revizí' is not displayed.", button.isDisplayed());
            assertTrue("Link 'Přepnout na mapu revizí' is not enabled.", button.isEnabled());
            button.click();
        } catch (NoSuchElementException e) {
            System.out.println("'Rozšíření - 'přepnout na mapu revizí' button was not found.");
        }

        try {
            legendItem = driver.findElement(By.xpath("//*[text()='věrohodný údaj']"));
            if (legendItem.isDisplayed()) {
                System.out.println("'Rozšíření - Legenda - mapa revizí' - OK");
            } else {
                System.out.println("'Rozšíření - Legenda - mapa revizí' - is invalid");
            }

            assertTrue("'Rozšíření - Legenda - mapa revizí' - is invalid", legendItem.isDisplayed());
        } catch (NoSuchElementException e) {
            System.out.println("'Rozšíření - Legenda - věrohodný údaj' item was not found.");
        }

        System.out.println();
        // Store the current window handle
        String parentWindow = driver.getWindowHandle();

        //tlačítko - údaje k mapě - otevře se v novém okně, uživatel musí být přihlášen
        System.out.println("Buton link 'Rozšíření - údaje k mapě'.");
        try {
            button = driver.findElement(By.xpath("//button[contains(text(),'údaje k mapě')]"));
            assertTrue("Button 'údaje k mapě' is not displayed.", button.isDisplayed());
            assertTrue("Button 'údaje k mapě' is not enabled.", button.isEnabled());
            button.click();
        } catch (NoSuchElementException e) {
            System.out.println("Button link 'Rozšíření - údaje k mapě' was not found.");
        }

        Set<String> handles = driver.getWindowHandles();
        for (String windowHandle : handles) {
            if (!windowHandle.equals(parentWindow)) {
                driver.switchTo().window(windowHandle);

                url = driver.getCurrentUrl();
                testPageInfo(url, "https://pladias.ibot.cas.cz/atlas/map?taxonId=" + taxonId);

                driver.close(); //closing child window
                driver.switchTo().window(parentWindow); //cntrl to parent window
            }
        }

        //tlačítko - fytokartografické systézy
        System.out.println("Button link 'fytokartografické syntézy'");
        try {
            link = driver.findElement(By.xpath("//a[contains(@href,'syntezy')]"));
            assertTrue("Button 'fytokartografické syntézy' is not displayed.", link.isDisplayed());
            assertTrue("Link 'fytokartografické syntézy' is not enabled.", link.isEnabled());
//          link.click();
            url = link.getAttribute("href");
            driver.get(url);
        } catch (NoSuchElementException e) {
            System.out.println("Button link 'Rozšíření - fytokartografické syntézy' was not found.");
        }

        //wait 10 seconds
        Thread.sleep(10000);

        latestFile = getLatestFilefromDir(downloadPath);
        fileName = latestFile.getName();
        System.out.println("name: " + fileName);

        expectedFileName = taxon + ".pdf";

        if (fileName.equals(expectedFileName)) {
            System.out.println("File download - OK.");
            System.out.println();
        } else {
            System.out.println("File download - failed.");
            System.out.println();
        }

        assertTrue("Downloaded file name is not matching with expected file name.",
                fileName.equals(expectedFileName));

        //pokud se tlačítko - fytokartografické systémy otevře v novém okně
//        handles = driver.getWindowHandles();
//        for (String windowHandle : handles) {
//            if (!windowHandle.equals(parentWindow)) {
//                driver.switchTo().window(windowHandle);
//
//                url = driver.getCurrentUrl();
//                if (url.equals(baseUrl + "downloads/images/syntezy/" + taxon.replace(" ", "%20") + ".pdf")) {
//                    System.out.println("'Rozšíření - fytokartografické syntézy' - pdf href - OK");
//                } else {
//                    System.out.println("'Rozšíření - fytokartografické syntézy' - pdf href - is invalid");
//                }
//
//                driver.close(); //closing child window
//                driver.switchTo().window(parentWindow); //cntrl to parent window
//            }
//        }

        //link 'pravidla pro užití dat'
        try {
            link = driver.findElement(By.xpath("//a[text()='Pravidla pro užití dat']"));
            assertTrue("Link 'pravidla pro užití dat' is not displayed.", link.isDisplayed());
            assertTrue("Link 'pravidla pro užití dat' is not enabled.", link.isEnabled());
            link.click();
            url = driver.getCurrentUrl();
            testPageInfo(url, baseUrl + "homepage/rules");
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Rozšíření - Pravidla pro užití dat' was not found.");
        }

        //Tab 'Květena ČR'
        driver.get(baseUrl + "taxon/flora/" + taxon);

        System.out.println("Tab 'Květena ČR': ");
        System.out.println();

        try {
            link = driver.findElement(By.xpath("//button[contains(text(),'stáhnout')]/ancestor::a"));
            assertTrue("Link 'Květena ČR - stáhnout' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Květena ČR - stáhnout' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            //Download the file
            driver.get(url);
            //wait 5 seconds
            Thread.sleep(5000);

            latestFile = getLatestFilefromDir(downloadPath);
            fileName = latestFile.getName();

            //opakovaně přidávám čekám 10 sekund, dokud se soubor nestáhne
            while (fileName.endsWith(".crdownload")) {
                Thread.sleep(10000);
                latestFile = getLatestFilefromDir(downloadPath);
                fileName = latestFile.getName();
            }

            expectedFileName = taxon.substring(0, taxon.indexOf(" ")) + ".pdf";
            latestFile = getLatestFilefromDir(downloadPath);
            fileName = latestFile.getName();
            System.out.println("name: " + fileName);

            if (fileName.equals(expectedFileName)) {
                System.out.println("File '" + taxon + " - Květena ČR' download - OK.");
                System.out.println();
            } else {
                System.out.println("File '" + taxon + " - Květena ČR' download - failed.");
                System.out.println();
            }

            assertTrue("Downloaded file name is not matching with expected file name.",
                    fileName.equals(expectedFileName));

//            if (url.equals(expectedUrl)) {
//                System.out.println("Button 'Květena ČR - stáhnout': - OK");
//            } else {
//                System.out.println("Button 'Květena ČR - stáhnout: - failed");
//            }
//            assertEquals("Button 'Květena ČR - stáhnout' was not found.", expectedUrl, url);

        } catch (NoSuchElementException e) {
            System.out.println("Button 'Květena ČR - stáhnout' was not found.");
        }

        driver.get(baseUrl + "taxon/flora/" + taxon);

        System.out.println();

        //info about test (e.g. duration)
        getTestInfo(testStart);

        // Use stored value for output stream
        System.setOut(console);
        //System.out.println("This will be written on the console!");
    }

    //TODO: připravit metodu na testování vzorových syntaxonů
    @Test
    public void testSyntaxonProfile() throws InterruptedException {
        String syntaxon = "Isoëtetum lacustris";
        int syntaxonId = 560;
        PrintStream console = System.out;
        WebElement link;
        String url;
        String expectedUrl;
        File latestFile;
        String fileName;
        String expectedFileName;
        LocalDateTime testStart = LocalDateTime.now();
        String testStartFormated = dtf3.format(testStart);

        writeTestResultToFile("testSyntaxonProfile.txt");
        //vytvořím textový soubor pro výstup testu

        System.out.println("Test of syntaxon profile: ");
        System.out.println("URL: https://pladias.cz/vegetation/overview/Iso%C3%ABtetum%20lacustris");
        System.out.println("Description: It tests links and downoads in syntaxon profile. As an example " +
                "Isoëtetum lacustris was used.");
        System.out.println("Test start: " + testStartFormated);
        System.out.println();

        driver.get(baseUrl + "vegetation/overview/" + syntaxon);
        System.out.println("Keywords to find errors: not found, failed, incorrect, invalid");
        System.out.println();
        System.out.println("Tested syntaxa: " + syntaxon + ", id: " + syntaxonId);
        System.out.println();

        //nové hledání
        try {
            link = driver.findElement(By.linkText("nové hledání"));
            assertTrue("Link 'nové hledání' is not displayed.", link.isDisplayed());
            assertTrue("Link 'nové hledání' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "vegetation/";
            System.out.println("Button link 'nové hledání': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'nové hledání' was not found.");
        }

        driver.get(baseUrl + "vegetation/overview/" + syntaxon);

        //Přehled
        try {
            link = driver.findElement(By.linkText("Přehled"));
            assertTrue("Link 'Přehled' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Přehled' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "vegetation/overview/" +
                    syntaxon.replace(" ", "%20").replace("ë", "%C3%AB");
            System.out.println("Link 'Přehled': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Přehled' was not found.");
        }

        //Nomenklatura
        try {
            link = driver.findElement(By.linkText("Nomenklatura"));
            assertTrue("Link 'Nomenklatura' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Nomenklatura' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "vegetation/nomenclature/" +
                    syntaxon.replace(" ", "%20").replace("ë", "%C3%AB");
            System.out.println("Link 'Nomenklatura': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Nomenklatura' was not found.");
        }

        //Druhy
        try {
            link = driver.findElement(By.xpath("//nav[contains(@class,'nav-pills')]/a[text()='Druhy']"));
            assertTrue("Link 'Druhy' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Druhy' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "vegetation/species/" +
                    syntaxon.replace(" ", "%20").replace("ë", "%C3%AB");
            System.out.println("Link 'Druhy': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Druhy' was not found.");
        }

        //Popis
        try {
            link = driver.findElement(By.linkText("Popis"));
            assertTrue("Link 'Popis' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Popis' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "vegetation/description/" +
                    syntaxon.replace(" ", "%20").replace("ë", "%C3%AB");
            System.out.println("Link 'Popis': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Popis' was not found.");
        }

        //Rozšíření
        try {
            link = driver.findElement(By.linkText("Rozšíření"));
            assertTrue("Link 'Rozšíření' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Rozšíření' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "vegetation/distribution/" +
                    syntaxon.replace(" ", "%20").replace("ë", "%C3%AB");
            System.out.println("Link 'Rozšíření': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Rozšíření' was not found.");
        }

        //Obrázky
        try {
            link = driver.findElement(By.linkText("Obrázky"));
            assertTrue("Link 'Obrázky' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Obrázky' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "vegetation/pictures/" +
                    syntaxon.replace(" ", "%20").replace("ë", "%C3%AB");
            System.out.println("Link 'Obrázky': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Obrázky' was not found.");
        }

        //Přehled - detail
        driver.get(baseUrl + "vegetation/overview/" + syntaxon);

        System.out.println("Tab 'Přehled' - detail: ");
        System.out.println();

        //Přehled - odkaz na hlavní obrázek
        try {
            link = driver.findElement(By.xpath("//a[contains(@class,'mainPicture')]"));
            assertTrue("Link 'Přehled - Hlavní obrázek' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Přehled - Hlavní obrázek' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "vegetation/pictures/" +
                    syntaxon.replace(" ", "%20").replace("ë", "%C3%AB") + "#image1";
            System.out.println("Link 'Přehled - Hlavní obrázek': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Přehled - Hlavní obrázek' was not found.");
        }

        driver.get(baseUrl + "vegetation/overview/" + syntaxon);

        //Přehled - odkaz na další obrázek (jen první)
        try {
            link = driver.findElement(By.xpath("//div[@class='picture']/a"));
            assertTrue("Link 'Přehled - Další obrázek' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Přehled - Další obrázek' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "vegetation/pictures/" +
                    syntaxon.replace(" ", "%20").replace("ë", "%C3%AB") + "#image2";
            System.out.println("Link 'Přehled - Další obrázek': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Přehled - Další obrázek' was not found.");
        }

        driver.get(baseUrl + "vegetation/overview/" + syntaxon);

        //Přehled - odkaz na mapku
        try {
            link = driver.findElement(By.xpath("//img[contains(@src,'vegetace/maps')]/ancestor::a"));
            assertTrue("Link 'Přehled - Mapka' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Přehled - Mapka' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "vegetation/distribution/" +
                    syntaxon.replace(" ", "%20").replace("ë", "%C3%AB");
            System.out.println("Link 'Přehled - Mapka': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Přehled - Mapka' was not found.");
        }

        driver.get(baseUrl + "vegetation/overview/" + syntaxon);

        System.out.println("'Přehled - Charakteristika': ");
        System.out.println();
        //Přehled - odkaz na Charakteristika - Diagnostické, konstantní a dominantní taxony
        try {
            link = driver.findElement(By.xpath("//a[contains(@class,'dataButton')]" +
                    "[text()='Diagnostické, konstantní a dominantní taxony']"));
            assertTrue("Link 'Přehled - Charakteristika - Diagnostické, konstantní a dominantní taxony' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Přehled - Charakteristika - Diagnostické, konstantní a dominantní taxony' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "vegetation/species/" +
                    syntaxon.replace(" ", "%20").replace("ë", "%C3%AB");
            System.out.println("Link 'Přehled - Charakteristika - Diagnostické, konstantní " +
                    "a dominantní taxony': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Přehled - Charakteristika - Diagnostické, konstantní " +
                    "a dominantní taxony' was not found.");
        }

        driver.get(baseUrl + "vegetation/overview/" + syntaxon);

        //Přehled - odkaz na Charakteristika - Popis
        try {
            link = driver.findElement(By.xpath("//a[contains(@class,'dataButton')]" +
                    "[text()='Popis']"));
            assertTrue("Link 'Přehled - Charakteristika - Popis' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Přehled - Charakteristika - Popis' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "vegetation/description/" +
                    syntaxon.replace(" ", "%20").replace("ë", "%C3%AB");
            System.out.println("Link 'Přehled - Charakteristika - Popis': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Přehled - Charakteristika - Popis' was not found.");
        }

        driver.get(baseUrl + "vegetation/overview/" + syntaxon);

        //Přehled - odkaz na Charakteristika - Popis
        try {
            link = driver.findElement(By.xpath("//a[contains(@class,'dataButton')]" +
                    "[text()='Rozšíření']"));
            assertTrue("Link 'Přehled - Charakteristika - Rozšíření' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Přehled - Charakteristika - Rozšíření' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedUrl = baseUrl + "vegetation/distribution/" +
                    syntaxon.replace(" ", "%20").replace("ë", "%C3%AB");
            System.out.println("Link 'Přehled - Charakteristika - Rozšíření': ");
            testPageInfo(url, expectedUrl);
        } catch (NoSuchElementException e) {
            System.out.println("Link 'Přehled - Charakteristika - Rozšíření' was not found.");
        }

        driver.get(baseUrl + "vegetation/overview/" + syntaxon);

        //tlačítko na stažení pdf - Vegetace ČR
        try {
            link = driver.findElement(By.xpath("//*[contains(text(),'Vegetace ČR')]"));
            assertTrue("Link 'Vegetace ČR' is not displayed.", link.isDisplayed());
            assertTrue("Link 'Vegetace ČR' is not enabled.", link.isEnabled());
            url = link.getAttribute("href");
            expectedFileName = "syntaxon_" + syntaxonId + ".pdf";

            driver.get(url);

            //wait 10 seconds
            Thread.sleep(10000);

            latestFile = getLatestFilefromDir(downloadPath);
            fileName = latestFile.getName();
            System.out.println("name: " + fileName);

            if (fileName.equals(expectedFileName)) {
                System.out.println("File '" + syntaxon + " - Vegetace ČR' download - OK.");
                System.out.println();
            } else {
                System.out.println("File '" + syntaxon + " - Vegetace ČR' download - failed.");
                System.out.println();
            }

            assertTrue("Downloaded file name is not matching with expected file name.",
                    fileName.equals(expectedFileName));

//            if (url.equals(expectedUrl)) {
//                System.out.println("Button link 'Vegetace ČR' - OK");
//            } else {
//                System.out.println("Button link 'Vegetace ČR' is invalid.");
//            }
//            assertEquals("Button link 'Vegetace ČR' is invalid.", expectedUrl, url);

        } catch (NoSuchElementException e) {
            System.out.println("Link 'Vegetace ČR' was not found.");
        }

        //info about test (e.g. duration)
        getTestInfo(testStart);

        // Use stored value for output stream
        System.setOut(console);
        //System.out.println("This will be written on the console!");

    }

    @Test
    //zjisteni rozbitych odkazu

    public void verifyLinksMorePages() {
        CSVReader reader = null;
        String csvFile = "pages.csv";
//      String csvFile = "pages2.csv";
        PrintStream console = System.out;
        String[] line = null;
        //strany, ktere se maji zkontrolovat
        ArrayList<String> pages = new ArrayList<String>();
        String pageUrl = null;
        LocalDateTime testStart = LocalDateTime.now();
        String testStartFormated = dtf3.format(testStart);

        writeTestResultToFile("verifyLinksMorePages.txt");

        System.out.println("Test of broken links: ");
        System.out.println("Description: It tests all links on pages defined in file pages.csv.");
        System.out.println("Test start: " + testStartFormated);
        System.out.println();
        System.out.println("Keywords to find errors: 404, not found");
        System.out.println();

        System.out.println("Tested pages: ");
        //nacte variabilni cast adresy z csv souboru
        try {
            reader = new CSVReader(new FileReader(csvFile));

            while ((line = reader.readNext()) != null) {
                System.out.println(line[0]);
                pages.add(line[0]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //cyklicky projdu stranky
        for (String i : pages) {
            pageUrl = baseUrl + i;
            //metoda na overeni odkazu na jedne strance
            verifyLinks(pageUrl);
        }

        System.out.println();
        //info about test (e.g. duration)
        getTestInfo(testStart);

// Use stored value for output stream
        System.setOut(console);
        //System.out.println("This will be written on the console!");
    }

    @Test
    //zjisteni rozbitych obrazku na vice strankach

    //taxony: proměnná basePicturesURL: https://pladias.cz/taxon/pictures/, proměnná csvFile: taxa.csv
    //syntaxony: proměnná basePicturesURL: https://pladias.cz/vegetation/pictures/,
    // proměnná csvFile: syntaxa.csv
    //syntaxony - mapka: proměnná basePicturesURL: https://pladias.cz/vegetation/distribution/, proměnná csvFile: syntaxa.csv

    //taxony s uvozovkami musi mit uvozovky jednoduche

    //Test rozbitých obrázků taxonů
    public void verifyImagesTaxa() {
        PrintStream console = System.out;
        writeTestResultToFile("verifyImagesTaxa.txt");
        System.out.println("Test of broken images in taxon - gallery:");
        System.out.println("URL: e.g. https://pladias.cz/taxon/pictures/Abies%20alba");
        System.out.println("Description: It tests whether in taxon gallery are not missing images.");

        verifyImagesMorePages("taxon/pictures/", "taxa.csv");
//        verifyImagesMorePages("taxon/pictures/", "taxa2.csv");
        // Use stored value for output stream
        System.setOut(console);
        //System.out.println("This will be written on the console!")
    }

    @Test
    //Test rozbitých obrázků syntaxonů
    public void verifyImagesSyntaxa() {
        PrintStream console = System.out;

        writeTestResultToFile("verifyImagesSyntaxa.txt");
        System.out.println("Test of broken images in syntaxon - gallery:");
        System.out.println("URL: e.g. https://pladias.cz/vegetation/pictures/Iso%C3%ABtetum%20lacustris");
        System.out.println("Description: It tests whether in syntaxon gallery are not missing images.");

        verifyImagesMorePages("vegetation/pictures/", "syntaxa.csv");
//        verifyImagesMorePages("vegetation/pictures/", "syntaxa2.csv");

        // Use stored value for output stream
        System.setOut(console);
        //System.out.println("This will be written on the console!");
    }

    @Test
    //Test rozbitých mapek u syntaxonů
    public void verifyImagesSyntaxaMap() {

        PrintStream console = System.out;

        writeTestResultToFile("verifyImagesSyntaxaMap.txt");
        System.out.println("Test of broken images in vegetation - distribution:");
        System.out.println("URL: e.g. https://pladias.cz/vegetation/distribution/Iso%C3%ABtetum%20lacustris");
        System.out.println("Description: It tests whether in vegetation - distribution is not missing map.");

        verifyImagesMorePages("vegetation/distribution/", "syntaxa.csv");
//        verifyImagesMorePages("vegetation/distribution/", "syntaxa2.csv");

        // Use stored value for output stream
        System.setOut(console);
        //System.out.println("This will be written on the console!");
    }

    //nemá hledanou záložku (např. distribution u syntaxonů)
    public void verifyImagesMorePages(String urlConstantPart, String csvFile) {
        //staticka cast adresy
        //String basePicturesURL = baseUrl + "taxon/pictures/";
        //String basePicturesURL = baseUrl + "vegetation/pictures/";
        //String basePicturesURL = baseUrl + "vegetation/distribution/";
        String basePicturesURL = baseUrl + urlConstantPart;
        //String csvFile = "syntaxa2.csv";
        //String csvFile;
        String picturesURL;
        CSVReader reader = null;
        String[] line = null;
        ArrayList<String> taxa = new ArrayList<String>();
        LocalDateTime testStart = LocalDateTime.now();
        String testStartFormated = dtf3.format(testStart);

//        System.out.println("Test of broken images: ");
        System.out.println("test start: " + testStartFormated);
        System.out.println();
        System.out.println("Keywords to find errors: invalid, broken");
        System.out.println();

        //nacte variabilni cast adresy z csv souboru
        try {
            reader = new CSVReader(new FileReader(csvFile));

            System.out.println("Tested taxa/syntaxa: ");
            while ((line = reader.readNext()) != null) {
                System.out.println(line[0]);
                taxa.add(line[0]);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //seznam taxonu
//        taxa.add("Abies alba");
//        taxa.add("Abies cephalonica");
//        taxa.add("Abies concolor");
//        taxa.add("Abies grandis");
//        taxa.add("Abies homolepis");
//        taxa.add("Abies nordmanniana");

        System.out.println();
        System.out.println("Tested pages: ");

        if (taxa != null) {
            for (String i : taxa) {
                picturesURL = basePicturesURL + i;
                //System.out.println(picturesURL);
                verifyImages(picturesURL);
            }
        }

        System.out.println();

        //info about test (e.g. duration)
        getTestInfo(testStart);

    }

    //zjisteni rozbitych obrazku na jedne strance
    public void verifyImages(String picturesURL) {
        List<WebElement> images;

        //jdi na stranku
        driver.get(picturesURL);
        if (picturesURL.replace(" ", "%20").replace("ë", "%C3%AB").
//        if (URLEncodeDecode.encode(picturesURL).
        equals(driver.getCurrentUrl())) {
            System.out.println("url " + picturesURL + " is valid");
            //najdi vsechny obrazky na jedne strance

//            System.out.println("expected url: " + picturesURL + ", actual url: " +
//                    driver.getCurrentUrl());
        } else {
            System.out.println("url " + picturesURL + " is probably invalid");
//            System.out.println("expected url: " + picturesURL.replace(" ","%20")
//                    + ", actual url: " + driver.getCurrentUrl());
//            System.out.println("expected url: " + com.cooltrickshome.URLEncodeDecode.encode(picturesURL)
//                    + ", actual url: " + driver.getCurrentUrl());
        }

        //vybrat jen obrázky, které lze stáhnout (fotky, kresby, mapky)
        images = driver.findElements(By.xpath("//img[contains(@src,'downloads')]"));

        if (images.size() == 0) {
            System.out.println("Images not found.");
        }

//        for (WebElement image : driver.findElements(By.xpath("//img[contains(@src,'downloads')]"))) {

        for (WebElement image : images) {
            isImageBroken(image);
        }

    }

    //metoda na zjisteni jednoho rozbiteho obrazku
    public void isImageBroken(WebElement image) {
        if (image.getAttribute("naturalWidth").equals("0")) {
            System.out.println(image.getAttribute("outerHTML") + "is broken");
        } else {
            System.out.println(image.getAttribute("outerHTML") + " - OK");
        }
        assertTrue("Image is broken.", !image.getAttribute("naturalWidth").equals("0"));
    }

    @Test
    //zjisteni rozbitych odkazu na kveteny

    public void verifyLinkFloraPdfMorePages() {
        //staticka cast adresy
        String baseLinkURL = baseUrl + "taxon/flora/";
        String csvFile = "taxa.csv";
//        String csvFile = "taxa2.csv";
        PrintStream console = System.out;
        String linkURL;
        CSVReader reader = null;
        String[] line = null;
        ArrayList<String> taxa = new ArrayList<String>();
        LocalDateTime testStart = LocalDateTime.now();
        String testStartFormated = dtf3.format(testStart);

        writeTestResultToFile("verifyLinkFloraPdfMorePages.txt");

        System.out.println("Test of broken link to pdf 'Květena ČR': ");
        System.out.println("URL: e.g. https://pladias.cz/taxon/flora/Abies%20alba");
        System.out.println("Description: It tests if links to pdf downloading (button) download the pdf properly.");
        System.out.println("Test start: " + testStartFormated);
        System.out.println();
        System.out.println("Keywords to find errors: 404, not found");
        System.out.println();

        //nacte variabilni cast adresy z csv souboru
        System.out.println("Tested taxa: ");
        try {
            reader = new CSVReader(new FileReader(csvFile));

            while ((line = reader.readNext()) != null) {
                System.out.println(line[0]);
                taxa.add(line[0]);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
        System.out.println("Tested pdf links: ");
        if (taxa != null) {
            for (String i : taxa) {
                linkURL = baseLinkURL + i;
                verifyLinkFloraPdf(linkURL);
            }
        }

        System.out.println();
        //info about test (e.g. duration)
        getTestInfo(testStart);

        // Use stored value for output stream
        System.setOut(console);
        //System.out.println("This will be written on the console!");

    }

    public void verifyLinkFloraPdf(String linkURL) {
        String linkUrl;
        String pageUrl = linkURL;
        WebElement link;
        driver.get(pageUrl);
        //najdi tlacitko na stazeni
        try {
            link = driver.findElement(By.linkText("stáhnout"));
            assertTrue("Button 'Květena ČR - stáhnout' is not displayed.", link.isDisplayed());
            assertTrue("Button 'Květena ČR - stáhnout' is not enabled.", link.isEnabled());
            //ziskam odkaz na pdf
            linkUrl = link.getAttribute("href");

            //spustim metodu na kontrolu rozbitych odkazu
            verifyLinkActive(linkUrl);
        } catch (NoSuchElementException e) {
            //e.printStackTrace();
            System.out.println(linkURL + "- without link to pdf file");
        }
    }

    @Test
    //zjisteni rozbitych odkazu na pdf vegetace

    public void verifyLinkVegetationPdfMorePages() {
        //staticka cast adresy
        String baseLinkURL = baseUrl + "vegetation/overview/";
        String csvFile = "syntaxa.csv";
//        String csvFile = "syntaxa2.csv";
        PrintStream console = System.out;
        String linkURL;
        CSVReader reader = null;
        String[] line = null;
        ArrayList<String> syntaxa = new ArrayList<String>();
        LocalDateTime testStart = LocalDateTime.now();
        String testStartFormated = dtf3.format(testStart);

        writeTestResultToFile("verifyLinkVegetationPdfMorePages.txt");

        System.out.println("Test of broken link to pdf 'Vegetace ČR': ");
        System.out.println("URL: e.g. https://pladias.cz/vegetation/overview/Iso%C3%ABtetum%20lacustris");
        System.out.println("Description: It tests if links to pdf downloading (button) download the pdf properly.");
        System.out.println("Test start: " + testStartFormated);
        System.out.println();
        System.out.println("Keywords to find errors: 404, not found");
        System.out.println();

        //nacte variabilni cast adresy z csv souboru
        System.out.println("Tested syntaxa: ");
        try {
            reader = new CSVReader(new FileReader(csvFile));

            while ((line = reader.readNext()) != null) {
                System.out.println(line[0]);
                syntaxa.add(line[0]);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println();
        System.out.println("Tested pdf links: ");

        if (syntaxa != null) {
            for (String i : syntaxa) {
                linkURL = baseLinkURL + i;
                verifyLinkVegetationPdf(linkURL);
            }
        }

        System.out.println();

        //info about test (e.g. duration)
        getTestInfo(testStart);

        // Use stored value for output stream
        System.setOut(console);
        //System.out.println("This will be written on the console!");

    }

    public void verifyLinkVegetationPdf(String linkURL) {
        String linkUrl;
        String pageUrl = linkURL;
        WebElement link;

        driver.get(pageUrl);
        //najdi tlacitko na stazeni
        try {
            link = driver.findElement(By.linkText("Vegetace ČR"));
            assertTrue("Button 'Vegetace ČR' is not displayed.", link.isDisplayed());
            assertTrue("Button 'Vegetace ČR' is not enabled.", link.isEnabled());
            //ziskam odkaz na pdf
            linkUrl = link.getAttribute("href");

            //spustim metodu na kontrolu rozbitych odkazu
            verifyLinkActive(linkUrl);
        } catch (NoSuchElementException e) {
            //e.printStackTrace();
            System.out.println(linkURL + " - without link to pdf file");
        }
    }

    @Test
    //zjisteni rozbitych odkazu na pdf fytokartografické syntézy

    public void verifyLinkSynthesisPdfMorePages() {
        //staticka cast adresy
        String baseLinkURL = baseUrl + "taxon/distribution/";
        String csvFile = "taxa.csv";
//        String csvFile = "taxa2.csv";
        PrintStream console = System.out;
        String linkURL;
        CSVReader reader = null;
        String[] line = null;
        ArrayList<String> taxa = new ArrayList<String>();
        LocalDateTime testStart = LocalDateTime.now();
        String testStartFormated = dtf3.format(testStart);

        writeTestResultToFile("verifyLinkSynthesisPdfMorePages.txt");

        System.out.println("Test of broken links to download pdf 'Fytokartografické syntézy': ");
        System.out.println("URL: e.g. https://pladias.cz/taxon/distribution/Abies%20alba");
        System.out.println("Description: It tests if links to pdf downloading (button) download the pdf properly.");
        System.out.println("Test start: " + testStartFormated);
        System.out.println();
        System.out.println("Keywords to find errors: 404, not found");
        System.out.println();

        //nacte variabilni cast adresy z csv souboru
        try {
            reader = new CSVReader(new FileReader(csvFile));

            System.out.println("Tested taxa: ");

            while ((line = reader.readNext()) != null) {
                System.out.println(line[0]);
                taxa.add(line[0]);
            }
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (taxa != null) {
            for (String i : taxa) {
                linkURL = baseLinkURL + i;
                verifyLinkSynthesisPdf(linkURL);
            }
        }

        System.out.println();
        //info about test (e.g. duration)
        getTestInfo(testStart);

        // Use stored value for output stream
        System.setOut(console);
        //System.out.println("This will be written on the console!");

    }

    public void verifyLinkSynthesisPdf(String linkURL) {
        String linkUrl;
        String pageUrl = linkURL;
        WebElement link;
        driver.get(pageUrl);
        //najdi tlacitko na stazeni
        try {
            link = driver.findElement(By.xpath("//a[contains(@href,'syntezy')]"));
            assertTrue("Button 'Rozšíření - fytokartografické syntézy' is not displayed.", link.isDisplayed());
            assertTrue("Button 'Rozšíření - fytokartografické syntézy' is not enabled.", link.isEnabled());
            //ziskam odkaz na pdf
            linkUrl = link.getAttribute("href");

            //spustim metodu na kontrolu rozbitych odkazu
            verifyLinkActive(linkUrl);
        } catch (NoSuchElementException e) {

            System.out.println(linkURL + " - without link to pdf file");
        }
    }

    @Test
    //Test of Určování - Vegetace - nepoužívám
    public void testVegetationDetermination() {
        WebElement radiobox;
        WebElement textField1;
        WebElement textField2;
        WebElement cleanButton;

        //navigate to page
        driver.get(baseUrl + "vegkey/");

        //test the radioboxes
        //Lesní a křovinná vegetace
        radiobox = driver.findElement(By.xpath("//input[@name='vegkey_forest'][@value='1']"));
        System.out.println("Radiobox " +
                "Lesní a křovinná vegetace: displayed - " + radiobox.isDisplayed() +
                ", enabled - " + radiobox.isEnabled() + ", selected - " + radiobox.isSelected());
        //assertTrue(radiobox.isDisplayed());
        assertTrue(radiobox.isEnabled());
        assertTrue(radiobox.isSelected());

        //Ostatní vegetační typy
        radiobox = driver.findElement(By.xpath("//input[@name='vegkey_forest'][@value='0']"));
        System.out.println("Radiobox " +
                "Ostatní vegetační typy: displayed - " + radiobox.isDisplayed() +
                ", enabled - " + radiobox.isEnabled() + ", selected - " + radiobox.isSelected());
        //assertTrue(radiobox.isDisplayed());
        assertTrue(radiobox.isEnabled());
        assertFalse(radiobox.isSelected());

        //Určování asociací
        radiobox = driver.findElement(By.xpath("//input[@name='vegkey_association'][@value='1']"));
        System.out.println("Radiobox " +
                "Určování asociací: displayed - " + radiobox.isDisplayed() +
                ", enabled - " + radiobox.isEnabled() + ", selected - " + radiobox.isSelected());
        //assertTrue(radiobox.isDisplayed());
        assertTrue(radiobox.isEnabled());
        //assertFalse(radiobox.isSelected());

        //Určování svazů
        radiobox = driver.findElement(By.xpath("//input[@name='vegkey_association'][@value='0']"));
        System.out.println("Radiobox " +
                "Určování svazů: displayed - " + radiobox.isDisplayed() +
                ", enabled - " + radiobox.isEnabled() + ", selected - " + radiobox.isSelected());
        //assertTrue(radiobox.isDisplayed());
        assertTrue(radiobox.isEnabled());
        //assertTrue(radiobox.isSelected());

        //text field Taxony s pokryvností >25 %
        textField1 = driver.findElement(By.id("vegkey-dominant-taxon"));
        System.out.println("Text field for taxa >25%: displayed - " + textField1.isDisplayed()
                + ", enabled: " + textField1.isEnabled());
        assertTrue(textField1.isDisplayed());
        assertTrue(textField1.isEnabled());

        //text field Ostatní taxony
        textField2 = driver.findElement(By.id("vegkey-common-taxon"));
        System.out.println("Text field for other taxa: displayed - " + textField2.isDisplayed()
                + ", enabled: " + textField2.isEnabled());
        assertTrue(textField2.isDisplayed());
        assertTrue(textField2.isEnabled());

        textField1.sendKeys("Abies alba");
        //nefunguje
        System.out.println("Searched text in text field1: " + textField2.getText());

        textField2.sendKeys("Viburnum opulus");
        //nefunguje
        System.out.println("Searched text in text field2: " + textField2.getText());

        //test clear form button
        cleanButton = driver.findElement(By.xpath("//a[text()='vyčistit formulář']"));
        cleanButton.click();

//        assertEquals(textField1.getAttribute("placeholder"), textField1.getText());
//        assertEquals(textField1.getAttribute("placeholder"), textField2.getText());

    }

    @Test
    //Ke stažení - Druhy a vlastnosti
    //není testováno isDisplayed, isEnabled kvůli rozbalování boxů
    public void testDownloadFeatures() throws InterruptedException {
        List<WebElement> links;
        String url;
        File latestFile;
        String fileName;
        String expectedFileName;
        PrintStream console = System.out;
        LocalDateTime testStart = LocalDateTime.now();
        String testStartFormated = dtf3.format(testStart);

        writeTestResultToFile("testDownloadFeatures.txt");

        //Ke stažení - Druhy a vlastnosti
        System.out.println("Test of 'Download - Species and traits': ");
        System.out.println("URL: https://pladias.cz/download/features");
        System.out.println("Description: It tests if links to downloading (buttons) download the files (e.g. pdf, xlsx) properly.");
        System.out.println("Test start: " + testStartFormated);
        System.out.println();
        System.out.println("Keywords to find errors: failed");
        System.out.println();

        driver.get(baseUrl + "download/features");

        //najde odkazy na stahování, kromě traitů, stáhne a otestuje

        links = driver.findElements(By.xpath("//a[@download][not(contains(@download,'/traits/'))]"));
        System.out.println("Number of links: " + links.size());
        System.out.println();

        for (WebElement link : links) {
            url = link.getAttribute("href");
            System.out.println("url: " + url);
            driver.get(url);

            //wait 5 seconds
            Thread.sleep(5000);

            latestFile = getLatestFilefromDir(downloadPath);
            fileName = latestFile.getName();

            //opakovaně přidávám čekám 10 sekund, dokud se soubor nestáhne
            while (fileName.endsWith(".crdownload")) {
                Thread.sleep(10000);
                latestFile = getLatestFilefromDir(downloadPath);
                fileName = latestFile.getName();
            }

            System.out.println("Downloaded file name: " + fileName);
            expectedFileName = url.substring(url.lastIndexOf("/") + 1);

            if (fileName.equals(expectedFileName)) {
                System.out.println("File download - OK.");
                System.out.println();
            } else {
                System.out.println("File download - failed.");
                System.out.println();
            }

            assertTrue("Downloaded file name is not matching with expected file name.",
                    fileName.equals(expectedFileName));
        }

        //najde odkazy na stahování traitů, stáhne je, ale netestuje
        //zkontrolovat ručně?

        System.out.println();
        System.out.println("Traits to download: ");
        System.out.println("Traits download must be checked manually.");

        links = driver.findElements(By.xpath("//a[@download][contains(@download,'/traits/')]"));

        System.out.println();
        System.out.println("Number of trait links: " + links.size());
        System.out.println();

        for (WebElement link : links) {
            url = link.getAttribute("href");
            System.out.println("url: " + url);
            driver.get(url);

            //wait 10 seconds
            Thread.sleep(10000);
        }

        System.out.println();

        //info about test (e.g. duration)
        getTestInfo(testStart);

        // Use stored value for output stream
        System.setOut(console);
        //System.out.println("This will be written on the console!");
    }

    @Test
    //Ke stažení - Fytogeografie a rozšíření druhů
    //upravit thread.sleep
    public void testDownloadPhytogeography() throws InterruptedException {
        List<WebElement> links;
        String url;
        File latestFile;
        String fileName;
        String expectedFileName;
        PrintStream console = System.out;
        LocalDateTime testStart = LocalDateTime.now();
        String testStartFormated = dtf3.format(testStart);

        writeTestResultToFile("testDownloadPhytogeography.txt");

        //Ke stažení - Druhy a vlastnosti
        System.out.println("Test of 'Ke stažení - Fytogeografie a rozšíření druhů': ");
        System.out.println("URL: https://pladias.cz/download/phytogeography");
        System.out.println("Description: It tests if links to downloading (buttons) download the files (e.g. tif, zip) properly.");
        System.out.println("test start: " + testStartFormated);
        System.out.println();
        System.out.println("Keywords to find errors: failed");
        System.out.println();

        driver.get(baseUrl + "download/phytogeography");

        //najde odkazy na stahování, stáhne a otestuje
        links = driver.findElements(By.xpath("//a[@download]"));

        System.out.println("Number of links: " + links.size());
        System.out.println();

        for (WebElement link : links) {
            url = link.getAttribute("href");
            System.out.println("url: " + url);
            driver.get(url);

            //wait 5 seconds
            Thread.sleep(5000);

            latestFile = getLatestFilefromDir(downloadPath);
            fileName = latestFile.getName();

            //opakovaně přidávám čekám 10 sekund, dokud se soubor nestáhne
            while (fileName.endsWith(".crdownload")) {
                Thread.sleep(10000);
                latestFile = getLatestFilefromDir(downloadPath);
                fileName = latestFile.getName();
            }

            System.out.println("Downloaded file name: " + fileName);
            expectedFileName = url.substring(url.lastIndexOf("/") + 1);

            if (fileName.equals(expectedFileName)) {
                System.out.println("File download - OK.");
                System.out.println();
            } else {
                System.out.println("File download - failed.");
                System.out.println();
                if (expectedFileName.contains("crdownload")) {
                    System.out.println("File needs more time to be downloaded.");
                }
            }

            assertTrue("Downloaded file name is not matching with expected file name.",
                    fileName.equals(expectedFileName));
        }

        //info about test (e.g. duration)
        getTestInfo(testStart);
        // Use stored value for output stream
        System.setOut(console);
        //System.out.println("This will be written on the console!");

    }

    @Test
    //Ke stažení - Fytogeografie a rozšíření druhů - Výpis taxonů pro mapovací pole a kvadranty
    //upravit thread.sleep
    public void testDownloadTaxaListFromInteractiveMap() throws InterruptedException {
        List<WebElement> links;
        WebElement element;
        String url;
        File latestFile;
        String fileName;
        String expectedFileName;
        String square;
        PrintStream console = System.out;
        LocalDateTime testStart = LocalDateTime.now();
        String testStartFormated = dtf3.format(testStart);

        writeTestResultToFile("testDownloadTaxaListFromInteractiveMap.txt");

        //Ke stažení - Druhy a vlastnosti
        System.out.println("Test of 'Ke stažení - Fytogeografie a rozšíření druhů - Výpis taxonů pro mapovací pole a kvadranty': ");
        System.out.println("URL: https://pladias.cz/download/phytogeography");
        System.out.println("Description: It tests if links to downloading lists of taxa (xlsx) download the files properly.");
        System.out.println();
        System.out.println("Test start: " + testStartFormated);
        System.out.println();
        System.out.println("Keywords to find errors: failed");

        driver.get(baseUrl + "download/phytogeography");

        //find canvas and click on it
        element = driver.findElement(By.xpath("//canvas"));
        assertTrue("Link 'Přehled - Hlavní obrázek' is not displayed.", element.isDisplayed());
        assertTrue("Link 'Přehled - Hlavní obrázek' is not enabled.", element.isEnabled());
        element.click();

        square = driver.findElement(By.xpath("//*[@id='square']/b[1]")).getAttribute("innerHTML");

        //find links to download present taxa
        links = driver.findElements(By.xpath("//p[@id='present_taxa']/a"));

        System.out.println("Present taxa - number of links: " + links.size());
        System.out.println();

        for (WebElement link : links) {
            link.click();

            //wait 5 seconds
            Thread.sleep(5000);

            latestFile = getLatestFilefromDir(downloadPath);
            fileName = latestFile.getName();

            //opakovaně přidávám čekám 10 sekund, dokud se soubor nestáhne
            while (fileName.endsWith(".crdownload")) {
                Thread.sleep(10000);
                latestFile = getLatestFilefromDir(downloadPath);
                fileName = latestFile.getName();
            }

            System.out.println("Downloaded file name: " + fileName);
            expectedFileName = "zaznamenane-taxony" + square + link.getAttribute("innerHTML").replace("mapovací pole", "abcd") + "vypis-ke-dni" + dtf2.format(now) + ".xlsx";
            System.out.println("expected file name: " + expectedFileName);

            if (fileName.equals(expectedFileName)) {
                System.out.println("File download - OK.");
                System.out.println();
            } else {
                System.out.println("File download - failed.");
                System.out.println();
            }

            assertTrue("Downloaded file name is not matching with expected file name.",
                    fileName.equals(expectedFileName));
        }

        //find links to download missing taxa
        links = driver.findElements(By.xpath("//p[@id='missing_taxa']/a"));

        System.out.println("Missing taxa - number of links: " + links.size());
        System.out.println();

        for (WebElement link : links) {
            link.click();

            //wait 10 seconds
            Thread.sleep(5000);

            latestFile = getLatestFilefromDir(downloadPath);
            fileName = latestFile.getName();
            System.out.println("Downloaded file name: " + fileName);
            expectedFileName = "chybejici-taxony" + square + link.getAttribute("innerHTML").replace("mapovací pole", "abcd") + "vypis-ke-dni" + dtf2.format(now) + ".xlsx";
            System.out.println("expected file name: " + expectedFileName);

            if (fileName.equals(expectedFileName)) {
                System.out.println("File download - OK.");
                System.out.println();
            } else {
                System.out.println("File download - failed.");
                System.out.println();
            }

            assertTrue("Downloaded file name is not matching with expected file name.",
                    fileName.equals(expectedFileName));
        }

        element = driver.findElement(By.xpath("//button[text()='zavřít']"));
        assertTrue("Link 'Přehled - Hlavní obrázek' is not displayed.", element.isDisplayed());
        assertTrue("Link 'Přehled - Hlavní obrázek' is not enabled.", element.isEnabled());
        element.click();

        //info about test (e.g. duration)
        getTestInfo(testStart);

        // Use stored value for output stream
        System.setOut(console);
        //System.out.println("This will be written on the console!");
    }

    @Test
    //Ke stažení - Vegetace
    public void testDownloadVegetation() throws InterruptedException, IOException {
        List<WebElement> links;
        String url;
        File latestFile;
        String fileName;
        String expectedFileName;
        PrintStream console = System.out;
        LocalDateTime testStart = LocalDateTime.now();
        String testStartFormated = dtf3.format(testStart);

        writeTestResultToFile("testDownloadVegetation.txt");

        try {
            //Ke stažení - Druhy a vlastnosti
            System.out.println("'Ke stažení - Vegetace': ");
            System.out.println("URL: https://pladias.cz/download/vegetation");
            System.out.println("Description: It tests if links to downloading files (pdf, zip, xlsx) download the files properly.");
            System.out.println("Test start: " + testStartFormated);
            System.out.println();
            System.out.println("Keywords to find errors: failed");
            System.out.println();

            driver.get(baseUrl + "download/vegetation");

            //najde odkazy na stahování
            links = driver.findElements(By.xpath("//a[@download]"));

            System.out.println("Number of links: " + links.size());
            System.out.println();

            for (WebElement link : links) {
                url = link.getAttribute("href");
                System.out.println("url: " + url);

                //if the file is image (jpg type) save image do download folder
                if (getContentType(url).equals("image")) {
                    try {
                        BufferedImage bufferedImage = ImageIO.read(new URL(url));
                        File outputfile = new File(downloadPath + url.substring(url.lastIndexOf("/") + 1));
                        //ImageIO.write(bufferedImage, "jpg", outputfile);
                        ImageIO.write(bufferedImage, url.substring(url.lastIndexOf(".") + 1), outputfile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                //if the file is not image
                else {
                    driver.get(url);
                }

                //wait 5 seconds
                Thread.sleep(5000);

                //test if the file was downloaded
                latestFile = getLatestFilefromDir(downloadPath);
                fileName = latestFile.getName();

                //opakovaně přidávám čekám 10 sekund, dokud se soubor nestáhne
                while (fileName.endsWith(".crdownload")) {
                    Thread.sleep(10000);
                    latestFile = getLatestFilefromDir(downloadPath);
                    fileName = latestFile.getName();
                }

                System.out.println("Downloaded file name: " + fileName);
                expectedFileName = url.substring(url.lastIndexOf("/") + 1);

                if (fileName.equals(expectedFileName)) {
                    System.out.println("File download - OK.");
                    System.out.println();
                } else {
                    System.out.println("File download - failed.");
                    if (fileName.contains("crdownload")) {
                        System.out.println("File needs more time to be downloaded.");
                    }
                }

                assertTrue("Downloaded file name is not matching with expected file name.",
                        fileName.equals(expectedFileName));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //info about test (e.g. duration)
        getTestInfo(testStart);

        // Use stored value for output stream
        System.setOut(console);
        //System.out.println("This will be written on the console!");
    }

    @Test
    //Ke stažení - Bibliografie
    public void testDownloadBibliography() throws InterruptedException, IOException {

        List<WebElement> links;
        String url;
        File latestFile;
        String fileName;
        String expectedFileName;
        PrintStream console = System.out;
        LocalDateTime testStart = LocalDateTime.now();
        String testStartFormated = dtf3.format(testStart);

        writeTestResultToFile("testDownloadBibliography.txt");

        try {
            //Ke stažení - Druhy a vlastnosti
            System.out.println("'Ke stažení - Bibliografie': ");
            System.out.println("URL: https://pladias.cz/download/bibliography");
            System.out.println("Description: It tests if links to downloading files (pdf) download the files properly.");
            System.out.println("Test start: " + testStartFormated);
            System.out.println();
            System.out.println("Keywords to find errors: failed");
            System.out.println();

            driver.get(baseUrl + "download/bibliography");

            //najde odkazy na stahování
            links = driver.findElements(By.xpath("//a[@download]"));

            System.out.println("Number of links: " + links.size());
            System.out.println();

            for (WebElement link : links) {
                url = link.getAttribute("href");
                System.out.println("url: " + url);

                //if the file is image save it do download folder
                if (getContentType(url).equals("image")) {
                    try {
                        BufferedImage bufferedImage = ImageIO.read(new URL(url));
                        File outputfile = new File(downloadPath + url.substring(url.lastIndexOf("/") + 1));
                        //ImageIO.write(bufferedImage, "jpg", outputfile);
                        ImageIO.write(bufferedImage, url.substring(url.lastIndexOf(".") + 1), outputfile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                //if the file is not image
                else {
                    driver.get(url);
                }

                //wait 5 seconds
                Thread.sleep(5000);

                //test if the file was downloaded
                latestFile = getLatestFilefromDir(downloadPath);
                fileName = latestFile.getName();

                //opakovaně přidávám čekám 1 minutu, dokud se soubor nestáhne
                while (fileName.endsWith(".crdownload")) {
                    Thread.sleep(60000);
                    latestFile = getLatestFilefromDir(downloadPath);
                    fileName = latestFile.getName();
                }

                System.out.println("Downloaded file name: " + fileName);
                expectedFileName = url.substring(url.lastIndexOf("/") + 1);

                if (fileName.equals(expectedFileName)) {
                    System.out.println("File download - OK.");
                    System.out.println();
                } else {
                    System.out.println("File download - failed.");
                    if (fileName.contains("crdownload")) {
                        System.out.println("File needs more time to be downloaded.");
                    }
                }

                assertTrue("Downloaded file name is not matching with expected file name.",
                        fileName.equals(expectedFileName));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //info about test (e.g. duration)
        getTestInfo(testStart);

        // Use stored value for output stream
        System.setOut(console);
        //System.out.println("This will be written on the console!");
    }

    /////////////////////////////////////////////////////////////////////////////////
    //Pomocné metody
    /////////////////////////////////////////////////////////////////////////////////

    //verification of page url, displaying title and source code length
    public void testPageInfo(String url, String expectedUrl) {
        String title;
        int titleLength;
        String actualUrl;
        //String expectedUrl = "https://pladias.cz/";
        String pageSource;
        int pageSourceLength;

        driver.get(url);

        //take page title
        title = driver.getTitle();
        //take page title length
        titleLength = title.length();

        //print title and its lenght in the console
        System.out.println("Title of the page is: '" + title + "'");
        System.out.println("Length of the page title is: " + titleLength);
        assertNotEquals(0, titleLength);

        //storing ULR in String variable
        actualUrl = driver.getCurrentUrl();

        if (actualUrl.equals(expectedUrl)) {
            System.out.println("Verification successful - the correct url is opened.");
        } else {
            System.out.println("Verification failed - an incorred url is opened.");
        }

        assertEquals(expectedUrl, actualUrl);

        //storing page source in string variable
        pageSource = driver.getPageSource();

        //storing page source in string variable
        pageSourceLength = pageSource.length();
        assertNotEquals(0, pageSourceLength);

        //printing length of the page source on console
        System.out.println("Total length of the page source is: " + pageSourceLength);
        System.out.println();
    }

    //metoda na overeni odkazů na jedne strance
    public void verifyLinks(String pageUrl) {
        //public void verifyLinks() {

//      String pageUrl = "https://pladias.cz/";
        String linkUrl;
        String linkText;
        String title;

        driver.get(pageUrl);
        title = driver.getTitle();
        //najdi vsechny odkazy
        List<WebElement> links = driver.findElements(By.tagName("a"));

        System.out.println();
        System.out.println("Title: " + title + ", url: " + pageUrl + ", number of links: " + links.size());

        //cyklicky projdu odkazy

        for (
                int i = 0; i < links.size(); i++) {
            WebElement ele = links.get(i);
            linkText = ele.getText();
            linkUrl = ele.getAttribute("href");

            //spustim metodu na kontrolu rozbitych odkazu
            verifyLinkActive(linkUrl);
        }

    }

    //method - write result of test into text file
    public void writeTestResultToFile(String fileName) {
        boolean result;

//vytvořím textový soubor pro výstup testu
        File file = new File(dirPath + fileName);
        //initialize File object and passing path as argument

        try {
            //create new file
            result = file.createNewFile(); //creates a new file

            if (result) {
                System.out.println("Directory created: " + file.getCanonicalPath());
            } else {
                System.out.println("Directory already exist at location: " + file.getCanonicalPath());
            }
        } catch (IOException e) {
            e.printStackTrace(); //prints exception if any
        }

        //new stream
        try {
            // Creating a File object that represents the disk file.
            PrintStream out = new PrintStream(file);

            // Assign out to output stream
            System.setOut(out);
            //System.out.println("This will be written to the text file");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //checks if the file is downloaded
    public boolean isFileDownloaded(String downloadPath, String fileName) {
        boolean flag = false;
        File dir = new File(downloadPath);
        File[] dir_contents = dir.listFiles();

        for (int i = 0; i < dir_contents.length; i++) {
            if (dir_contents[i].getName().equals(fileName))
                return flag = true;
        }

        return flag;
    }

    // Get the latest file from a specific directory*/
    public File getLatestFilefromDir(String dirPath) {
        File lastModifiedFile = null;

        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return null;
        } else {
            lastModifiedFile = files[0];
            if (files.length > 1) {
                for (int i = 1; i < files.length; i++) {
                    if (lastModifiedFile.lastModified() < files[i].lastModified()) {
                        lastModifiedFile = files[i];
                    }
                }
            }
        }

        return lastModifiedFile;
    }

    //metoda na zjištění typu souboru ještě před stažením
    public String getContentType(String urlText) throws IOException {
        String contentTypeSubtype = null;
        String contentType = null;
        String contentSubtype = null;
        long contentSize = 0;

        try {
            URL url = new URL(urlText);
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            contentTypeSubtype = huc.getContentType();
            String[] array = contentTypeSubtype.split("/");
            contentType = array[0];
            contentSubtype = array[1];
            //zjisti velikost
            contentSize = huc.getContentLengthLong();
            huc.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
//        System.out.println("content type and subtype:" + contentTypeSubtype);
//        System.out.println("content type: " + contentType);
//        System.out.println("content subtype: " + contentSubtype);
//        System.out.println("content size: " + contentSize);

        return contentType;

    }

    public void getTestInfo(LocalDateTime testStart) {
        String testStartFormated = dtf3.format(testStart);
        LocalDateTime testEnd = LocalDateTime.now();
        String testEndFormated = dtf3.format(testEnd);

        Duration duration = Duration.between(testStart, testEnd);
        long diff = Math.abs(duration.toMinutes());

        System.out.println("test start: " + testStartFormated);
        System.out.println("test end: " + testEndFormated);
        if (diff == 0) {
            System.out.println("test duration: < 1 min.");
        } else {
            System.out.println("test duration: " + diff + " min.");
        }

    }

}