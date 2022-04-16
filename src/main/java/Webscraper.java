import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableDataCell;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Webscraper {
    private WebClient webClient = new WebClient();
    private HtmlPage page;
    private String url;

    public Webscraper (String url) throws IOException {
        this.url = url;
        page = getWebPage();
    }

    private HtmlPage getWebPage() throws IOException {
        webClient.getOptions().setCssEnabled(false);

        //disabling JS will result in some changes in the hierarchy of HTML elements, presumably because some JS scripts mess with the HTML structure
        webClient.getOptions().setJavaScriptEnabled(false);
        return webClient.getPage(this.url);
    }

    public WaterlooProgram[] extract() {
        List<DomText> titles = page.getByXPath("//table[@class = 'tablesaw tablesaw-stack']/tbody/tr/td[1]/a/text()");
        int count = 0;

        WaterlooProgram[] programs = new WaterlooProgram[titles.size()];

        for (DomText i : titles) {
            count++;
            String currentTitle = i.getWholeText();
            String apath = String.format("//table[@class = 'tablesaw tablesaw-stack']/tbody/tr/td[1]/a[text() = '%s']", currentTitle);

            String href = ((DomAttr) page.getByXPath(apath +"/@href").get(0)).getValue();
            String audiences = page.getByXPath(apath + "/ancestor::tr/td[2]/text()").get(0).toString();
            String dates = page.getByXPath(apath + "/ancestor::tr/td[3]/text()").get(0).toString();
            String overview = page.getByXPath(apath + "/ancestor::tr/td[4]/text()").get(0).toString();
            String focus = page.getByXPath(apath + "/ancestor::tr/th/text()").get(0).toString();

            programs[count-1] = new WaterlooProgram(focus, currentTitle, href, audiences, dates, overview);
        }

        return programs;
    }
}
