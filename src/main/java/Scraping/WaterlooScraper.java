package Scraping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class WaterlooScraper implements Runnable {
    private WebClient webClient;
    private HtmlPage page;
    private static final List<Program> programs;
    private static final Set<String> hrefSet;

    static {
        programs = new LinkedList<>();
        hrefSet = new HashSet<>();
    }

    public WaterlooScraper() throws IOException {
        this.webClient = new WebClient();
        this.webClient.getOptions().setCssEnabled(false);
        this.webClient.getOptions().setJavaScriptEnabled(false);
        String url = "https://uwaterloo.ca/future-students/visit-waterloo/high-school-enrichment-programs";
        this.page = this.webClient.getPage(url);
    }

    @Override
    public void run() {
        List<DomText> titles = page.getByXPath("//table[@class = 'tablesaw tablesaw-stack']/tbody/tr/td[1]/a/text()");

        for (DomText i : titles) {
            String currentTitle = i.getWholeText();
            String apath = String.format("//table[@class = 'tablesaw tablesaw-stack']/tbody/tr/td[1]/a[text() = '%s']", currentTitle);

            String href = ((DomAttr) page.getByXPath(apath + "/@href").get(0)).getValue();

            if (!Toolbox.isNotDuplicate(hrefSet, href)) {
                continue;
            }

            String audiences = page.getByXPath(apath + "/ancestor::tr/td[2]/text()").get(0).toString();
            LinkedList<String> dates = new LinkedList<>();
            dates.add(page.getByXPath(apath + "/ancestor::tr/td[3]/text()").get(0).toString());
            String overview = page.getByXPath(apath + "/ancestor::tr/td[4]/text()").get(0).toString();
            String focusAsString = ((HtmlElement) page.getByXPath(apath + "/ancestor::tr/th").get(0)).getTextContent();
            LinkedList<String> focus = Arrays.stream(focusAsString.split("\sand\s|,"))
                    .map(String::trim)
                    .collect(Collectors.toCollection(LinkedList::new));
            focus.removeAll(List.of(new String[]{""}));

            programs.add(new Program("Waterloo", focus, currentTitle.replace("and", "").trim(), href, audiences, dates, overview));
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
}
