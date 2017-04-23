package com.fern.yatranslater.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fern.yatranslater.MainActivity;
import com.fern.yatranslater.R;
import com.fern.yatranslater.YaApplication;
import com.fern.yatranslater.adapters.ResultTranslateAdapter;
import com.fern.yatranslater.db.DbHelper;
import com.fern.yatranslater.entities.Language;
import com.fern.yatranslater.entities.TranslateItem;
import com.fern.yatranslater.net.clients.NetClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class TranslateFragment extends Fragment {
    private final static String TAG = "TranslateFragment";
    private final int USER_NO_INPUT_DELAY = 1000; //время бездействия пользователя

    private View view;
    private View actionBarView;

    private TextView fromLanguage_tv;
    private TextView intoLanguage_tv;

    private ImageButton toggleLanguage_button;

    Unbinder unbinder;

    @BindView(R.id.text_translate)
    TextInputEditText inputTranslateText;

    @BindView(R.id.translate_result)
    RecyclerView translateResultRv;

    private NetClient netClient;
    private DbHelper dbHelper;

    private TextWatcher textWatcher;

    private Timer historyTimer;

    private List<TranslateItem> textTranslate;
    private ResultTranslateAdapter adapter;

    private YaApplication yaApplicationContext;

    private String translateDirection;
    private Language nativeLang;
    private Language translateLang;


    public TranslateFragment() {
        textTranslate = new ArrayList<>();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_translate, container, false);
        yaApplicationContext = (YaApplication) getActivity().getApplicationContext();

        /*Получаем используемые нами языки*/
        nativeLang = yaApplicationContext.getNativeLang();
        translateLang = yaApplicationContext.getTranslateLang();
        translateDirection = translateLang.getLangCode() + "-" + nativeLang.getLangCode();

        /*Устанавливаем для ActionBar своой view*/
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setCustomView(R.layout.language_swipe);
        actionBarView = ((AppCompatActivity) getActivity()).getSupportActionBar().getCustomView();
        initActionBarViews();

        /*Получам сетевого клиента и БД клиента*/
        netClient = ((MainActivity) getActivity()).getNetClient();
        dbHelper = DbHelper.getInstance(getActivity());

        unbinder = ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        initInputListeners();
        initRecyclerView();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        yaApplicationContext.setFragmentChanging(false);
        /*Получаем используемые нами языки, они могли измениться*/
        nativeLang = yaApplicationContext.getNativeLang();
        translateLang = yaApplicationContext.getTranslateLang();
        fromLanguage_tv.setText(translateLang.getLangName());
        intoLanguage_tv.setText(nativeLang.getLangName());

        /*Получам последнею запись "перевода" из БД*/
        TranslateItem cacheItem = dbHelper.getLastItem();
        if (cacheItem != null) {
            textTranslate.add(cacheItem);
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        /*Если пользователь сменил фрагмент, то очищам поле ввода, если "паузу" вызовет, что то другое текст останеться*/
        if (yaApplicationContext.isFragmentChanging()) {
            inputTranslateText.setText("");
        }
    }

    /**
     * Инициализация views в ActionBar
     */
    private void initActionBarViews() {
        if (actionBarView != null) {
            fromLanguage_tv = (TextView) actionBarView.findViewById(R.id.from_language);
            intoLanguage_tv = (TextView) actionBarView.findViewById(R.id.into_language);
            toggleLanguage_button = (ImageButton) actionBarView.findViewById(R.id.toggle_language);
            toggleLanguage_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onLanguageToggle();
                }
            });
        }
    }

    /**
     * Отслеживания изменения текста вводимого пользователем
     */
    private void initInputListeners() {
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                /*Если пользователь печатает в течении одной секунды, то отменяем таймер сохранения в историю*/
                if (historyTimer != null) {
                    historyTimer.cancel();
                    historyTimer.purge();
                    historyTimer = null;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null && !editable.toString().equals("")) {
                    onTranslate(translateDirection, editable.toString());
                    saveToHistory(); // если пользователь не вводил текст 1 сек
                } else {
                    textTranslate.clear();
                    adapter.notifyDataSetChanged();
                }
            }
        };

        inputTranslateText.addTextChangedListener(textWatcher);
    }

    /**
     * Инициализация RecyclerView для возможных вариантов перевода текста
     */
    private void initRecyclerView() {
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        translateResultRv.setLayoutManager(llm);
        adapter = new ResultTranslateAdapter(textTranslate);
        yaApplicationContext.registerAdapter(adapter, textTranslate);
        translateResultRv.setAdapter(adapter);
    }

    /**
     * Смена направления перевода
     */
    private void onLanguageToggle() {
        String from = fromLanguage_tv.getText().toString();
        String into = intoLanguage_tv.getText().toString();
        fromLanguage_tv.setText(into);
        intoLanguage_tv.setText(from);

        Language temp = nativeLang;
        nativeLang = translateLang;
        translateLang = temp;
        changeTranslateDirection();
        temp = null;
    }

    private void onTranslate(String direction, String text) {
        netClient.translateText(direction, text);
    }

    /**
     * Сохраняет историю если пользователь не вводил текст 1 секунду
     */
    private void saveToHistory() {
        historyTimer = new Timer();
        historyTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                insertHistoryData();
            }
        }, USER_NO_INPUT_DELAY);
    }

    /**
     * Сохранение данных перевода в таблицу БД
     */
    private void insertHistoryData() {
        if (!textTranslate.isEmpty()) {
            for (TranslateItem translateItem : textTranslate) {
                if (!dbHelper.insertTranslate(translateItem)) {
                    Log.d(TAG, "Send: Can't insert to history table");
                }
            }
        }
    }

    /**
     * Смена направления перевода
     */
    private void changeTranslateDirection() {
        translateDirection = translateLang.getLangCode() + "-" + nativeLang.getLangCode();
        onTranslate(translateDirection, inputTranslateText.getText().toString());
        insertHistoryData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
