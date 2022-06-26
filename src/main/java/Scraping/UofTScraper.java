package Scraping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

import java.io.IOException;
import java.util.*;

public class UofTScraper implements Runnable {
    private WebClient webClient;
    private HtmlPage page;
    private static final List<Program> programs;
    private static final Set<String> hrefSet;
    private static final HashMap<String, String> focusFilter;

    static {
        programs = new LinkedList<>();
        hrefSet = new HashSet<>();
        focusFilter = new HashMap<>();
        focusFilter.put("physical education", "Physical Education");
        focusFilter.put("athletics", "Physical Education");
        focusFilter.put("design", "Architecture");
        focusFilter.put("architecture", "Architecture");
        focusFilter.put("engineering", "Engineering");
        focusFilter.put("drama", "Drama");
        focusFilter.put("physics", "Physics");
        focusFilter.put("math", "Mathematics");
        focusFilter.put("mathematics", "Mathematics");
        focusFilter.put("law", "Social Science");
        focusFilter.put("medicine", "Medicine");
    }

    public UofTScraper() throws IOException {
        this.webClient = new WebClient();
        this.webClient.getOptions().setCssEnabled(false);
        this.webClient.getOptions().setJavaScriptEnabled(false);
        String url = "https://future.utoronto.ca/academics/pre-university-programs/enrichment-summer-program-for-high-school-students/";
        this.page = this.webClient.getPage(url);
    }

    @Override
    public void run() {
        List<DomText> titles = page.getByXPath("//div[@class = 'container']/div[@class = 'row']//main/h3//a/text()");

        int index = 0;
        for (DomText i : titles) {
            String currentTitle = i.getWholeText();
            String apath = String.format("//div[@class = 'container']/div[@class = 'row']//main/h3[%d]//a", index+1);

            HtmlAnchor anchor = page.getFirstByXPath(apath);
            while (anchor == null) {
                index++;
                apath = String.format("//div[@class = 'container']/div[@class = 'row']//main/h3[%d]//a", index+1);
                anchor = page.getFirstByXPath(apath);
            }
            String href = anchor.getHrefAttribute();

            if (!Toolbox.isNotDuplicate(hrefSet, href)) {
                continue;
            }

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

            String focusAsString = firstPHashMap.get("Campus & Department") == null ? "" : firstPHashMap.get("Campus & Department");
            String audiences = firstPHashMap.get("Age Group") == null ? "" : firstPHashMap.get("Age Group");
            LinkedList<String> dates = new LinkedList<>();
            dates.add(firstPHashMap.get("Program Dates") == null ? "" : firstPHashMap.get("Program Dates"));

            Set<String> focusSet = new HashSet<>();
            for (String keyword : focusFilter.keySet()) {
                if (currentTitle.toLowerCase().contains(keyword) || focusAsString.toLowerCase().contains(keyword)) {
                    focusSet.add(focusFilter.get(keyword));
                }
            }
            LinkedList<String> focus = new LinkedList<>(focusSet);

            programs.add(new Program("UofT", focus, currentTitle, href, audiences, dates, overview));
            index++;
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
