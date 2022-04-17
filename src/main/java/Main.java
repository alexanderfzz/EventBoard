import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Webscraper webscraper = new Webscraper();
        WaterlooProgram[] listWaterlooProgram = webscraper.waterlooExtract();
        UofTProgram[] listUofTProgram = webscraper.UofTExtract();
        //TODO: save objects in json format, and in java object notation, implement serializable
        //TODO: remove the "and" from the title of the event
        //TODO: research databases
    }
}
