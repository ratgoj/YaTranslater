package com.fern.yatranslater.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.fern.yatranslater.MainActivity;
import com.fern.yatranslater.R;
import com.fern.yatranslater.YaApplication;
import com.fern.yatranslater.adapters.LinkedHashMapAdapter;
import com.fern.yatranslater.entities.Language;
import com.fern.yatranslater.entities.TranslateLangs;
import com.fern.yatranslater.net.clients.NetClient;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {
    private final static String TAG = "SettingsFragment";

    private View view;
    private View actionBarView;
    private NetClient netClient;
    private Call<TranslateLangs> langsCall;
    Unbinder unbinder;

    @BindView(R.id.demand_yandex)
    TextView demandYandex;

    @BindView(R.id.native_language)
    Spinner nativeSpinner;

    @BindView(R.id.language_of_translation)
    Spinner translateSpinner;

    private String nativeLanguageCode;
    private TranslateLangs langs;
    private YaApplication yaApplicationContext;

    LinkedHashMapAdapter<String, String> adapter;

    private Language language;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        yaApplicationContext = (YaApplication) getActivity().getApplicationContext();

        /*Устанавливаем для ActionBar своой view*/
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setCustomView(R.layout.settins_ab_title);
        actionBarView = ((AppCompatActivity) getActivity()).getSupportActionBar().getCustomView();
        unbinder = ButterKnife.bind(this, view);

        /*Получам код языка установленого пользователем по умолчанию*/
        nativeLanguageCode = Locale.getDefault().getLanguage();

        setHasOptionsMenu(true);

         /*Получам сетевого клиента*/
        netClient = ((MainActivity) getActivity()).getNetClient();
        getLangsFromNet();
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
    }

    /**
     * Запрос к API сервера для получения списка языков перевода
     */
    private void getLangsFromNet() {
        langsCall = netClient.getYaNetService().getLangs(nativeLanguageCode, getString(R.string.api_key));
        langsCall.enqueue(new Callback<TranslateLangs>() {
            @Override
            public void onResponse(Call<TranslateLangs> call, Response<TranslateLangs> response) {
                if (response.code() != netClient.SERVER_CODE_200) {
                    netClient.showCodeErrorDialog(response.code(), "Code =" + response.code() + " request = " + call.request().toString() + " headers = " + call.request().headers().toString());
                } else {
                    initSpinners(response.body());
                }
            }

            @Override
            public void onFailure(Call<TranslateLangs> call, Throwable t) {
                netClient.showFailureDialog(t.getMessage());
            }
        });
    }

    /**
     * Инициализация выпадающих списков для языков
     */
    private void initSpinners(TranslateLangs translateLangs) {
        langs = translateLangs;

        adapter = new LinkedHashMapAdapter<String, String>(yaApplicationContext, R.layout.spinner_item, langs.getLangs());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if (nativeSpinner != null && translateSpinner != null) {
            nativeSpinner.setAdapter(adapter);
            translateSpinner.setAdapter(adapter);

            //Получаем позиции для языков по умолчанию
            int nativePosition = new ArrayList<String>(langs.getLangs().keySet()).indexOf(nativeLanguageCode);
            int defaultTranslatePosition = new ArrayList<String>(langs.getLangs().keySet()).indexOf("en");

            nativeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    // Сам не в восторге от этого решения :(
                    yaApplicationContext.setNativeLang(new Language(nativeSpinner.getSelectedItem().toString()));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            translateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    // Сам не в восторге от этого решения :(
                    yaApplicationContext.setTranslateLang(new Language(translateSpinner.getSelectedItem().toString()));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            nativeSpinner.setSelection(nativePosition);
            translateSpinner.setSelection(defaultTranslatePosition);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
