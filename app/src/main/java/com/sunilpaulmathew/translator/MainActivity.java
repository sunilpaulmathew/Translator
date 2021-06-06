/*
 * Copyright (C) 2021-2022 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of The Translator, An application to help translate android apps.
 *
 */

package com.sunilpaulmathew.translator;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;

import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.translator.activities.SettingsActivity;
import com.sunilpaulmathew.translator.fragments.TranslatorFragment;
import com.sunilpaulmathew.translator.utils.Translator;
import com.sunilpaulmathew.translator.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on June 30, 2020
 */

public class MainActivity extends AppCompatActivity {

    public static AppCompatEditText mSearchWord;
    private boolean mExit;
    private final Handler mHandler = new Handler();
    private MaterialTextView mAboutApp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Initialize App Theme
        Utils.initializeAppTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAboutApp = findViewById(R.id.about_app);
        mSearchWord = findViewById(R.id.search_Text);
        AppCompatImageButton mSettings = findViewById(R.id.settings_menu);
        AppCompatImageButton mSearch = findViewById(R.id.search_button);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new TranslatorFragment()).commit();

        mSettings.setOnClickListener(v -> {
            Intent settingsMenu = new Intent(this, SettingsActivity.class);
            startActivity(settingsMenu);
        });

        if (!Utils.exist(getFilesDir().toString() + "/strings.xml")) {
            mSearch.setVisibility(View.GONE);
        }

        mSearch.setOnClickListener(v -> {
            mSearchWord.setVisibility(View.VISIBLE);
            mAboutApp.setVisibility(View.GONE);
        });
        mSearchWord.setTextColor(Color.RED);
        mSearchWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Translator.mData.clear();
                Translator.mKeyText = s.toString().toLowerCase();
                Translator.reloadUI(MainActivity.this);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (Translator.mKeyText != null) {
            mSearchWord.setText(null);
            Translator.mKeyText = null;
            return;
        }
        if (mAboutApp.getVisibility() == View.GONE) {
            mSearchWord.setVisibility(View.GONE);
            mAboutApp.setVisibility(View.VISIBLE);
            return;
        }
        if (mExit) {
            mExit = false;
            super.onBackPressed();
        } else {
            Utils.showSnackbar(findViewById(android.R.id.content), getString(R.string.press_back));
            mExit = true;
            mHandler.postDelayed(() -> mExit = false, 2000);
        }
    }

}