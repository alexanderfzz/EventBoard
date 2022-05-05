package Scraping;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class UBCScraper implements Runnable {
    private WebClient webClient;
    private HtmlPage page;
    private String url;
    private Program[] programs;

    public UBCScraper() throws IOException {
        this.webClient = new WebClient();
        this.webClient.getOptions().setCssEnabled(false);
        this.webClient.getOptions().setJavaScriptEnabled(false);
        this.url = "https://extendedlearning.ubc.ca/programs/future-global-leaders";
        this.page = this.webClient.getPage(this.url);
    }

    @Override
    public void run() {
        LinkedList<Program> programList = new LinkedList<>();
        Set<String> hrefSet = new HashSet<>();
    }
}
