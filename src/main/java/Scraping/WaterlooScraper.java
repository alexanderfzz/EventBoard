package Scraping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class WaterlooScraper implements Runnable {
    private WebClient webClient;
    private HtmlPage page;
    private String url;
    private Program[] programs;

    public WaterlooScraper() throws IOException {
        this.webClient = new WebClient();
        this.webClient.getOptions().setCssEnabled(false);
        this.webClient.getOptions().setJavaScriptEnabled(false);
        this.url = "https://uwaterloo.ca/future-students/visit-waterloo/high-school-enrichment-programs";
        this.page = this.webClient.getPage(this.url);
    }

    @Override
    public void run() {
        List<DomText> titles = page.getByXPath("//table[@class = 'tablesaw tablesaw-stack']/tbody/tr/td[1]/a/text()");
        LinkedList<Program> programList = new LinkedList<>();
        Set<String> hrefSet = new HashSet<>();

        for (DomText i : titles) {
            String currentTitle = i.getWholeText();
            String apath = String.format("//table[@class = 'tablesaw tablesaw-stack']/tbody/tr/td[1]/a[text() = '%s']", currentTitle);

            String href = ((DomAttr) page.getByXPath(apath +"/@href").get(0)).getValue();
            String audiences = page.getByXPath(apath + "/ancestor::tr/td[2]/text()").get(0).toString();
            LinkedList<String> dates = new LinkedList<>();
            dates.add(page.getByXPath(apath + "/ancestor::tr/td[3]/text()").get(0).toString());
            String overview = page.getByXPath(apath + "/ancestor::tr/td[4]/text()").get(0).toString();
            String focus = page.getByXPath(apath + "/ancestor::tr/th/text()").get(0).toString();

//            System.out.println(currentTitle);
//            System.out.println(href);
//            System.out.println(audiences);
//            System.out.println(dates);
//            System.out.println(overview);
//            System.out.println(focus);
//            System.out.println();

            if (Toolbox.isADupelicate(hrefSet, href)) {
                programList.add(new Program("Waterloo", focus, currentTitle, href, audiences, dates, overview));
            }
        }
        this.programs = programList.toArray(Program[]::new);
        try {
            this.export();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void export() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        for (Program i : this.programs) {
            System.out.println(Toolbox.ObjectToJSON(objectMapper, i));
        }
    }
}
