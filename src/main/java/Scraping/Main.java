package Scraping;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Main {
    public static WaterlooScraper waterlooScraper;
    public static UofTScraper uofTScraper;
    public static McMasterScraper mcMasterScraper;
    public static UBCScraper ubcScraper;

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

        ubcScraper = new UBCScraper();
        Thread ubcScraperThread = new Thread(ubcScraper);
        ubcScraperThread.start();

        //OPTIMIZE: remove the "and" from the title of the waterloo event

        //TODO: learn Callable interface
        //TODO: create word filter for UBC focus

        //FIXME: AHHHHHHHHHHHHH!! While it's true that age is no guarantee of efficiency, and youth is no guarantee of innovation

    }
}
