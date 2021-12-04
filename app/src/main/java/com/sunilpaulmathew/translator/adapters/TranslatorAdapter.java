/*
 * Copyright (C) 2021-2022 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of The Translator, An application to help translate android apps.
 *
 */

package com.sunilpaulmathew.translator.adapters;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.translator.R;
import com.sunilpaulmathew.translator.utils.StringsItem;
import com.sunilpaulmathew.translator.utils.Translator;
import com.sunilpaulmathew.translator.utils.Utils;

import java.util.List;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on September 28, 2020
 */
public class TranslatorAdapter extends RecyclerView.Adapter<TranslatorAdapter.ViewHolder> {

    private static List<StringsItem> data;

    public TranslatorAdapter(List<StringsItem> data){
        TranslatorAdapter.data = data;
    }

    @NonNull
    @Override
    public TranslatorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_main, parent, false);
        return new ViewHolder(rowItem);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull TranslatorAdapter.ViewHolder holder, int position) {
        if (Translator.getKeyText() != null && Translator.isTextMatched(data.get(position).getDescription())) {
            holder.description.setText(Utils.fromHtml(data.get(position).getDescription().replace(Translator.getKeyText(),
                    "<b><i><font color=\"" + Color.RED + "\">" + Translator.getKeyText() + "</font></i></b>")));
        } else {
            holder.description.setText(data.get(position).getDescription());
        }
        holder.description.setTextColor(Utils.isDarkTheme(holder.description.getContext()) ?
                Utils.getThemeAccentColor(holder.description.getContext()) : Color.BLACK);
        holder.layoutCard.setOnLongClickListener(item -> {
            new MaterialAlertDialogBuilder(holder.layoutCard.getContext())
                    .setMessage(holder.description.getContext().getString(R.string.delete_line_question, holder.description.getText()))
                    .setNegativeButton(R.string.cancel, (dialog1, id1) -> {
                    })
                    .setPositiveButton(R.string.yes, (dialogInterface, i) -> {

                        Translator.deleteSingleString(data.get(position), holder.layoutCard.getContext());
                        data.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, data.size());
                    })
                    .show();
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final MaterialCardView layoutCard;
        private final MaterialTextView description;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.description = view.findViewById(R.id.description);
            layoutCard = view.findViewById(R.id.recycler_view_card);
        }

        @Override
        public void onClick(View view) {
            LayoutInflater mLayoutInflater = LayoutInflater.from(view.getContext());
            View editLayout = mLayoutInflater.inflate(R.layout.layout_string_edit, null);
            AppCompatEditText mText = editLayout.findViewById(R.id.text);
            MaterialCardView mReload = editLayout.findViewById(R.id.reload);

            mReload.setOnClickListener(v -> {
                if (mText.getText() != null && !mText.getText().toString().trim().equals(data.get(getAdapterPosition()).getDescription())) {
                    mText.setText(data.get(getAdapterPosition()).getDescription());
                }
            });

            mText.setText(data.get(getAdapterPosition()).getDescription());
            mText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().contains("\n")) {
                        mText.setText(s.toString().replace("\n","\\n"));
                        Utils.showSnackbar(mText, view.getContext().getString(R.string.line_break_message));
                    }
                    if (s.toString().contains("<") || s.toString().contains(">")) {
                        Utils.showSnackbar(mText, view.getContext().getString(R.string.tag_complete_message));
                    }
                }
            });
            mText.requestFocus();

            new MaterialAlertDialogBuilder(view.getContext())
                    .setView(editLayout)
                    .setNegativeButton(R.string.cancel, (dialog1, id1) -> {
                    })
                    .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                        if (mText.getText() == null || mText.getText().toString().trim().isEmpty() ||
                                mText.getText().toString().trim().equals(data.get(getAdapterPosition()).getDescription())) {
                            return;
                        }
                        String oldString = data.get(getAdapterPosition()).getTitle() + "\">\"" + data.get(getAdapterPosition()).getDescription() + "\"</string>";
                        String newString = data.get(getAdapterPosition()).getTitle() + "\">\"" + mText.getText() + "\"</string>";
                        Utils.create(Translator.getStrings(view.getContext()).replace(oldString, newString), view.getContext().getFilesDir().toString() + "/strings.xml");
                        data.get(getAdapterPosition()).setDescription(mText.getText().toString());
                        notifyItemChanged(getAdapterPosition());
                    }).show();
        }
    }

}