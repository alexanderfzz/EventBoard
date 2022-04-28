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
import java.util.stream.Collectors;

public class McMasterScraper  implements Runnable{
    private WebClient webClient;
    private HtmlPage page;
    private String url;
    private McMasterProgram[] programs;

    public McMasterScraper() throws IOException {
        this.webClient = new WebClient();
        this.webClient.getOptions().setCssEnabled(false);
        this.webClient.getOptions().setJavaScriptEnabled(false);
        this.url = "https://youthprograms.eng.mcmaster.ca/programs/list";
        this.page = this.webClient.getPage(this.url);
    }

    @Override
    public void run() {
        String[] focusFilter = {"Engineering", "Science", "Math", "Technology", "Coding", "Leaders"};
        focusFilter = Arrays.stream(focusFilter)
                .map(String::toLowerCase).toArray(String[]::new);
        String[] audienceFilter = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "/", "-", "K", "(", ")"};

        List<HtmlListItem> pages = page.getByXPath("//div[@class = 'item-list']/ul/li");
        int totalPages = pages.size()-2 <= 0 ? pages.size() : pages.size()-2;
        LinkedList<McMasterProgram> programList = new LinkedList<>();
        Set<String> hrefSet = new HashSet<>();
        for (int i=0; i<totalPages; i++) {
            if (i!=0) {
                HtmlAnchor nextPage = (HtmlAnchor) page.getByXPath("//div[@class = 'item-list']/ul/li[" + (i+1) + "]/a").get(0);
                try {
                    this.page = nextPage.click();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            List<DomText> titles = page.getByXPath("//div[@id = 'news-row']/div/div[@class = 'card']/h2/a/text()");

            for (DomText j : titles) {
                String currentTitle = j.getWholeText();
                String apath = String.format("//div[@id = 'news-row']/div/div[@class = 'card']/h2/a[text() = '%s']", currentTitle);

                //intersect of two arrays
                Set<String> titleSet = new HashSet<>(Arrays.asList(currentTitle.toLowerCase().split(" ")));
                Set<String> focusSet = new HashSet<>(Arrays.asList(focusFilter));
                titleSet.retainAll(focusSet);
                String focus = String.join("/", titleSet.toArray(new String[0]));

                String[] titleArray = currentTitle.split(" ");
                boolean valid = false;
                for (int index=0; index<audienceFilter.length; index++) {
                    valid = titleArray[titleArray.length-1].contains(audienceFilter[index]);
                    if (valid) break;
                }
                String audiences = valid ? titleArray[titleArray.length-1].replace("(", "").replace(")", "") : "";

                //navigating into the subpage
                HtmlAnchor subpage = (HtmlAnchor) page.getByXPath(apath).get(0);
                HtmlPage masterpage = this.page.getPage();
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

                //exit subpage
                this.page = masterpage;

//                System.out.println(currentTitle);
//                System.out.println(href);
//                System.out.println(overview);
//                for (String k : dates) {
//                    System.out.println(k);
//                }
//                System.out.println();

                if (Toolbox.isADupelicate(hrefSet, href)) {
                    programList.add(new McMasterProgram(focus, currentTitle, href, audiences, dates, overview));
                }
            }
        }
        this.programs = programList.toArray(McMasterProgram[]::new);
        try {
            this.export();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void export() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        for (McMasterProgram i : this.programs) {
            System.out.println(Toolbox.ObjectToJSON(objectMapper, i));
        }
    }
}
