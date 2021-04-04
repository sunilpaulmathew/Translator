/*
 * Copyright (C) 2021-2022 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of The Translator, An application to help translate android apps.
 *
 */

package com.sunilpaulmathew.translator.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.translator.R;
import com.sunilpaulmathew.translator.adapters.RecycleViewStringsAdapter;
import com.sunilpaulmathew.translator.utils.Translator;
import com.sunilpaulmathew.translator.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on September 29, 2020
 */

public class StringViewActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_string_view);

        AppCompatImageButton mBack = findViewById(R.id.back_button);
        AppCompatImageButton mSave = findViewById(R.id.save_button);
        MaterialTextView mCancel = findViewById(R.id.cancel_button);
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);

        if (Translator.mFindText != null) Translator.mFindText = null;

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new RecycleViewStringsAdapter(getStrings()));

        mBack.setOnClickListener(v -> onBackPressed());
        mSave.setOnClickListener(v -> Translator.saveString(this));
        mCancel.setOnClickListener(v -> onBackPressed());
    }

    private List<String> getStrings() {
        List<String> mData = new ArrayList<>();
        for (String line : Objects.requireNonNull(Utils.read(getFilesDir().toString() + "/strings.xml")).split("\\r?\\n")) {
            if (line.contains("<string name=") && line.endsWith("</string>") && !line.contains("translatable=\"false")) {
                mData.add(line);
            }
        }
        return mData;
    }

}