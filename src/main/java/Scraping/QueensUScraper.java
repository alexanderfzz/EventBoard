package Scraping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.Html;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class QueensUScraper implements Runnable {
    private static final List<Program> programs;
    private static final Set<String> hrefSet;

    static {
        programs = Collections.synchronizedList(new LinkedList<>());
        hrefSet = ConcurrentHashMap.newKeySet();
    }

    public QueensUScraper() {
    }

    @Override
    public void run() {
        LinkedList<Thread> threads = new LinkedList<>();
        //TODO: esu, qsea
        QueensESUScraper queensESUScraper = null;
        try {
            queensESUScraper = new QueensESUScraper();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread eSUScraperThread = new Thread(queensESUScraper);
        threads.add(eSUScraperThread);
        eSUScraperThread.start();

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            export();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    private static void export() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        for (Program i : programs) {
            System.out.println(Toolbox.ObjectToJSON(objectMapper, i));
        }
    }

    public static List<Program> getPrograms() {
        return programs;
    }


    private static class QueensESUScraper implements Runnable {
        private WebClient webClient;
        private HtmlPage page;

        private QueensESUScraper() throws IOException {
            this.webClient = new WebClient();
            this.webClient.getOptions().setCssEnabled(false);
            this.webClient.getOptions().setJavaScriptEnabled(false);
            String url = "https://esu.queensu.ca/programs/";
            this.page = this.webClient.getPage(url);
        }

        @Override
        public void run() {
            List<HtmlAnchor> anchors = page.getByXPath("//div[@id='esu-programs-list']/div/div/div/div[2]/p/a");
            for (HtmlAnchor anchor : anchors) {
                String absoluteURL = "https://esu.queensu.ca/programs" + anchor.getHrefAttribute().replace("https://esu.queensu.ca/programs", "").replace("/programs", "");
                if (!Toolbox.isNotDuplicate(hrefSet, absoluteURL)) {
                    continue;
                }
                try {
                    baseScrape(absoluteURL);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void baseScrape(String url) throws IOException {
            HtmlPage basePage = this.webClient.getPage(url);
            DomText titleDT = basePage.getFirstByXPath("//div[@class='cs-content']/div[1]/div[2]/div/h1/span/text()");
            String title, audiences;
            if (titleDT == null) {
                title = ((DomText) basePage.getFirstByXPath("//div[@class='cs-content']/div[1]/div[2]/div/h2/span/text()[1]")).getWholeText();
                audiences = ((DomText) basePage.getFirstByXPath("//div[@class=\"cs-content\"]/div[1]/div[2]/div/h2/span/text()[2]")).getWholeText().split("Grades ")[1];
            } else {
                title = titleDT.getWholeText();
                audiences = "Grades " + ((DomText) basePage.getFirstByXPath("//div[@class='cs-content']/div[1]/div[2]/div/h2/span/text()")).getWholeText().split("Grades ")[1];
            }
            System.out.println(title +": "+ audiences);
            //TODO: make the audience identifier better
        }
    }
}
