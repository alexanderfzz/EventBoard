package Scraping;

import java.util.LinkedList;

public class Program {
    private final String university;
    private String title, link, audiences, overview, formattedDate;
    private LinkedList<String> topic, dates;

    public Program() {
        this.university = "";
        this.topic = new LinkedList<>();
        this.title = "";
        this.link = "";
        this.audiences = "";
        this.dates = new LinkedList<>();
        this.overview = "";
    }

    public Program(String university, LinkedList<String> topic, String title, String link, String audiences, LinkedList<String> dates, String overview) {
        this.university = university;
        this.topic = topic;
        this.title = title;
        this.link = link;
        this.audiences = audiences;
        this.dates = dates;
        this.overview = overview;
    }
    public Program(String university, LinkedList<String> topic, String title, String link, String audiences, String date, String overview) {
        this.university = university;
        this.topic = topic;
        this.title = title;
        this.link = link;
        this.audiences = audiences;
        this.formattedDate = date;
        this.overview = overview;
    }

    public String getFormattedDate() {
        return formattedDate;
    }
    public String getUniversity() {
        return university;
    }

    public LinkedList<String> getTopic() {
        return topic;
    }

    public void setTopic(LinkedList<String> topic) {
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

    public String getDates() {
        if(dates.isEmpty() || dates.get(0).equals("")){
            return "Undetermined";
        }
        return String.join(",", dates);
    }

    public void setDates(LinkedList<String> dates) {
        this.dates = dates;
    }
}
