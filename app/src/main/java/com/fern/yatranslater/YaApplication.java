package com.fern.yatranslater;

import android.app.Application;

import com.fern.yatranslater.adapters.ResultTranslateAdapter;
import com.fern.yatranslater.entities.Language;
import com.fern.yatranslater.entities.TranslateItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrey Saprykin on 16.04.2017.
 */

public class YaApplication extends Application {
    private List<TranslateItem> translateResult;
    private ResultTranslateAdapter translateAdapter;

    /*Создаем переменные для языков, что бы знать если изменил ли пользователь языки в настройках*/
    private Language nativeLang;
    private Language translateLang;

    /*Позволяет узнать переодит ли пользователь на другой фрагмент*/
    private boolean isFragmentChanging;

    @Override
    public void onCreate() {
        super.onCreate();
        setDefaultLanguages();
        isFragmentChanging = false;
    }

    /**
     * Регистрируем Адаптер для RecyclerView(результаты перведа)
     */
    public void registerAdapter(ResultTranslateAdapter adapter, List<TranslateItem> data) {
        this.translateAdapter = adapter;
        this.translateResult = data;
    }

    /**
     * Запрс резульатата перевода асинхронный, этим методом мы обновляем данные адаптера, когда получаем результьтат из запроса
     */
    public void updateResult(String text, ArrayList<String> resultData) {
        translateResult.clear();
        for (String res : resultData) {
            translateResult.add(new TranslateItem(text, res));
        }
        translateAdapter.notifyDataSetChanged();
    }

    /**
     * Устанавливаем языки для перевода по умолчанию
     * Желательно здесь бы было узнать родной язык пользователя
     * Хорошие мысли не всегда приходят во воремя.
     */
    private void setDefaultLanguages() {
        setNativeLang(new Language("ru", getString(R.string.russian)));
        setTranslateLang(new Language("en", getString(R.string.english)));
    }

    public Language getNativeLang() {
        return nativeLang;
    }

    public void setNativeLang(Language nativeLang) {
        this.nativeLang = nativeLang;
    }

    public Language getTranslateLang() {
        return translateLang;
    }

    public void setTranslateLang(Language translateLang) {
        this.translateLang = translateLang;
    }

    public boolean isFragmentChanging() {
        return isFragmentChanging;
    }

    public void setFragmentChanging(boolean fragmentChanging) {
        isFragmentChanging = fragmentChanging;
    }
}
