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
import com.sunilpaulmathew.translator.adapters.StringViewAdapter;
import com.sunilpaulmathew.translator.utils.StringsItem;
import com.sunilpaulmathew.translator.utils.Translator;

import java.util.ArrayList;
import java.util.List;

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

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new StringViewAdapter(getStrings()));

        mBack.setOnClickListener(v -> onBackPressed());
        mSave.setOnClickListener(v -> Translator.saveString(this));
        mCancel.setOnClickListener(v -> onBackPressed());
    }

    private List<String> getStrings() {
        List<String> mData = new ArrayList<>();
        mData.add("<resources xmlns:tools=\"http://schemas.android.com/tools\" tools:ignore=\"MissingTranslation\">");
        mData.add("<!--Created by The Translator <https://play.google.com/store/apps/details?id=com.sunilpaulmathew.translator>-->\n");
        for (StringsItem item : Translator.getData(this)) {
            mData.add(item.getTitle() + "\">\"" + item.getDescription() + "\"</string>");
        }
        mData.add("</resources>");
        return mData;
    }

}