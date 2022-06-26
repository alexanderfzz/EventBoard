package Scraping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private static final List<Program> programs;
    private static final Set<String> hrefSet;

    static {
        programs = Collections.synchronizedList(new LinkedList<>());
        hrefSet = ConcurrentHashMap.newKeySet();
    }

    public McMasterScraper() throws IOException {
        this.webClient = new WebClient();
        this.webClient.getOptions().setCssEnabled(false);
        this.webClient.getOptions().setJavaScriptEnabled(false);
        String url = "https://youthprograms.eng.mcmaster.ca/programs/list";
        this.page = this.webClient.getPage(url);
    }

    @Override
    public void run() {
        List<HtmlListItem> pages = page.getByXPath("//div[@class = 'item-list']/ul/li");
        LinkedList<Thread> threads = new LinkedList<>();
        int totalPages = pages.size() - 2 <= 0 ? pages.size() : pages.size() - 2;
        for (int i = 0; i < totalPages; i++) {
            McMasterPageScraper mcMasterPageScraper = null;
            try {
                mcMasterPageScraper = new McMasterPageScraper("https://youthprograms.eng.mcmaster.ca/programs/list?page=" + i);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Thread pageScraperThread = new Thread(mcMasterPageScraper);
            threads.add(pageScraperThread);
            pageScraperThread.start();
        }

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


    private static class McMasterPageScraper implements Runnable {
        private WebClient webClient;
        private HtmlPage page;
        private static final HashMap<String, String> focusFilter;
        private static final String[] audienceFilter;

        static {
            focusFilter = new HashMap<>();
            focusFilter.put("engineering", "Engineering");
            focusFilter.put("science", "Science");
            focusFilter.put("math", "Math");
            focusFilter.put("technology", "Technology");
            focusFilter.put("coding", "Coding");
            focusFilter.put("leaders", "Leadership");
            focusFilter.put("stem", "STEM");
            audienceFilter = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "/", "-", "K", "(", ")"};
        }

        private McMasterPageScraper(String url) throws IOException {
            this.webClient = new WebClient();
            this.webClient.getOptions().setCssEnabled(false);
            this.webClient.getOptions().setJavaScriptEnabled(false);
            this.page = this.webClient.getPage(url);
        }

        @Override
        public void run() {
            List<DomText> titles = page.getByXPath("//div[@id = 'news-row']/div/div[@class = 'card']/h2/a/text()");
            for (DomText j : titles) {
                String currentTitle = j.getWholeText();
                String apath = String.format("//div[@id = 'news-row']/div/div[@class = 'card']/h2/a[text() = '%s']", currentTitle);

                //intersection
                Set<String> titleSet = new HashSet<>(Arrays.asList(currentTitle.toLowerCase().split(" ")));
                titleSet.retainAll(focusFilter.keySet());
                titleSet = titleSet.stream().map(s -> s = focusFilter.get(s)).collect(Collectors.toSet());
                LinkedList<String> focus = new LinkedList<>(titleSet);

                String[] titleArray = currentTitle.split(" ");
                boolean valid = false;
                for (String s : audienceFilter) {
                    valid = titleArray[titleArray.length - 1].contains(s);
                    if (valid) break;
                }
                String audiences = valid ? titleArray[titleArray.length - 1].replace("(", "").replace(")", "") : "";

                //nav to subpage
                HtmlAnchor subpage = (HtmlAnchor) page.getByXPath(apath).get(0);
                HtmlPage masterPage = this.page;
                try {
                    this.page = subpage.click();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String href = this.page.getUrl().toString();

                if (!Toolbox.isNotDuplicate(hrefSet, href)) {
                    this.page = masterPage;
                    continue;
                }

                String overview = String.valueOf(page.getByXPath("//div[@class = 'content']/div/div/div[@class = 'row']/div/p[2]/text()").get(0));
                LinkedList<String> dates = page.getByXPath("//div[@class = 'content']/div/div/div[@class = 'row']/div/div/div[@class = 'view-content']/table/tbody/tr/td[@class = 'lead']/strong/text()")
                        .stream()
                        .map(o -> (DomText) o)
                        .map(DomText::getWholeText).collect(Collectors.toCollection(LinkedList::new));

                //nav back to masterPage
                this.page = masterPage;

                programs.add(new Program("McMaster", focus, currentTitle, href, audiences, dates, overview));

            }
        }
    }
}
