package edu.wmdd.gover;

import java.util.ArrayList;

public class InspectionSection {

    private String name;
    private int id;
    private ArrayList<InspectionSectionItem> items;

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

    public ArrayList<InspectionSectionItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<InspectionSectionItem> items) {
        this.items = items;
    }

}
