package Scraping;

import java.util.LinkedList;

public class McMasterProgram {
    private final String university = "McMaster";
    private String topic, title, link, audiences, overview;
    private LinkedList<String> dates;

    public McMasterProgram() {
        this.topic = "";
        this.title = "";
        this.link = "";
        this.audiences = "";
        this.dates = new LinkedList<String>();
        this.overview = "";
    }

    public McMasterProgram(String topic, String title, String link, String audiences, LinkedList<String> dates, String overview) {
        this.topic = topic;
        this.title = title;
        this.link = link;
        this.audiences = audiences;
        this.dates = dates;
        this.overview = overview;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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
