/*
 * Copyright (C) 2021-2022 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of The Translator, An application to help translate android apps.
 *
 */

package com.sunilpaulmathew.translator.adapters;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.translator.R;
import com.sunilpaulmathew.translator.utils.Utils;

import java.util.ArrayList;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 24, 2021
 */

public class RecycleViewSettingsAdapter extends RecyclerView.Adapter<RecycleViewSettingsAdapter.ViewHolder> {

    private ArrayList<RecycleViewItem> data;

    private static ClickListener mClickListener;

    public RecycleViewSettingsAdapter(ArrayList<RecycleViewItem> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public RecycleViewSettingsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_settings, parent, false);
        return new RecycleViewSettingsAdapter.ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewSettingsAdapter.ViewHolder holder, int position) {
        if (this.data.get(position).getTitle() != null) {
            holder.mTitle.setText(this.data.get(position).getTitle());
            holder.mTitle.setVisibility(View.VISIBLE);
            holder.mTitle.setTextColor(Utils.isDarkTheme(holder.mTitle.getContext()) ? Color.WHITE : Color.BLACK);
        }
        if (this.data.get(position).getDescription() != null) {
            holder.mDescription.setText(this.data.get(position).getDescription());
            holder.mDescription.setVisibility(View.VISIBLE);
        }
        if (this.data.get(position).getIcon() != null) {
            holder.mIcon.setImageDrawable(this.data.get(position).getIcon());
            holder.mIcon.setColorFilter(Utils.isDarkTheme(holder.mIcon.getContext()) ? Color.WHITE : Color.BLACK);
            holder.mIcon.setVisibility(View.VISIBLE);
        }
        if ((position == 2 || position == 3) && !Utils.exist(holder.mTitle.getContext().getFilesDir().toString() + "/strings.xml")
                || position == 10 && Utils.isPlayStoreAvailable(holder.mTitle.getContext()) && Utils.isDonated(holder.mTitle.getContext())) {
            holder.mTitle.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.mDescription.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AppCompatImageView mIcon;
        private MaterialTextView mTitle;
        private MaterialTextView mDescription;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.mIcon = view.findViewById(R.id.icon);
            this.mTitle = view.findViewById(R.id.title);
            this.mDescription = view.findViewById(R.id.description);
        }

        @Override
        public void onClick(View view) {
            mClickListener.onItemClick(getAdapterPosition(), view);
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        RecycleViewSettingsAdapter.mClickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

}