package Scraping;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Webscraper webscraper = new Webscraper();
//        WaterlooProgram[] listWaterlooProgram = webscraper.waterlooExtract();
//        UofTProgram[] listUofTProgram = webscraper.UofTExtract();
        McMasterProgram[] listMcMasterProgram = webscraper.mcMasterExtract();

        ObjectMapper objectMapper = new ObjectMapper();
//        for (WaterlooProgram i : listWaterlooProgram) {
//            System.out.println(Toolbox.ObjectToJSON(objectMapper, i));
//        }
//        for (UofTProgram i : listUofTProgram) {
//            System.out.println(Toolbox.ObjectToJSON(objectMapper, i));
//        }
//        for (McMasterProgram i : listMcMasterProgram) {
//            System.out.println(Toolbox.ObjectToJSON(objectMapper, i));
//        }

        //TODO: remove the "and" from the title of the waterloo event
        //TODO: McMaster audience better parsing (extract information from other sources than the title)
        //TODO: duplicate checker
        //TODO: scrape concurrently
    }
}
