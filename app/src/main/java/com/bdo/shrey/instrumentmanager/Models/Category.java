package com.bdo.shrey.instrumentmanager.Models;

public class Category {
    private String cat_name, cat_code;
    private int cat_count, cat_code_count, Active, Inactive, Transit;

    public Category() {
    }

    public Category(String cat_name, String cat_code, int cat_count, int cat_code_count, int Active, int Inactive, int Transit) {
        this.cat_name = cat_name;
        this.cat_code = cat_code;
        this.cat_count = cat_count;
        this.cat_code_count = cat_code_count;
        this.Active = Active;
        this.Inactive = Inactive;
        this.Transit = Transit;
    }

    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }

    public String getCat_code() {
        return cat_code;
    }

    public void setCat_code(String cat_code) {
        this.cat_code = cat_code;
    }

    public int getCat_count() {
        return cat_count;
    }

    public void setCat_count(int cat_count) {
        this.cat_count = cat_count;
    }

    public int getCat_code_count() {
        return cat_code_count;
    }

    public void setCat_code_count(int cat_code_count) {
        this.cat_code_count = cat_code_count;
    }

    public int getActive() {
        return Active;
    }

    public void setActive(int Active) {
        this.Active = Active;
    }

    public int getInactive() {
        return Inactive;
    }

    public void setInactive(int Inactive) {
        this.Inactive = Inactive;
    }

    public int getTransit() {
        return Transit;
    }

    public void setTransit(int Transit) {
        this.Transit = Transit;
    }
}
