package Scraping;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Webscraper webscraper = new Webscraper();
        WaterlooProgram[] listWaterlooProgram = webscraper.waterlooExtract();
        UofTProgram[] listUofTProgram = webscraper.UofTExtract();
        McMasterProgram[] listMcMasterProgram = webscraper.mcMasterExtract();
        ObjectMapper objectMapper = new ObjectMapper();

        //TODO: figure out if java object notation have any use
        //TODO: remove the "and" from the title of the event
    }
}
