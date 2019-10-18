package edu.wmdd.gover;

import java.util.Date;

public class Report {

    public Date report_date;
    public String property_name;
    public String notes;
    public int id;

    public Report() {

    }

    public Date getReportDate() {
        return report_date;
    }

    public void setReportDate(Date report_date) {
        this.report_date = report_date;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}