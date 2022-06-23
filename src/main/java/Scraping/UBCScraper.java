package Scraping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UBCScraper implements Runnable {
    private WebClient webClient;
    private HtmlPage page;
    private String url;
    private static final List<Program> programs = Collections.synchronizedList(new LinkedList<>());
    private static final Set<String> hrefSet = ConcurrentHashMap.newKeySet();
    private static final HashMap<String, String> focusFilter = new HashMap<>();

    static {
        focusFilter.put("digital media", "Digital Media");
        focusFilter.put("entrepreneurship", "Business");
        focusFilter.put("business", "Business");
        focusFilter.put("economy", "Business");
        focusFilter.put("finance", "Business");
        focusFilter.put("video game", "Business");
        focusFilter.put("artificial intelligence", "Computer Science");
        focusFilter.put("machine learning", "Computer Science");
        focusFilter.put(" ai", "Computer Science");
        focusFilter.put("ai ", "Computer Science");
        focusFilter.put("python", "Computer Science");
        focusFilter.put("data analysis", "Computer Science");
        focusFilter.put("science", "Science");
        focusFilter.put("engineering", "Engineering");
        focusFilter.put("health", "Health");
        focusFilter.put("politics", "Politics");
        focusFilter.put("art", "Art");
        focusFilter.put("crime", "Social Science");
        focusFilter.put("society", "Social Science");
        focusFilter.put("social", "Social Science");
        focusFilter.put("philosophy", "Social Science");
        focusFilter.put("psychology", "Social Science");
    }

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
            UBCOnlineScraper uBCOnlineScraper = new UBCOnlineScraper();
            Thread onlineScraperThread = new Thread(uBCOnlineScraper);
            threads.add(onlineScraperThread);
            onlineScraperThread.start();
            UBCOnCampusScraper uBCOnCampusScraper = new UBCOnCampusScraper();
            Thread onCampusScraperThread = new Thread(uBCOnCampusScraper);
            threads.add(onCampusScraperThread);
            onCampusScraperThread.start();
            for (Thread thread : threads) {
                thread.join();
            }
            export();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void baseScrape(HtmlPage page, String topic, String format) {
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
        if (topic.equals("")) {
            topic = applyFocusFilter(title);
        }
        programs.add(new Program("UBC", topic, format + " " + title, page.getBaseURI(), audiences, dates, overview));
    }

    public static String applyFocusFilter(String title) {
        //TODO: this
        Set<String> focusSet = new HashSet<>();
        for (String keyword : focusFilter.keySet()) {
            if (title.toLowerCase().contains(keyword)) {
                focusSet.add(focusFilter.get(keyword));
            }
        }
        return String.join("/",focusSet);
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


    public static class UBCOnCampusScraper implements Runnable {
        private WebClient webClient;
        private HtmlPage page;
        private String url = "https://extendedlearning.ubc.ca/programs/future-global-leaders-oncampus/registration-dates";

        public UBCOnCampusScraper() throws IOException {
            this.webClient = new WebClient();
            this.webClient.getOptions().setCssEnabled(false);
            this.webClient.getOptions().setJavaScriptEnabled(false);
            this.page = this.webClient.getPage(url);
        }

        @Override
        public void run() {
            List<HtmlTableRow> tableRows = page.getByXPath("//div/div[4]/table/tbody[1]/tr");
            processTableRowList(tableRows, this.webClient);
            //TODO: this second table also needs a filter for the topic attribute
            tableRows = page.getByXPath("//div/div[4]/table/tbody[2]/tr");
            processTableRowList(tableRows, this.webClient);
        }

        public static void processTableRowList(List<HtmlTableRow> tableRows, WebClient webClient) {
            String currentFocus = "";
            for (HtmlTableRow htmlTableRow : tableRows) {
                String classAttr = htmlTableRow.getAttribute("class");
                if (classAttr.equals("course-topic")) {
                    currentFocus = htmlTableRow.getFirstElementChild().getTextContent();
                    continue;
                }
                Iterable<DomElement> iterable = htmlTableRow.getChildElements();
                Iterator<DomElement> iter = iterable.iterator();
                iter.next();
                try {
                    while (iter.hasNext()) {
                        DomElement currentChildNode = iter.next();
                        DomElement a = currentChildNode.getFirstElementChild();
                        if (a != null && Toolbox.isNotDuplicate(hrefSet, "https://extendedlearning.ubc.ca" + a.getAttribute("href"))) {
                            baseScrape(webClient.getPage("https://extendedlearning.ubc.ca" + a.getAttribute("href")), currentFocus, "(On-Campus)");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static class UBCOnlineScraper implements Runnable {
        private WebClient webClient;
        private HtmlPage page;
        private String summerUrl = "https://extendedlearning.ubc.ca/programs/future-global-leaders-online-summer";
        private String fallUrl = "https://extendedlearning.ubc.ca/programs/future-global-leaders-online-fall";

        public UBCOnlineScraper() throws IOException {
            this.webClient = new WebClient();
            this.webClient.getOptions().setCssEnabled(false);
            this.webClient.getOptions().setJavaScriptEnabled(false);
            this.page = this.webClient.getPage(this.summerUrl);
        }

        @Override
        public void run() {
            List<HtmlAnchor> anchors = page.getByXPath("//*[@id='fgl-terms']/div/div/div[2]/p[2]/a");
            HtmlPage masterPage = this.page;
            processAnchorList(this.webClient, this.page, anchors);
            this.page = masterPage;
            anchors = page.getByXPath("//*[@id='fgl-page']/div/div/div/div[2]/div[2]/p/a");
            processAnchorList(this.webClient, this.page, anchors);
            try {
                this.page = this.webClient.getPage(fallUrl);
                anchors = page.getByXPath("//*[@id='fgl-terms']/div/div/div[2]/p[2]/a");
                processAnchorList(this.webClient, this.page, anchors);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private static void processAnchorList(WebClient webClient, HtmlPage page, List<HtmlAnchor> anchors) {
            try {
                for (HtmlAnchor anchor : anchors) {
                    String absoluteURL = "https://extendedlearning.ubc.ca" + anchor.getHrefAttribute();
                    if (!Toolbox.isNotDuplicate(hrefSet, absoluteURL)) {
                        continue;
                    }
                    page = webClient.getPage(absoluteURL);
                    //TODO: create filter for topic attribute
                    baseScrape(page, "", "(Online)");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
