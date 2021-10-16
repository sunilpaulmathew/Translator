/*
 * Copyright (C) 2021-2022 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of The Translator, An application to help translate android apps.
 *
 */

package com.sunilpaulmathew.translator.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sunilpaulmathew.translator.R;
import com.sunilpaulmathew.translator.adapters.TranslatorAdapter;
import com.sunilpaulmathew.translator.utils.StringsItem;
import com.sunilpaulmathew.translator.utils.Translator;
import com.sunilpaulmathew.translator.utils.Utils;

import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 04, 2021
 */
public class FindReplaceActivity extends AppCompatActivity {

    private AppCompatEditText mFromText, mToText;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_replace);

        mFromText = findViewById(R.id.from_text);
        mToText = findViewById(R.id.to_text);
        AppCompatImageButton mBack = findViewById(R.id.back_button);
        FrameLayout mApply = findViewById(R.id.apply);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new TranslatorAdapter(Translator.getData(this)));

        mFromText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Translator.setKeyText(s.toString());
                reloadUI();
            }
        });

        mApply.setOnClickListener(v -> {
            if (mFromText.getText() != null && !mFromText.getText().toString().isEmpty() && mToText.getText() != null &&
                    !mToText.getText().toString().isEmpty()) {
                if (Translator.getData(this).size() == 0 || mFromText.getText().toString().equals(mToText.getText().toString())) {
                    return;
                }
                updateStrings();
            }
        });

        mBack.setOnClickListener(v -> onBackPressed());
    }

    private void updateStrings() {
        new MaterialAlertDialogBuilder(this)
                .setMessage(getString(R.string.find_and_replace_question, mFromText.getText()) + " \"" + mToText.getText() + "\"?")
                .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                })
                .setPositiveButton(getString(R.string.apply), (dialogInterface, i) -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append("<resources xmlns:tools=\"http://schemas.android.com/tools\" tools:ignore=\"MissingTranslation\">").append("\n");
                    sb.append("<!--Created by The Translator <https://play.google.com/store/apps/details?id=com.sunilpaulmathew.translator>-->\n\n");
                    for (StringsItem item : Translator.getRawData(this)) {
                        sb.append(item.getTitle()).append("\">\"").append(item.getDescription().replace(Objects.requireNonNull(
                                mFromText.getText()).toString(), Objects.requireNonNull(mToText.getText()).toString()))
                                .append("\"</string>").append("\n");
                    }
                    sb.append("</resources>");
                    Utils.create(sb.toString(), getFilesDir().toString() + "/strings.xml");
                    reloadUI();
                })
                .show();
    }

    private void reloadUI() {
        mRecyclerView.setAdapter(new TranslatorAdapter(Translator.getData(this)));
    }

    @Override
    public void onBackPressed() {
        if (Translator.getKeyText() != null && !Translator.getKeyText().isEmpty()) {
            Translator.setKeyText(null);
            mFromText.setText(null);
            mToText.setText(null);
            reloadUI();
            return;
        }
        Translator.reloadUI(this);
        super.onBackPressed();
    }

}