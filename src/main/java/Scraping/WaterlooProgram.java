package Scraping;

import java.io.Serializable;
import java.util.LinkedList;

public class WaterlooProgram implements Serializable {
    private String focus, title, href, audiences, overview;
    private LinkedList<String> dates;

    public WaterlooProgram() {
        this.focus = "";
        this.title = "";
        this.href = "";
        this.audiences = "";
        this.dates = new LinkedList<String>();
        this.overview = "";
    }

    public WaterlooProgram(String focus, String title, String href, String audiences, LinkedList<String> dates, String overview) {
        this.focus = focus;
        this.title = title;
        this.href = href;
        this.audiences = audiences;
        this.dates = dates;
        this.overview = overview;
    }

    public void setFocus(String focus) {
        this.focus = focus;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public void setAudiences(String audiences) {
        this.audiences = audiences;
    }

    public void setDates(LinkedList<String> dates) {
        this.dates = dates;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getFocus() {
        return focus;
    }

    public String getTitle() {
        return title;
    }

    public String getHref() {
        return href;
    }

    public String getAudiences() {
        return audiences;
    }

    public LinkedList<String> getDates() {
        return dates;
    }

    public String getOverview() {
        return overview;
    }
}
