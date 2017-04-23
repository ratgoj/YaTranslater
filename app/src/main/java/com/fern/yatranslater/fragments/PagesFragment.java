package com.fern.yatranslater.fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fern.yatranslater.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class PagesFragment extends Fragment {
    public static final String TAG = "PagesFragment";
    private final int FAVORITES_INDEX = 1;
    /**
     * Количество страниц-экранов
     */
    private final int NUM_PAGES = 2;

    private View view;
    private View actionBarView;

    private MyPageAdapter myPageAdapter;

    ViewPager viewPager;

    @BindView(R.id.history_favorite_tab_layout)
    TabLayout historyFavoriteTabLayout;

    private Unbinder unbinder;

    private HistoryFragment historyFragment;
    private FavoritesFragment favoritesFragment;

    public PagesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pages, container, false);
        /*Устанавливаем для ActionBar своой view*/
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setCustomView(R.layout.favorite_history_swipe);

        actionBarView = ((AppCompatActivity) getActivity()).getSupportActionBar().getCustomView();
        unbinder = ButterKnife.bind(this, actionBarView);

        initFragments();
        initListeners();
        return view;
    }

    /**
     * Инициализируем viewPager, адаптер для viewPager,фрагмент Истории и фрагмент Избраного
     */
    private void initFragments() {
        historyFragment = new HistoryFragment();
        favoritesFragment = new FavoritesFragment();
        myPageAdapter = new MyPageAdapter(getChildFragmentManager());
        viewPager = (ViewPager) view.findViewById(R.id.history_favorites_view_pager);
        viewPager.setAdapter(myPageAdapter);
    }

    /**
     * Инициаллизация слушателей historyFavoriteTabLayout и viewPager, для отображения действий пользователя
     */
    private void initListeners() {
        historyFavoriteTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                historyFavoriteTabLayout.getTabAt(position).select();
                getCurrentFragment(position).onResume();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * Получить текущий фрагмент по позиции
     */
    private Fragment getCurrentFragment(int position) {
        switch (position) {
            case FAVORITES_INDEX:
                return favoritesFragment;
            default:
                return historyFragment;
        }
    }

    private class MyPageAdapter extends FragmentPagerAdapter {

        public MyPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return getCurrentFragment(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
