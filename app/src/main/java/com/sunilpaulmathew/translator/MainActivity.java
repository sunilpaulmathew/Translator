/*
 * Copyright (C) 2021-2022 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of The Translator, An application to help translate android apps.
 *
 */

package com.sunilpaulmathew.translator;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sunilpaulmathew.translator.fragments.TranslatorFragment;

import java.io.File;

import in.sunilpaulmathew.sCommon.Utils.sCrashReporterUtils;
import in.sunilpaulmathew.sCommon.Utils.sThemeUtils;
import in.sunilpaulmathew.sCommon.Utils.sUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on June 30, 2020
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Initialize App Theme
        sThemeUtils.initializeAppTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new sCrashReporterUtils(sUtils.getDrawable(R.drawable.ic_back, this), new File(getExternalFilesDir("log"), "crashLog"),
                sUtils.getColor(R.color.blue, this), 20).initialize(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new TranslatorFragment()).commit();
    }

}