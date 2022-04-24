package Scraping;

import java.io.Serializable;
import java.util.LinkedList;

public class WaterlooProgram implements Serializable {
    private final String university = "Waterloo";
    private String topic, title, link, audiences, overview;
    private LinkedList<String> dates;

    public WaterlooProgram() {
        this.topic = "";
        this.title = "";
        this.link = "";
        this.audiences = "";
        this.dates = new LinkedList<String>();
        this.overview = "";
    }

    public WaterlooProgram(String topic, String title, String link, String audiences, LinkedList<String> dates, String overview) {
        this.topic = topic;
        this.title = title;
        this.link = link;
        this.audiences = audiences;
        this.dates = dates;
        this.overview = overview;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(String link) {
        this.link = link;
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

    public String getTopic() {
        return topic;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
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
