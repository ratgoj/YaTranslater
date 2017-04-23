package com.fern.yatranslater.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.fern.yatranslater.R;
import com.fern.yatranslater.db.DbHelper;
import com.fern.yatranslater.entities.TranslateItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Andrey Saprykin on 15.04.2017.
 */

public class ResultTranslateAdapter extends RecyclerView.Adapter<ResultTranslateAdapter.ViewHolder> {
    private static final String TAG = "ResultTranslateAdapter";
    private Context context;
    private List<TranslateItem> results;

    public ResultTranslateAdapter(List<TranslateItem> results) {
        this.results = results;
    }

    @Override
    public ResultTranslateAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.translate_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ResultTranslateAdapter.ViewHolder holder, final int position) {
        holder.translateResult.setText(results.get(position).getTranslate());
        holder.sourceText.setText(results.get(position).getSource());
        holder.addRemoveToFavorite.setChecked(results.get(position).isFavorite());
    }

    @Override
    public int getItemCount() {
        return results.size();
    }


    public void setData(List<TranslateItem> data) {
        this.results = data;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cv_translate)
        CardView cardView;

        @BindView(R.id.translate_tv)
        TextView translateResult;

        @BindView(R.id.source_tv)
        TextView sourceText;

        @BindView(R.id.add_remove_favorite)
        ToggleButton addRemoveToFavorite;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            addRemoveToFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    results.get(getAdapterPosition()).setItem_id(DbHelper.getInstance(context).getLastId());
                    DbHelper.getInstance(context).updateFavorite(results.get(getAdapterPosition()), b);
                    results.get(getAdapterPosition()).setFavorite(b);
                }
            });
        }
    }
}
