package com.fern.yatranslater;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import com.fern.yatranslater.fragments.PagesFragment;
import com.fern.yatranslater.fragments.SettingsFragment;
import com.fern.yatranslater.fragments.TranslateFragment;
import com.fern.yatranslater.net.clients.NetClient;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {
    private static final int HISTORY_FRAGMENT_POSITION = 1;
    private static final int SETTINGS_FRAGMENT_POSITION = 2;

    private static final String FRAGMENT_POSITION = "currentFragmentPosition";

    //Используя библиотеку ButterKnife инициализируем view screenTabLayout
    @BindView(R.id.screen_navigation_tab_layout)
    TabLayout screenTabLayout;

    private TranslateFragment translateFragment;
    private PagesFragment historyFragment;
    private SettingsFragment settingsFragment;

    private NetClient netClient;
    private int savePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ButterKnife.bind(this); //инициализация всех view элементов (привязка)
        initFragments();
        initListeners();
        netClient = new NetClient(this, (YaApplication) getApplicationContext());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /*Сохраняем позицию текущего фрагмента*/
        outState.putInt(FRAGMENT_POSITION, savePosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        /*Восстанавливаем текущий фрагмент и устанавливаем для него статус "выбраного"*/
        int position = savedInstanceState.getInt(FRAGMENT_POSITION);
        setScreen(position);
        TabLayout.Tab current = screenTabLayout.getTabAt(position);
        current.select();
    }

    /**
     * Инициализация всех фрагментов и устанавка фрагмента по умолчанию (фрагмент перевод)
     */
    private void initFragments() {
        translateFragment = new TranslateFragment();
        historyFragment = new PagesFragment();
        settingsFragment = new SettingsFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.screen_container, translateFragment).commit();
    }

    /**
     * Инициализация слушателя выбора вкладки экрана
     */
    private void initListeners() {
        screenTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                savePosition = tab.getPosition();
                setScreen(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * Установка нужного фрагмента (экрана) по позиции
     */
    private void setScreen(int position) {
        ((YaApplication) getApplicationContext()).setFragmentChanging(true);
        switch (position) {
            case HISTORY_FRAGMENT_POSITION:
                getSupportFragmentManager().beginTransaction().replace(R.id.screen_container, historyFragment).commit();
                break;
            case SETTINGS_FRAGMENT_POSITION:
                getSupportFragmentManager().beginTransaction().replace(R.id.screen_container, settingsFragment).commit();
                break;
            default:
                getSupportFragmentManager().beginTransaction().replace(R.id.screen_container, translateFragment).commit();
                break;
        }
    }

    public NetClient getNetClient() {
        return netClient;
    }
}
