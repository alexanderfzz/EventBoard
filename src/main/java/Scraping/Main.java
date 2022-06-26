package Scraping;

import java.io.IOException;

public class Main {
    public static WaterlooScraper waterlooScraper;
    public static UofTScraper uofTScraper;
    public static McMasterScraper mcMasterScraper;
    public static UBCScraper uBCScraper;

    public static void main(String[] args) throws IOException {
        waterlooScraper = new WaterlooScraper();
        Thread waterlooScrapeThread = new Thread(waterlooScraper);
        waterlooScrapeThread.start();

        uofTScraper = new UofTScraper();
        Thread uOfTScrapeThread = new Thread(uofTScraper);
        uOfTScrapeThread.start();

        mcMasterScraper = new McMasterScraper();
        Thread mcMasterScrapeThread = new Thread(mcMasterScraper);
        mcMasterScrapeThread.start();

        uBCScraper = new UBCScraper();
        Thread uBCScrapeThread = new Thread(uBCScraper);
        uBCScrapeThread.start();

        //OPTIMIZE: currently Shaya is creating another program object after the date is formatted, this is not efficient, format the date before creating the first batch of object would be better, do that

    }
}
