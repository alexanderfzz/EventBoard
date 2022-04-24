package Scraping;

import java.io.Serializable;
import java.util.LinkedList;

public class UofTProgram implements Serializable {

    //focus: department
    //audiences: age group
    //date: program dates
    private final String university = "UofT";
    private String topic, title, link, audiences, overview;
    private LinkedList<String> dates;

    public UofTProgram() {
        this.topic = "";
        this.title = "";
        this.link = "";
        this.audiences = "";
        this.dates = new LinkedList<String>();
        this.overview = "";
    }

    public UofTProgram(String topic, String title, String link, String audiences, LinkedList<String> dates, String overview) {
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

    public LinkedList<String> getDates() {
        return dates;
    }

    public void setDates(LinkedList<String> dates) {
        this.dates = dates;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }
}
