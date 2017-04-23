package com.fern.yatranslater.entities;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Andrey Saprykin on 15.04.2017.
 */

public class TranslateLangs {
    private ArrayList<String> dirs;
    @SerializedName("langs")
    private LinkedHashMap<String, String> langs;

    public TranslateLangs() {
        this.dirs = new ArrayList<>();
        this.langs = new LinkedHashMap<>();
    }

    public TranslateLangs(ArrayList<String> dirs, LinkedHashMap<String, String> langs) {
        this.dirs = dirs;
        this.langs = langs;
    }

    @Nullable
    public ArrayList<String> getDirs() {
        return dirs;
    }

    public void setDirs(ArrayList<String> dirs) {
        this.dirs = dirs;
    }

    @Nullable
    public LinkedHashMap<String, String> getLangs() {
        return langs;
    }

    public void setLangs(LinkedHashMap<String, String> langs) {
        this.langs = langs;
    }
}
