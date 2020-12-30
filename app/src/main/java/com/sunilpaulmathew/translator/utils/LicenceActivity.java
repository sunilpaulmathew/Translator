/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of The Translator, An application to help translate android apps.
 *
 */

package com.sunilpaulmathew.translator.utils;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.translator.R;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on December 30, 2020
 */

public class LicenceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licence);

        AppCompatImageButton mBack = findViewById(R.id.back);
        MaterialTextView mCancel = findViewById(R.id.cancel_button);
        mCancel.setOnClickListener(v -> onBackPressed());

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new LicenceFragment()).commit();

        mBack.setOnClickListener(v -> onBackPressed());
    }

    public static class LicenceFragment extends Fragment {

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            WebView webView = new WebView(getActivity());
            webView.loadUrl("file:///android_asset/gpl.html");

            return webView;
        }

    }

}