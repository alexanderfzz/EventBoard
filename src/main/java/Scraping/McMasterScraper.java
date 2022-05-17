package Scraping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlListItem;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class McMasterScraper implements Runnable {
    private WebClient webClient;
    private HtmlPage page;
    private String url;
    private static final List<Program> programs = Collections.synchronizedList(new LinkedList<>());
    private static final Set<String> hrefSet = ConcurrentHashMap.newKeySet();

    public McMasterScraper() throws IOException {
        this.webClient = new WebClient();
        this.webClient.getOptions().setCssEnabled(false);
        this.webClient.getOptions().setJavaScriptEnabled(false);
        this.url = "https://youthprograms.eng.mcmaster.ca/programs/list";
        this.page = this.webClient.getPage(this.url);
    }

    @Override
    public void run() {
        List<HtmlListItem> pages = page.getByXPath("//div[@class = 'item-list']/ul/li");
        LinkedList<Thread> threads = new LinkedList<>();
        int totalPages = pages.size()-2 <= 0 ? pages.size() : pages.size()-2;

        try {
            for (int i=0; i<totalPages; i++) {
                McMasterPageScraper mcMasterPageScraper;
                Thread pageScraperThread;
                mcMasterPageScraper = new McMasterPageScraper("https://youthprograms.eng.mcmaster.ca/programs/list?page="+i);
                pageScraperThread = new Thread(mcMasterPageScraper);
                threads.add(pageScraperThread);
                pageScraperThread.start();
            }
            for (Thread thread : threads) {
                thread.join();
            }
            export();
            System.out.println(programs.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void export() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        for (Program i : programs) {
            System.out.println(Toolbox.ObjectToJSON(objectMapper, i));
        }
    }


    private static class McMasterPageScraper implements Runnable {
        private WebClient webClient;
        private HtmlPage page;
        private String url;
        private String[] focusFilter = {"Engineering", "Science", "Math", "Technology", "Coding", "Leaders"};
        private String[] audienceFilter = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "/", "-", "K", "(", ")"};

        public McMasterPageScraper(String url) throws IOException {
            this.webClient = new WebClient();
            this.url = url;
            this.webClient.getOptions().setCssEnabled(false);
            this.webClient.getOptions().setJavaScriptEnabled(false);
            this.page = this.webClient.getPage(this.url);
        }

        @Override
        public void run() {
            List<DomText> titles = page.getByXPath("//div[@id = 'news-row']/div/div[@class = 'card']/h2/a/text()");
            for (DomText j : titles) {
                String currentTitle = j.getWholeText();
                String apath = String.format("//div[@id = 'news-row']/div/div[@class = 'card']/h2/a[text() = '%s']", currentTitle);

                focusFilter = Arrays.stream(focusFilter).map(String::toLowerCase).toArray(String[]::new);

                //intersection
                Set<String> titleSet = new HashSet<>(Arrays.asList(currentTitle.toLowerCase().split(" ")));
                Set<String> focusSet = new HashSet<>(Arrays.asList(focusFilter));
                titleSet.retainAll(focusSet);
                String focus = String.join("/", titleSet.toArray(new String[0]));

                String[] titleArray = currentTitle.split(" ");
                boolean valid = false;
                for (String s : audienceFilter) {
                    valid = titleArray[titleArray.length - 1].contains(s);
                    if (valid) break;
                }
                String audiences = valid ? titleArray[titleArray.length-1].replace("(", "").replace(")", "") : "";

                //nav to subpage
                HtmlAnchor subpage = (HtmlAnchor) page.getByXPath(apath).get(0);
                HtmlPage masterPage = this.page;
                try {
                    this.page = subpage.click();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String overview = String.valueOf(page.getByXPath("//div[@class = 'content']/div/div/div[@class = 'row']/div/p[2]/text()").get(0));
                String href = this.page.getUrl().toString();
                LinkedList<String> dates = page.getByXPath("//div[@class = 'content']/div/div/div[@class = 'row']/div/div/div[@class = 'view-content']/table/tbody/tr/td[@class = 'lead']/strong/text()")
                        .stream()
                        .map(o -> (DomText) o)
                        .map(DomText::getWholeText).collect(Collectors.toCollection(LinkedList::new));

                //nav back to masterPage
                this.page = masterPage;

                if (Toolbox.isADuplicate(hrefSet, href)) {
                    programs.add(new Program("McMaster", focus, currentTitle, href, audiences, dates, overview));
                }
            }
        }
    }
}
