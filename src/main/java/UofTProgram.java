public class UofTProgram {

    //focus: department
    //audiences: age group
    //date: program dates
    private String focus, title, href, audiences, date, overview;

    public UofTProgram() {
        this.focus = "";
        this.title = "";
        this.href = "";
        this.audiences = "";
        this.date = "";
        this.overview = "";
    }

    public UofTProgram(String focus, String title, String href, String audiences, String date, String overview) {
        this.focus = focus;
        this.title = title;
        this.href = href;
        this.audiences = audiences;
        this.date = date;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }
}
