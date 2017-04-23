package com.fern.yatranslater.entities;

/**
 * Created by Andrey Saprykin on 15.04.2017.
 */

public class TranslateItem {
    private int item_id;
    private String source;
    private String translate;
    private boolean isFavorite;

    public TranslateItem(String source, String translate) {
        this.source = source;
        this.translate = translate;
        this.isFavorite = false;
        this.item_id = -1;
    }

    public TranslateItem(int item_id, String source, String translate, boolean isFavorite) {
        this.item_id = item_id;
        this.source = source;
        this.translate = translate;
        this.isFavorite = isFavorite;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTranslate() {
        return translate;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    @Override
    public String toString() {
        return "id = " + item_id + " source = " +source + " translate = "+ translate + " is favorite = " +isFavorite;
    }
}
