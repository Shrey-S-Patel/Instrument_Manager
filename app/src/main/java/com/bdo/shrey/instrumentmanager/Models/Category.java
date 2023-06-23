package com.bdo.shrey.instrumentmanager.Models;

public class Category {
    private String cat_name, cat_code;
    private int cat_count, cat_code_count;

    public Category() {
    }

    public Category(String cat_name, String cat_code, int cat_count, int cat_code_count) {
        this.cat_name = cat_name;
        this.cat_code = cat_code;
        this.cat_count = cat_count;
        this.cat_code_count = cat_code_count;
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
}
