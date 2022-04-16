public class WaterlooProgram {
    private String focus, title, href, audiences, data, overview;

    public WaterlooProgram() {
        this.focus = "";
        this.title = "";
        this.href = "";
        this.audiences = "";
        this.data = "";
        this.overview = "";
    }

    public WaterlooProgram(String focus, String title, String href, String audiences, String data, String overview) {
        this.focus = focus;
        this.title = title;
        this.href = href;
        this.audiences = audiences;
        this.data = data;
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

    public void setData(String data) {
        this.data = data;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }
}
