package com.ellalan.certifiedparent.model;

import java.io.Serializable;


public class CategoryStatements implements Serializable{
    private String QUOTE = "";
    private String IMAGE ="";

    public String getQUOTE() {
        return QUOTE;
    }

    public void setQUOTE(String QUOTE) {
        this.QUOTE = QUOTE;
    }

    public String getIMAGE() {
        return IMAGE;
    }

    public void setIMAGE(String IMAGE) {
        this.IMAGE = IMAGE;
    }




}
