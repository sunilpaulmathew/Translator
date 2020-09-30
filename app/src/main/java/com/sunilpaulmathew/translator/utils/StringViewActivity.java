/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of The Translator, An application to help translate android apps.
 *
 */

package com.sunilpaulmathew.translator.utils;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.sunilpaulmathew.translator.R;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on September 29, 2020
 */

public class StringViewActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_string_view);

        AppCompatTextView mForegroundText = findViewById(R.id.foreground_text);
        AppCompatTextView mCancel = findViewById(R.id.cancel_button);
        AppCompatImageButton mBack = findViewById(R.id.back_button);
        AppCompatImageButton mSave = findViewById(R.id.save_button);
        mBack.setOnClickListener(v -> onBackPressed());
        mSave.setOnClickListener(v -> Utils.getInstance().saveString(mSave, this));
        mCancel.setOnClickListener(v -> onBackPressed());
        mForegroundText.setText(Utils.getStrings(this));
    }

}