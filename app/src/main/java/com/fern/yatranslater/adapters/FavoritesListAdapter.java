package com.fern.yatranslater.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fern.yatranslater.R;
import com.fern.yatranslater.db.DbHelper;
import com.fern.yatranslater.entities.TranslateItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Andrey Saprykin on 22.04.2017.
 */

public class FavoritesListAdapter extends RecyclerView.Adapter<FavoritesListAdapter.ViewHolder> {
    private static final String TAG = "FavoritesListAdapter";
    private Context context;
    private List<TranslateItem> favoriteList;
    private TranslateItem currentItem;

    public FavoritesListAdapter(List<TranslateItem> favoriteList) {
        this.favoriteList = favoriteList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        currentItem = favoriteList.get(holder.getAdapterPosition());
        holder.translateText.setText(currentItem.getTranslate());
        holder.sourceText.setText(currentItem.getSource());
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    public void setNewList(List<TranslateItem> favoriteList) {
        this.favoriteList = favoriteList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.favorite_translate_text)
        TextView translateText;

        @BindView(R.id.favorite_source_text)
        TextView sourceText;

        @BindView(R.id.favorite_delete_item)
        ImageButton deleteItem;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.favorite_delete_item)
        public void deleteFavorite() {
            DbHelper.getInstance(context).updateFavorite(favoriteList.get(getAdapterPosition()), false);
            favoriteList.remove(getAdapterPosition());
            notifyItemRemoved(getAdapterPosition());
        }
    }
}
