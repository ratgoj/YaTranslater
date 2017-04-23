package com.fern.yatranslater.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.fern.yatranslater.R;
import com.fern.yatranslater.db.DbHelper;
import com.fern.yatranslater.entities.TranslateItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Andrey Saprykin on 18.04.2017.
 */

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.ViewHolder> {
    private static final String TAG = "HistoryListAdapter";
    private Context context;
    private List<TranslateItem> historyList;
    private TranslateItem currentItem;

    public HistoryListAdapter(List<TranslateItem> historyList) {
        this.historyList = historyList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hf_item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        currentItem = historyList.get(holder.getAdapterPosition());
        holder.translateText.setText(currentItem.getTranslate());
        holder.sourceText.setText(currentItem.getSource());
        holder.isFavorite.setChecked(currentItem.isFavorite());
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void setNewList(List<TranslateItem> historyList) {
        this.historyList = historyList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cv_fh_item)
        CardView cardView;

        @BindView(R.id.translate_text)
        TextView translateText;

        @BindView(R.id.source_text)
        TextView sourceText;

        @BindView(R.id.is_favorite)
        ToggleButton isFavorite;

        @BindView(R.id.delete_history_item)
        ImageButton deleteFromHistory;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            isFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (DbHelper.getInstance(context).updateFavorite(historyList.get(getAdapterPosition()), b)) {
                        historyList.get(getAdapterPosition()).setFavorite(b);
                    }
                }
            });
        }

        @OnClick(R.id.delete_history_item)
        public void deleteItem() {
            DbHelper.getInstance(context).deleteTranslateItem(historyList.get(getAdapterPosition()));
            historyList.remove(getAdapterPosition());
            notifyItemRemoved(getAdapterPosition());
        }
    }
}
