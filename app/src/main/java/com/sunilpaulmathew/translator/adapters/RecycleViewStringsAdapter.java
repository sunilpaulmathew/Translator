/*
 * Copyright (C) 2021-2022 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of The Translator, An application to help translate android apps.
 *
 */

package com.sunilpaulmathew.translator.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.translator.R;
import com.sunilpaulmathew.translator.utils.Translator;
import com.sunilpaulmathew.translator.utils.Utils;

import java.util.List;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 04, 2021
 */

public class RecycleViewStringsAdapter extends RecyclerView.Adapter<RecycleViewStringsAdapter.ViewHolder> {

    private static List<String> data;

    public RecycleViewStringsAdapter(List<String> data){
        RecycleViewStringsAdapter.data = data;
    }

    @NonNull
    @Override
    public RecycleViewStringsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_strings, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewStringsAdapter.ViewHolder holder, int position) {
        if (Translator.mFindText != null && data.get(position).contains(Translator.mFindText)) {
            holder.description.setText(Utils.fromHtml(data.get(position).replace(Translator.mFindText,
                    "<b><i><font color=\"" + Color.RED + "\">" + Translator.mFindText + "</font></i></b>")));
        } else {
            holder.description.setText(data.get(position));
        }
        //holder.description.setText(data.get(position));
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