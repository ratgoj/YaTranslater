package com.fern.yatranslater.entities;

import java.util.ArrayList;

/**
 * Created by Andrey Saprykin on 15.04.2017.
 */

public class Translate {
    private int code;
    private String lang;
    private ArrayList<String> text;

    public Translate() {
        text = new ArrayList<>();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public ArrayList<String> getText() {
        return text;
    }

    public void setText(ArrayList<String> text) {
        this.text = text;
    }

}
