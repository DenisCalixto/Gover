package edu.wmdd.gover;

public class Job {

    public String summary;
    public int id;
    private String image_url;

    public Job() {

    }

    public Job(String summary, int id, String image_url) {
        this.summary = summary;
        this.id = id;
        this.image_url = image_url;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getImage_url() {
        return image_url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}