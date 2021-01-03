/*
 * Copyright (C) 2021-2022 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of The Translator, An application to help translate android apps.
 *
 */

package com.sunilpaulmathew.translator.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.translator.R;
import com.sunilpaulmathew.translator.adapters.RecycleViewAdapter;
import com.sunilpaulmathew.translator.utils.Translator;
import com.sunilpaulmathew.translator.utils.Utils;

import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 03, 2020
 */

public class TranslatorFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_translator, container, false);

        AppCompatImageView mHelpImg = mRootView.findViewById(R.id.help_image);
        MaterialTextView mHelpTxt = mRootView.findViewById(R.id.help_text);
        FloatingActionButton mFab = mRootView.findViewById(R.id.fab);
        Translator.mRecyclerView = mRootView.findViewById(R.id.recycler_view);

        RecycleViewAdapter mRecycleViewAdapter = new RecycleViewAdapter(Translator.getData(requireActivity()));
        Translator.mRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        if (Utils.exist(requireActivity().getFilesDir().toString() + "/strings.xml")) {
            Translator.mRecyclerView.setAdapter(mRecycleViewAdapter);
        } else {
            mHelpImg.setVisibility(View.VISIBLE);
            mHelpTxt.setVisibility(View.VISIBLE);
            mFab.setVisibility(View.GONE);
        }

        mRecycleViewAdapter.setOnItemClickListener((position, v) -> {
            Utils.dialogEditText(Translator.mData.get(position), getString(R.string.update), mRootView,
                    (dialogInterface1, i1) -> {
                    }, text -> {
                        if (text.isEmpty()) {
                            return;
                        }
                        Utils.create(Objects.requireNonNull(Utils.read(requireActivity().getFilesDir().toString() + "/strings.xml")).replace(">" + Translator.mData.get(position) + "</string>", ">" + text + "</string>"), requireActivity().getFilesDir().toString() + "/strings.xml");
                        Translator.mData.set(position, text);
                        mRecycleViewAdapter.notifyDataSetChanged();
                    }, requireActivity()).setOnDismissListener(dialogInterface -> {
            }).show();
        });

        mFab.setOnClickListener(v -> Translator.saveString(mRootView, requireActivity()));

        return mRootView;
    }

}