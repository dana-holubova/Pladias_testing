Automated tests of Pladias websites

- Tests are created using JUnit and Selenium frameworks, Java 8
- Contains 20 different tests

Tutorial

- Open project in IJ Idea (https://www.jetbrains.com/idea/)
- Download the actual version of chromedriver.exe (https://chromedriver.chromium.org/downloads)
- Set configs/Configuration.properties and configs/PrivateConfigurationProperties (remove "example" from the filename)
and save it in the root directory (or replace the older version)
- Update files taxa.csv, syntaxa.csv a pages.csv (in the root directory)
- Launch PladiasTest class (in src/test/java/PladiasTest)
- The results of each test are saved in separate txt file in directory Pladias_tests_[actual_date]

