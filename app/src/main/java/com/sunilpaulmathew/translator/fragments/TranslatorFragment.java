/*
 * Copyright (C) 2021-2022 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of The Translator, An application to help translate android apps.
 *
 */

package com.sunilpaulmathew.translator.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.translator.R;
import com.sunilpaulmathew.translator.activities.SettingsActivity;
import com.sunilpaulmathew.translator.adapters.TranslatorAdapter;
import com.sunilpaulmathew.translator.utils.Translator;

import java.io.File;

import in.sunilpaulmathew.sCommon.Utils.sUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 03, 2020
 */
public class TranslatorFragment extends Fragment {

    private boolean mExit;
    private final Handler mHandler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_translator, container, false);

        AppCompatEditText mSearchWord = mRootView.findViewById(R.id.search_Text);
        AppCompatImageButton mSettings = mRootView.findViewById(R.id.settings_menu);
        AppCompatImageButton mSearch = mRootView.findViewById(R.id.search_button);
        LinearLayoutCompat mHelpLayout = mRootView.findViewById(R.id.help_layout);
        MaterialTextView mAboutApp = mRootView.findViewById(R.id.about_app);
        FloatingActionButton mFab = mRootView.findViewById(R.id.fab);

        Translator.initializeRecyclerView(mRootView, R.id.recycler_view);

        mSettings.setOnClickListener(v -> {
            Intent settingsMenu = new Intent(requireActivity(), SettingsActivity.class);
            startActivity(settingsMenu);
        });

        if (!sUtils.exist(new File(requireActivity().getFilesDir(), "strings.xml"))) {
            mSearch.setVisibility(View.GONE);
        }

        mSearch.setOnClickListener(v -> {
            mSearchWord.setVisibility(View.VISIBLE);
            mAboutApp.setVisibility(View.GONE);
            mSearchWord.requestFocus();
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
                Translator.getData(requireActivity()).clear();
                Translator.setKeyText(s.toString().toLowerCase());
                Translator.reloadUI(requireActivity());
            }
        });

        TranslatorAdapter mRecycleViewAdapter = new TranslatorAdapter(Translator.getData(requireActivity()));
        Translator.getRecyclerView().setLayoutManager(new LinearLayoutManager(requireActivity()));
        if (sUtils.exist(new File(requireActivity().getFilesDir(), "strings.xml"))) {
            Translator.getRecyclerView().setAdapter(mRecycleViewAdapter);
        } else {
            mHelpLayout.setVisibility(View.VISIBLE);
            mFab.setVisibility(View.GONE);
        }

        mFab.setOnClickListener(v -> Translator.saveString(requireActivity()));

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (Translator.getKeyText() != null) {
                    mSearchWord.setText(null);
                    Translator.setKeyText(null);
                    return;
                }
                if (mAboutApp.getVisibility() == View.GONE) {
                    mSearchWord.setVisibility(View.GONE);
                    mAboutApp.setVisibility(View.VISIBLE);
                    return;
                }
                if (mExit) {
                    mExit = false;
                    requireActivity().finish();
                } else {
                    sUtils.snackBar(requireActivity().findViewById(android.R.id.content), getString(R.string.press_back)).show();
                    mExit = true;
                    mHandler.postDelayed(() -> mExit = false, 2000);
                }
            }
        });

        return mRootView;
    }

}