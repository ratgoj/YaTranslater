package com.fern.yatranslater.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.fern.yatranslater.R;
import com.fern.yatranslater.YaApplication;
import com.fern.yatranslater.adapters.FavoritesListAdapter;
import com.fern.yatranslater.db.DbHelper;
import com.fern.yatranslater.entities.TranslateItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesFragment extends Fragment {
    public static final String TAG = "FavoritesFragment";
    private View view;
    private DbHelper dbHelper;
    private List<TranslateItem> favoritesItems;
    private List<TranslateItem> searchItems;

    private FavoritesListAdapter adapter;

    @BindView(R.id.favorites_search)
    SearchView searchFavorites;

    @BindView(R.id.favorites_list)
    RecyclerView favoritesListRv;

    Unbinder unbinder;

    public FavoritesFragment() {
        // Required empty public constructor
        favoritesItems = new ArrayList<>();
        searchItems = new ArrayList<>();

        adapter = new FavoritesListAdapter(favoritesItems);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favorites, container, false);
        setHasOptionsMenu(true);
        unbinder = ButterKnife.bind(this, view);
         /*Получаем клиента БД*/
        dbHelper = DbHelper.getInstance(getActivity());

        favoritesListRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        favoritesListRv.setAdapter(adapter);
        searchFavorites.setOnQueryTextListener(new SearchListener());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateFavoritesData();
        ((YaApplication) getActivity().getApplicationContext()).setFragmentChanging(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.hf_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.items_delete:
                showDeleteFavoriteDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * Обновить данные из БД
     */
    public void updateFavoritesData() {
        if (!favoritesItems.isEmpty()) {
            favoritesItems.clear();
        }
        favoritesItems.addAll(dbHelper.getItems(true));
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Диалог для очистки избранного
     */
    private void showDeleteFavoriteDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.favorite_dialog_delete_title))
                .setMessage(getResources().getString(R.string.favorite_dialog_delete_question))
                .setNegativeButton(getResources().getString(R.string.negative_button_name), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton(getResources().getString(R.string.positive_button_name), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        deleteItems();
                    }
                }).create()
                .show();
    }

    /**
     * Удаляем записи "Избранного" из БД и обновляем вид
     */
    private void deleteItems() {
        dbHelper.deleteAllHistoryOrFavorites(true);
        favoritesItems.clear();
        adapter.notifyDataSetChanged();
    }

    /**
     * Поиск по "Избраному"
     */
    private class SearchListener implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if (!searchItems.isEmpty()) {
                searchItems.clear();
            }
            if (!newText.isEmpty() && !newText.equals("")) {
                for (TranslateItem item : favoritesItems) {
                    if (item.getTranslate().startsWith(newText) || item.getSource().startsWith(newText)) {
                        searchItems.add(item);
                    }
                }
                adapter.setNewList(searchItems);
            } else {
                adapter.setNewList(favoritesItems);
            }
            return false;
        }
    }
}
