/*
 * Copyright (C) 2021-2022 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of The Translator, An application to help translate android apps.
 *
 */

package com.sunilpaulmathew.translator.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.translator.BuildConfig;
import com.sunilpaulmathew.translator.R;
import com.sunilpaulmathew.translator.utils.Utils;

import in.sunilpaulmathew.sCommon.Utils.sJSONUtils;
import in.sunilpaulmathew.sCommon.Utils.sUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on September 24, 2020
 */

public class AboutActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        AppCompatImageView mDeveloper = findViewById(R.id.developer);
        MaterialTextView mAppName = findViewById(R.id.app_title);
        MaterialTextView mForegroundText = findViewById(R.id.foreground_text);
        MaterialTextView mCancel = findViewById(R.id.cancel_button);
        mDeveloper.setOnClickListener(v -> sUtils.launchUrl("https://github.com/sunilpaulmathew", this));
        mCancel.setOnClickListener(v -> onBackPressed());

        mAppName.setText(getString(R.string.app_name) + (Utils.isDonated(this) ? " Pro " :  " ") + BuildConfig.VERSION_NAME);
        mForegroundText.setText(sJSONUtils.getString(sJSONUtils.getJSONObject(sUtils.readAssetFile(
                "changelogs.json", this)), "releaseNotes"));
        mForegroundText.setVisibility(View.VISIBLE);
        mCancel.setVisibility(View.VISIBLE);
    }

}