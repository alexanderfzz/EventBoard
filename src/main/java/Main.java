import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Webscraper webscraper = new Webscraper("https://uwaterloo.ca/future-students/visit-waterloo/high-school-enrichment-programs");
        WaterlooProgram[] listWaterlooProgram = webscraper.extract();

        //TODO: save objects in json format
        //TODO: remove the "and" from the title of the event
        //TODO: research databases
    }
}
