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
import com.fern.yatranslater.adapters.HistoryListAdapter;
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
public class HistoryFragment extends Fragment {
    public static final String TAG = "HistoryFragment";
    private View view;
    private DbHelper dbHelper;

    private List<TranslateItem> historyItems;
    private List<TranslateItem> searchItems;
    private HistoryListAdapter adapter;

    @BindView(R.id.history_search)
    SearchView searchView;

    @BindView(R.id.history_list)
    RecyclerView historyListRv;

    Unbinder unbinder;

    public HistoryFragment() {
        searchItems = new ArrayList<>();
        historyItems = new ArrayList<>();
        adapter = new HistoryListAdapter(historyItems);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history, container, false);
        setHasOptionsMenu(true);
        unbinder = ButterKnife.bind(this, view);

        /*Получаем клиента БД*/
        dbHelper = DbHelper.getInstance(getActivity());

        initRecyclerView();
        searchView.setOnQueryTextListener(new SearchListener());
        return view;
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
                showDeleteHistoryDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateHistoryData();
        ((YaApplication)getActivity().getApplicationContext()).setFragmentChanging(false);
    }

    /**Обновить данные из БД*/
    public void updateHistoryData() {
        if (!historyItems.isEmpty()) {
            historyItems.clear();
        }
        historyItems.addAll(dbHelper.getItems(false));
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void initRecyclerView() {
        historyListRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        historyListRv.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**Очистить историю исключая добавленные в "Избранное" и обновить вид*/
    private void deleteItems() {
        dbHelper.deleteAllHistoryOrFavorites(false);
        historyItems.clear();
        historyItems.addAll(dbHelper.getItems(false));
        adapter.notifyDataSetChanged();
    }

    /**Диалог очистки истории*/
    private void showDeleteHistoryDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.delete_history_title_dialog))
                .setMessage(getResources().getString(R.string.delete_history_question_dialog))
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
     * Поиск по существующей истории
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
                for (TranslateItem item : historyItems) {
                    if (item.getTranslate().startsWith(newText) || item.getSource().startsWith(newText)) {
                        searchItems.add(item);
                    }
                }
                adapter.setNewList(searchItems);
            } else {
                adapter.setNewList(historyItems);
            }
            return false;
        }
    }
}
