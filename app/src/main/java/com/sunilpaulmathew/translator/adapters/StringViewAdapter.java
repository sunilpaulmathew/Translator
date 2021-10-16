/*
 * Copyright (C) 2021-2022 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of The Translator, An application to help translate android apps.
 *
 */

package com.sunilpaulmathew.translator.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.translator.R;

import java.util.List;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 15, 2021
 */
public class StringViewAdapter extends RecyclerView.Adapter<StringViewAdapter.ViewHolder> {

    private static List<String> data;

    public StringViewAdapter(List<String> data){
        StringViewAdapter.data = data;
    }

    @NonNull
    @Override
    public StringViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_strings, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull StringViewAdapter.ViewHolder holder, int position) {
        holder.description.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialTextView description;

        public ViewHolder(View view) {
            super(view);
            this.description = view.findViewById(R.id.description);
        }
    }

}