package Scraping;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        WaterlooScraper waterlooScraper = new WaterlooScraper();
        Thread waterlooScrapeThread = new Thread(waterlooScraper);
        waterlooScrapeThread.start();

        UofTScraper uofTScraper = new UofTScraper();
        Thread uOfTScrapeThread = new Thread(uofTScraper);
        uOfTScrapeThread.start();

        McMasterScraper mcMasterScraper = new McMasterScraper();
        Thread mcMasterScrapeThread = new Thread(mcMasterScraper);
        mcMasterScrapeThread.start();

        //OPTIMIZE: remove the "and" from the title of the waterloo event
        //OPTIMIZE: McMaster audience better parsing (extract information from other sources than the title)
        //OPTIMIZE: if possible improve efficiency of mcmaster scrape, or somehow do multithreading for mcmaster scrape

        //TODO: figure out why waterloo and mcmaster together cause warning log from client-id thread
        //TODO: learn Callable interface
        //TODO: change waterloo scrape to javascript-less

        //FIXME: AHHHHHHHHHHHHH!! While it's true that age is no guarantee of efficiency, and youth is no guarantee of innovation
        //   youth however, is a guarantee of insanity, typing from experience...
    }
}
