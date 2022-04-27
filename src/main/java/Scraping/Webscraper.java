package Scraping;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

import javax.swing.text.html.HTML;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Webscraper {
    private WebClient webClient = new WebClient();
    private HtmlPage page;
    private String url;

    public Webscraper () {
        this.webClient.getOptions().setCssEnabled(false);
        //disabling JS will result in some changes in the hierarchy of HTML elements, presumably because some JS scripts mess with the HTML structure
        //this.webClient.getOptions().setJavaScriptEnabled(false);
    }


    public WaterlooProgram[] waterlooExtract() throws IOException {
        this.url = "https://uwaterloo.ca/future-students/visit-waterloo/high-school-enrichment-programs";
        this.page = this.webClient.getPage(this.url);

        List<DomText> titles = page.getByXPath("//table[@class = 'tablesaw tablesaw-stack']/tbody/tr/td[1]/span/a/text()");
        WaterlooProgram[] programs = new WaterlooProgram[titles.size()];
        int count = 0;

        for (DomText i : titles) {
            count++;
            String currentTitle = i.getWholeText();
            String apath = String.format("//table[@class = 'tablesaw tablesaw-stack']/tbody/tr/td[1]/span/a[text() = '%s']", currentTitle);

            String href = ((DomAttr) page.getByXPath(apath +"/@href").get(0)).getValue();
            String audiences = page.getByXPath(apath + "/ancestor::tr/td[2]/span/text()").get(0).toString();
            LinkedList<String> dates = new LinkedList<>();
            dates.add(page.getByXPath(apath + "/ancestor::tr/td[3]/span/text()").get(0).toString());
            String overview = page.getByXPath(apath + "/ancestor::tr/td[4]/span/text()").get(0).toString();
            String focus = page.getByXPath(apath + "/ancestor::tr/th/span/text()").get(0).toString();

//            System.out.println(currentTitle);
//            System.out.println(href);
//            System.out.println(audiences);
//            System.out.println(dates);
//            System.out.println(overview);
//            System.out.println(focus);
//            System.out.println();

            programs[count-1] = new WaterlooProgram(focus, currentTitle, href, audiences, dates, overview);
        }
        return programs;
    }


    public UofTProgram[] UofTExtract() throws IOException {
        this.url = "https://future.utoronto.ca/academics/pre-university-programs/enrichment-summer-program-for-high-school-students/";
        boolean previousJSState = this.webClient.getOptions().isJavaScriptEnabled();
        this.webClient.getOptions().setJavaScriptEnabled(false);
        this.page = this.webClient.getPage(this.url);

        List<DomText> titles = page.getByXPath("//div[@class = 'container']/div[@class = 'row']//main/h3//a/text()");
        UofTProgram[] programs = new UofTProgram[titles.size()];
        int count = 0;

        for (DomText i : titles) {
            count++;
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

            programs[count-1] = new UofTProgram(focus, currentTitle, href, audiences, dates, overview);
        }
        this.webClient.getOptions().setJavaScriptEnabled(previousJSState);
        return programs;
    }


    public McMasterProgram[] mcMasterExtract() throws IOException {
        this.url = "https://youthprograms.eng.mcmaster.ca/programs/list";
        this.webClient.getOptions().setJavaScriptEnabled(false);
        this.page = this.webClient.getPage(this.url);

        String[] focusFilter = {"Engineering", "Science", "Math", "Technology", "Coding", "Leaders"};
        focusFilter = Arrays.stream(focusFilter)
                .map(String::toLowerCase).toArray(String[]::new);
        Character[] audienceFilter = {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '/', '-', 'K'};

        List<HtmlListItem> pages = page.getByXPath("//div[@class = 'item-list']/ul/li");
        int totalPages = pages.size()-2 <= 0 ? pages.size() : pages.size()-2;
        int count = 0;
        LinkedList<McMasterProgram> programs = new LinkedList<>();
        for (int i=0; i<totalPages; i++) {
            if (i!=0) {
                HtmlAnchor nextPage = (HtmlAnchor) page.getByXPath("//div[@class = 'item-list']/ul/li[" + (i+1) + "]/a").get(0);
                this.page = nextPage.click();
            }

            List<DomText> titles = page.getByXPath("//div[@id = 'news-row']/div/div[@class = 'card']/h2/a/text()");

            for (DomText j : titles) {
                count++;
                String currentTitle = j.getWholeText();
                String apath = String.format("//div[@id = 'news-row']/div/div[@class = 'card']/h2/a[text() = '%s']", currentTitle);

                //intersect of two arrays
                Set<String> titleSet = new HashSet<>(Arrays.asList(currentTitle.toLowerCase().split(" ")));
                Set<String> focusSet = new HashSet<>(Arrays.asList(focusFilter));
                titleSet.retainAll(focusSet);
                String focus = String.join("/", titleSet.toArray(new String[0]));

                List<Character> titleFilterList = new LinkedList<>();
                for (char c : currentTitle.toCharArray()) {
                    titleFilterList.add(c);
                }
                Set<Character> titleSet2 = new LinkedHashSet<>(titleFilterList);
                Set<Character> audienceSet = new HashSet<>(Arrays.asList(audienceFilter));
                titleSet2.retainAll(audienceSet);
                String audiences = String.join("", titleSet2.stream().map(String::valueOf).toArray(String[]::new));

                //navigating into the subpage
                HtmlAnchor subpage = (HtmlAnchor) page.getByXPath(apath).get(0);
                HtmlPage masterpage = this.page.getPage();
                this.page = subpage.click();

                String overview = String.valueOf(page.getByXPath("//div[@class = 'content']/div/div/div[@class = 'row']/div/p[2]/text()").get(0));
                String href = this.page.getUrl().toString();
                LinkedList<String> dates = page.getByXPath("//div[@class = 'content']/div/div/div[@class = 'row']/div/div/div[@class = 'view-content']/table/tbody/tr/td[@class = 'lead']/strong/text()")
                        .stream()
                        .map(o -> (DomText) o)
                        .map(DomText::getWholeText).collect(Collectors.toCollection(LinkedList::new));

                //exit subpage
                this.page = masterpage;
                programs.add(new McMasterProgram(focus, currentTitle, href, audiences, dates, overview));


//                System.out.println(currentTitle);
//                System.out.println(href);
//                System.out.println(overview);
//                for (String k : dates) {
//                    System.out.println(k);
//                }
//                System.out.println();

            }
        }

        return programs.toArray(McMasterProgram[]::new);
    }
}
