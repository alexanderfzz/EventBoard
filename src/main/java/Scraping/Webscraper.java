package Scraping;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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

        List<DomText> titles = page.getByXPath("//div[@id = 'news-row']/div/div[@class = 'card']/h2/a/text()");
        McMasterProgram[] programs = new McMasterProgram[titles.size()];
        int count = 0;

        for (DomText i : titles) {
            count++;
            String currentTitle = i.getWholeText();
            String apath = String.format("//div[@id = 'news-row']/div/div[@class = 'card']/h2/a[text() = '%s']", currentTitle);

            String href = ((DomAttr) page.getByXPath(apath + "/@href").get(0)).getValue();

            System.out.println(currentTitle);
            System.out.println(href);
            System.out.println();
        }
        return new McMasterProgram[0];
    }
}
