/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of The Translator, An application to help translate android apps.
 *
 */

package com.sunilpaulmathew.translator.views;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
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

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull RecycleViewAdapter.ViewHolder holder, int position) {
        if (Utils.mKeyText != null && this.data.get(position).toLowerCase().contains(Utils.mKeyText)) {
            holder.description.setText(Utils.fromHtml(this.data.get(position).toLowerCase().replace(Utils.mKeyText,
                    "<b><i><font color=\"" + Color.RED + "\">" + Utils.mKeyText + "</font></i></b>")));
        } else {
            holder.description.setText(this.data.get(position));
        }
        if (Utils.isDarkTheme(holder.description.getContext())) {
            holder.description.setTextColor(ViewUtils.getThemeAccentColor(holder.description.getContext()));
        }
        holder.layoutCard.setOnLongClickListener(item -> {
            new AlertDialog.Builder(holder.layoutCard.getContext())
                    .setMessage(holder.description.getContext().getString(R.string.delete_line_question, holder.description.getText()))
                    .setNegativeButton(R.string.cancel, (dialog1, id1) -> {
                    })
                    .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                        Utils.deleteSingleString(">" + holder.description.getText() + "</string>", holder.layoutCard.getContext());
                        data.remove(position);
                        notifyDataSetChanged();
                    })
                    .show();
            return false;
        });
        holder.imageButton.setImageDrawable(Utils.getSpecialCharacters(this.data.get(position)).isEmpty()
                ? holder.imageButton.getContext().getResources().getDrawable(R.drawable.ic_info)
                : holder.imageButton.getContext().getResources().getDrawable(R.drawable.ic_warning));
        holder.imageButton.setOnClickListener(v -> {
            new AlertDialog.Builder(holder.imageButton.getContext())
                    .setIcon(Utils.getSpecialCharacters(this.data.get(position)).isEmpty()
                            ? R.drawable.ic_info : R.drawable.ic_warning)
                    .setTitle(Utils.getSpecialCharacters(this.data.get(position)).isEmpty()
                            ? R.string.please_note : R.string.warning)
                    .setMessage(Utils.getSpecialCharacters(this.data.get(position)).isEmpty()
                            ? holder.imageButton.getContext().getString(R.string.illegal_string_warning)
                            : holder.imageButton.getContext().getString(R.string.edit_string_warning,
                            Utils.getSpecialCharacters(this.data.get(position))) + "\n\n" +
                            holder.imageButton.getContext().getString(R.string.illegal_string_warning))
                    .setPositiveButton(holder.imageButton.getContext().getString(R.string.cancel), (dialogInterface, i) -> {
                    })
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AppCompatTextView description;
        private AppCompatImageButton imageButton;
        private MaterialCardView layoutCard;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.description = view.findViewById(R.id.description);
            imageButton = view.findViewById(R.id.info_icon);
            layoutCard = view.findViewById(R.id.recycler_view_card);
        }

        @Override
        public void onClick(View view) {
            ViewUtils.dialogEditText(this.description.getText().toString(), view.getContext().getString(R.string.update),
                    (dialogInterface1, i1) -> {
                    }, text -> {
                        if (text.isEmpty()) {
                            return;
                        }
                        Utils.create(Objects.requireNonNull(Utils.readFile(itemView.getContext().getFilesDir().toString() + "/strings.xml")).replace(">" + this.description.getText() + "</string>", ">" + text + "</string>"), itemView.getContext().getFilesDir().toString() + "/strings.xml");
                        data.set(getLayoutPosition(), text);
                        notifyDataSetChanged();
                    }, view.getContext()).setOnDismissListener(dialogInterface -> {
            }).show();
        }
    }

}