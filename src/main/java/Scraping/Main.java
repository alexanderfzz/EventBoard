package Scraping;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        McMasterScraper mcMasterScraper = new McMasterScraper();
        Thread mcMasterScrapeThread = new Thread(mcMasterScraper);
        mcMasterScrapeThread.start();

        WaterlooScraper waterlooScraper = new WaterlooScraper();
        Thread waterlooScrapeThread = new Thread(waterlooScraper);
        waterlooScrapeThread.start();

        UofTScraper uofTScraper = new UofTScraper();
        Thread uOfTScrapeThread = new Thread(uofTScraper);
        uOfTScrapeThread.start();


        //TODO: remove the "and" from the title of the waterloo event
        //TODO: McMaster audience better parsing (extract information from other sources than the title)
        //TODO: figure out why waterloo and mcmaster together cause warning log from client-id thread
        //TODO: learn Callable interface
        //TODO: if possible improve efficiency of mcmaster scrape, or somehow do multithreading for mcmaster scrape
    }
}
