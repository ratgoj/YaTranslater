package com.fern.yatranslater.entities;

/**
 * Created by Andrey Saprykin on 23.04.2017.
 */

public class Language {
    private String langCode;
    private String langName;

    public Language(String langCode, String langName) {
        this.langCode = langCode;
        this.langName = langName;
    }

    public Language(String CodeAndName) {
        this.langCode = CodeAndName.substring(0, CodeAndName.lastIndexOf("="));
        this.langName = CodeAndName.substring(CodeAndName.lastIndexOf("=")+1, CodeAndName.length());
    }

    public String getLangCode() {
        return langCode;
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }


    public String getLangName() {
        return langName;
    }

    public void setLangName(String langName) {
        this.langName = langName;
    }
}
