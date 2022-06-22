package Scraping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UBCScraper implements Runnable {
    private WebClient webClient;
    private HtmlPage page;
    private String url;
    private static final List<Program> programs = Collections.synchronizedList(new LinkedList<>());
    private static final Set<String> hrefSet = ConcurrentHashMap.newKeySet();

    public UBCScraper() throws IOException {
        this.webClient = new WebClient();
        this.webClient.getOptions().setCssEnabled(false);
        this.webClient.getOptions().setJavaScriptEnabled(false);
        this.url = "https://extendedlearning.ubc.ca/programs/future-global-leaders";
        this.page = this.webClient.getPage(this.url);
    }

    @Override
    public void run() {
        LinkedList<Thread> threads = new LinkedList<>();
        try {
            SummerOnlineScraper summerOnlineScraper = new SummerOnlineScraper();
            Thread SOScraperThread = new Thread(summerOnlineScraper);
            threads.add(SOScraperThread);
            SOScraperThread.start();
            //TODO: add fallOnline and onCampus threads
            for (Thread thread : threads) {
                thread.join();
            }
            export();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void baseScrape(HtmlPage page) {
        String apath = "//div/div/div[@id = 'content']/section[@class = 'region-main']";
        String title = ((DomText) page.getFirstByXPath(apath + "/h1/text()")).getWholeText();
        String audiences = "ages" + ((DomText) page.getFirstByXPath("//article/div/p[1]/em[2]/text()")).getWholeText().split("ages")[1].replace(".", "");
        String overview = ((DomText) page.getFirstByXPath("//article/div/p[2]/text()")).getWholeText();

        int dateSize = page.getByXPath("//section[@id='block-views-section-drawer-drawer-courses']/div/div/div[2]/div").size();
        LinkedList<String> dates = new LinkedList<>();
        for (int i = 0; i < dateSize; i++) {
            DomText firstDate = page.getFirstByXPath("//section/div/div/div[2]/div[" + (i + 1) + "]/div/span/div[1]/span[1]/text()");
            DomText lastDate = page.getFirstByXPath("//section/div/div/div[2]/div[" + (i + 1) + "]/div/span/div[1]/span[2]/text()");
            dates.add(firstDate.getWholeText() + "-" + lastDate);
        }
        programs.add(new Program("UBC", "", title, page.getBaseURI(), audiences, dates, overview));
    }

    public void export() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        for (Program i : programs) {
            System.out.println(Toolbox.ObjectToJSON(objectMapper, i));
        }
    }

    public static List<Program> getPrograms() {
        return programs;
    }


    public static class SummerOnlineScraper implements Runnable {
        private WebClient webClient;
        private HtmlPage page;
        private String url = "https://extendedlearning.ubc.ca/programs/future-global-leaders-online-summer";

        public SummerOnlineScraper() throws IOException {
            this.webClient = new WebClient();
            this.webClient.getOptions().setCssEnabled(false);
            this.webClient.getOptions().setJavaScriptEnabled(false);
            this.page = this.webClient.getPage(this.url);
        }

        @Override
        public void run() {
            List<HtmlAnchor> anchors = page.getByXPath("//*[@id='fgl-terms']/div/div/div/p/a");
            HtmlPage masterPage = this.page;
            processAnchorList(this.webClient, this.page, anchors);
            this.page = masterPage;
            anchors = page.getByXPath("//*[@id='fgl-page']/div/div/div/div/div/p/a");
            processAnchorList(this.webClient, this.page, anchors);
        }

        private static void processAnchorList(WebClient webClient, HtmlPage page, List<HtmlAnchor> anchors) {
            try {
                for (HtmlAnchor anchor : anchors) {
                    String absoluteURL = "https://extendedlearning.ubc.ca" + anchor.getHrefAttribute();
                    if (!Toolbox.isNotDuplicate(hrefSet, absoluteURL)) {
                        continue;
                    }
                    page = webClient.getPage(absoluteURL);
                    baseScrape(page);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
