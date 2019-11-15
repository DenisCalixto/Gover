package edu.wmdd.gover;

public class Property {

    private String name;
    private String notes;
    private int id;
    private String image_url;

    public Property() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getImage_url() {
        return image_url.replace("http:", "https:");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url.replace("http:", "https:");
    }

}