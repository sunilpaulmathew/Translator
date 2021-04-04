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
import com.sunilpaulmathew.translator.adapters.RecycleViewStringsAdapter;
import com.sunilpaulmathew.translator.utils.Translator;
import com.sunilpaulmathew.translator.utils.Utils;

import java.util.ArrayList;
import java.util.List;
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
        mRecyclerView.setAdapter(new RecycleViewStringsAdapter(getStrings()));

        mFromText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Translator.mFindText = s.toString();
                reloadUI();
            }
        });

        mApply.setOnClickListener(v -> {
            if (mFromText.getText() != null && !mFromText.getText().toString().isEmpty() && mToText.getText() != null &&
                    !mToText.getText().toString().isEmpty()) {
                if (Translator.checkIllegalCharacters(mToText.getText().toString())) {
                    Utils.showSnackbar(findViewById(android.R.id.content), getString(R.string.illegal_string_message));
                    return;
                }
                if (getStrings().size() == 0) {
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
                    for (String mString : getStrings()) {
                        String finalString = ">" + mString + "</string>";
                        String replaceText = ">" + mString.replace(Translator.mFindText, Objects.requireNonNull(mToText.getText())) + "</string>";;
                        Utils.create(Objects.requireNonNull(Utils.read(getFilesDir().toString() + "/strings.xml")).replace(finalString,
                                replaceText), getFilesDir().toString() + "/strings.xml");
                        reloadUI();
                    }
                })
                .show();
    }

    private List<String> getStrings() {
        List<String> mData = new ArrayList<>();
        for (String line : Objects.requireNonNull(Utils.read(getFilesDir().toString() + "/strings.xml")).split("\\r?\\n")) {
            if (line.contains("<string name=") && line.endsWith("</string>") && !line.contains("translatable=\"false")) {
                String[] finalLine = line.split("\">");
                if (Translator.mFindText == null) {
                    mData.add(finalLine[1].replace("</string>", ""));
                } else if (finalLine[1].replace("</string>", "").contains(Translator.mFindText)) {
                    mData.add(finalLine[1].replace("</string>", ""));
                }
            }
        }
        return mData;
    }

    private void reloadUI() {
        mRecyclerView.setAdapter(new RecycleViewStringsAdapter(getStrings()));
    }

    @Override
    public void onBackPressed() {
        if (Translator.mFindText != null && !Translator.mFindText.isEmpty()) {
            Translator.mFindText = null;
            mFromText.setText(null);
            mToText.setText(null);
            reloadUI();
            return;
        }
        Translator.reloadUI(this);
        super.onBackPressed();
    }

}