package com.bdo.shrey.instrumentmanager.Models;

public class History {
    private String i_cat, from, to;

    public History() {
    }

    public History(String i_cat, String from, String to) {
        this.i_cat = i_cat;
        this.from = from;
        this.to = to;
    }

    public String getI_cat() {
        return i_cat;
    }

    public void setI_cat(String i_cat) {
        this.i_cat = i_cat;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
