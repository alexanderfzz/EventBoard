public class WaterlooProgram {
    private String focus, title, href, audiences, date, overview;

    public WaterlooProgram() {
        this.focus = "";
        this.title = "";
        this.href = "";
        this.audiences = "";
        this.date = "";
        this.overview = "";
    }

    public WaterlooProgram(String focus, String title, String href, String audiences, String date, String overview) {
        this.focus = focus;
        this.title = title;
        this.href = href;
        this.audiences = audiences;
        this.date = date;
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

    public void setDate(String date) {
        this.date = date;
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

    public String getDate() {
        return date;
    }

    public String getOverview() {
        return overview;
    }
}
