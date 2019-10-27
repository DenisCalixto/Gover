package edu.wmdd.gover;

import java.util.List;

public class InspectionTemplateSection {

    private String name;
    private int id;
    private List<InspectionTemplateItem> items;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<InspectionTemplateItem> getItems() {
        return items;
    }

    public void setItems(List<InspectionTemplateItem> items) {
        this.items = items;
    }

}
