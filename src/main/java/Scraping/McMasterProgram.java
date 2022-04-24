package Scraping;

import java.util.LinkedList;

public class McMasterProgram {
    private String focus, title, href, audiences, overview;
    private LinkedList<String> dates;

    public McMasterProgram() {
        this.focus = "";
        this.title = "";
        this.href = "";
        this.audiences = "";
        this.dates = new LinkedList<String>();
        this.overview = "";
    }

    public McMasterProgram(String focus, String title, String href, String audiences, LinkedList<String> dates, String overview) {
        this.focus = focus;
        this.title = title;
        this.href = href;
        this.audiences = audiences;
        this.dates = dates;
        this.overview = overview;
    }

    public String getFocus() {
        return focus;
    }

    public void setFocus(String focus) {
        this.focus = focus;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getAudiences() {
        return audiences;
    }

    public void setAudiences(String audiences) {
        this.audiences = audiences;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public LinkedList<String> getDates() {
        return dates;
    }

    public void setDates(LinkedList<String> dates) {
        this.dates = dates;
    }
}
