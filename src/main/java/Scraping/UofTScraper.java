package Scraping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;

import java.io.IOException;
import java.util.*;

public class UofTScraper implements Runnable {
    private WebClient webClient;
    private HtmlPage page;
    private String url;
    private Program[] programs;

    public UofTScraper() throws IOException {
        this.webClient = new WebClient();
        this.webClient.getOptions().setCssEnabled(false);
        this.webClient.getOptions().setJavaScriptEnabled(false);
        this.url = "https://future.utoronto.ca/academics/pre-university-programs/enrichment-summer-program-for-high-school-students/";
        this.page = this.webClient.getPage(this.url);
    }

    @Override
    public void run() {
        List<DomText> titles = page.getByXPath("//div[@class = 'container']/div[@class = 'row']//main/h3//a/text()");
        LinkedList<Program> programList = new LinkedList<>();
        Set<String> hrefSet = new HashSet<>();

        for (DomText i : titles) {
            String currentTitle = i.getWholeText();
            String apath = String.format("//div[@class = 'container']/div[@class = 'row']//main/h3//a[text() = '%s']", currentTitle);

            String href = ((DomAttr) page.getByXPath(apath + "/@href").get(0)).getValue();
            String firstP = ((HtmlParagraph) page.getByXPath(apath + "/ancestor::h3/following-sibling::p[1]").get(0)).asXml();
            String overview = ((HtmlParagraph) page.getByXPath(apath + "/ancestor::h3/following-sibling::p[2]").get(0)).getTextContent();

            firstP = firstP.replace("<strong>", "")
                    .replace("</strong>", "")
                    .replace("&amp;", "&")
                    .replace("<p>", "")
                    .replace("</p>", "")
                    .replace("\t", "")
                    .replace("\n", "");

            String[] preHashMap = firstP.split("<br/>");
            HashMap<String, String> firstPHashMap = new HashMap<>();
            for (String s : preHashMap) {
                String[] temp = s.split(":");
                if (temp.length == 1) continue;
                firstPHashMap.put(temp[0].trim(), temp[1].trim());
            }

            String focus = firstPHashMap.get("Campus & Department") == null ? "" : firstPHashMap.get("Campus & Department");
            String audiences = firstPHashMap.get("Age Group") == null ? "" : firstPHashMap.get("Age Group");
            LinkedList<String> dates = new LinkedList<>();
            dates.add(firstPHashMap.get("Program Dates") == null ? "" : firstPHashMap.get("Program Dates"));

//            System.out.println(count +" "+currentTitle);
//            System.out.println(href);
//            System.out.println(audiences);
//            System.out.println(dates);
//            System.out.println(overview);
//            System.out.println(focus);
//            System.out.println();

            if (Toolbox.isADupelicate(hrefSet, href)) {
                programList.add(new Program("UofT", focus, currentTitle, href, audiences, dates, overview));
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
