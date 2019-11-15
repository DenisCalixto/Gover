package edu.wmdd.gover;

import java.util.Date;

public class Inspection {

    private String inspector_name;
    private Date inspection_date;
    private String property_name;
    private String notes;
    private int id;
    private String image_url;

    public Inspection() {

    }

    public String getInspectorName() {
        return inspector_name;
    }

    public void setInspectorName(String inspector_name) {
        this.inspector_name = inspector_name;
    }

    public Date getInspectionDate() {
        return inspection_date;
    }

    public void setInspectionDate(Date inspection_date) {
        this.inspection_date = inspection_date;
    }

    public String getPropertyName() {
        return property_name;
    }

    public void setPropertyName(String property_name) {
        this.property_name = property_name;
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