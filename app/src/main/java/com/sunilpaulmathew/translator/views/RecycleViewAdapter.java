/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of The Translator, An application to help translate android apps.
 *
 */

package com.sunilpaulmathew.translator.views;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.sunilpaulmathew.translator.R;
import com.sunilpaulmathew.translator.utils.Utils;
import com.sunilpaulmathew.translator.utils.ViewUtils;

import java.util.List;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on September 28, 2020
 */

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {

    private List<String> data;

    public RecycleViewAdapter (List<String> data){
        this.data = data;
    }

    @NonNull
    @Override
    public RecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_recycle_view, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewAdapter.ViewHolder holder, int position) {
        if (Utils.mKeyText != null && this.data.get(position).toLowerCase().contains(Utils.mKeyText)) {
            holder.textView.setText(Utils.fromHtml(this.data.get(position).toLowerCase().replace(Utils.mKeyText,
                    "<b><i><font color=\"" + Color.RED + "\">" + Utils.mKeyText + "</font></i></b>")));
        } else {
            holder.textView.setText(this.data.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AppCompatTextView textView;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.textView = view.findViewById(R.id.description);
        }

        @Override
        public void onClick(View view) {
            if (!Utils.getSpecialCharacters(this.textView.getText().toString()).isEmpty()) {
                Utils.showSnackbar(view, view.getContext().getString(R.string.edit_string_warning, Utils.getSpecialCharacters(this.textView.getText().toString())));
            }
            ViewUtils.dialogEditText(this.textView.getText().toString(), view.getContext().getString(R.string.update),
                    (dialogInterface1, i1) -> {
                    }, text -> {
                        if (text.isEmpty()) {
                            return;
                        }
                        Utils.create(Objects.requireNonNull(Utils.readFile(itemView.getContext().getFilesDir().toString() + "/strings.xml")).replace(">" + this.textView.getText() + "</string>", ">" + text + "</string>"), itemView.getContext().getFilesDir().toString() + "/strings.xml");
                        data.set(getLayoutPosition(), text);
                        notifyDataSetChanged();
                    }, view.getContext()).setOnDismissListener(dialogInterface -> {
            }).show();
        }
    }

}