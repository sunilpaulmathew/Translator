/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of The Translator, An application to help translate android apps.
 *
 */

package com.sunilpaulmathew.translator.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.sunilpaulmathew.translator.R;
import com.sunilpaulmathew.translator.utils.Utils;
import com.sunilpaulmathew.translator.utils.ViewUtils;
import com.sunilpaulmathew.translator.views.recyclerview.DescriptionView;
import com.sunilpaulmathew.translator.views.recyclerview.RecyclerViewItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on June 30, 2020
 */

public class TranslatorFragment extends RecyclerViewFragment {

    private AsyncTask<Void, Void, List<RecyclerViewItem>> mLoader;

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(new SearchFragment());
    }

    @Override
    public int getSpanCount() {
        return super.getSpanCount() + 1;
    }

    @Override
    protected Drawable getBottomFabDrawable() {
        return getResources().getDrawable(R.drawable.ic_save);
    }

    @Override
    protected boolean showBottomFab() {
        return true;
    }

    @Override
    protected void onBottomFabClick() {
        super.onBottomFabClick();

        if (Utils.isStorageWritePermissionDenied(requireActivity())) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            Utils.showSnackbar(getRootView(), getString(R.string.permission_denied_write_storage));
            return;
        }

        if (!Utils.existFile(Utils.mStringPath)) {
            Utils.showSnackbar(getRootView(), getString(R.string.save_string_error));
            return;
        }

        ViewUtils.dialogEditText(null,
                (dialogInterface2, iii) -> {},
                text -> {
                    if (text.isEmpty()) {
                        Utils.showSnackbar(getRootView(), getString(R.string.name_empty));
                        return;
                    }
                    if (!text.endsWith(".xml")) {
                        text += ".xml";
                    }
                    Utils.makeTranslatorFolder();
                    Utils.create("<!--Created by The Translator-->\n" + Utils.readFile(Utils.mStringPath), Environment.getExternalStorageDirectory().toString() + "/" + text);
                    Utils.showSnackbar(getRootView(), getString(R.string.save_string_message, Environment.getExternalStorageDirectory().toString() + "/" + text));
                }, getActivity()).setOnDismissListener(dialogInterface2 -> {
        }).show();
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        reload();
    }

    public void reload() {
        if (mLoader == null) {
            getHandler().postDelayed(new Runnable() {
                @SuppressLint("StaticFieldLeak")
                @Override
                public void run() {
                    mLoader = new AsyncTask<Void, Void, List<RecyclerViewItem>>() {

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            showProgress();
                            requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                        }

                        @Override
                        protected List<RecyclerViewItem> doInBackground(Void... voids) {
                            List<RecyclerViewItem> items = new ArrayList<>();
                            loadInTo(items);
                            return items;
                        }

                        @Override
                        protected void onPostExecute(List<RecyclerViewItem> recyclerViewItems) {
                            super.onPostExecute(recyclerViewItems);

                            if (isAdded()) {
                                clearItems();
                                for (RecyclerViewItem item : recyclerViewItems) {
                                    addItem(item);
                                }

                                hideProgress();
                                mLoader = null;
                            }
                            requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
                        }
                    };
                    mLoader.execute();
                }
            }, 250);
        }
    }

    private void loadInTo(List<RecyclerViewItem> items) {
        if (!Utils.existFile(Utils.mStringPath)) {
            DescriptionView stringEmpty = new DescriptionView();
            stringEmpty.setDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
            stringEmpty.setSummary(getString(R.string.import_string_message));
            stringEmpty.setFullSpan(true);
            items.add(stringEmpty);
            return;
        }
        try {
            for (String line : Objects.requireNonNull(Utils.readFile(Utils.mStringPath)).split("\\r?\\n")) {
                if (line.contains("<string name=") && line.endsWith("</string>") && !line.contains("translatable=\"false")) {
                    String[] finalLine = line.split("\">");

                    String summary = finalLine[1].replace("</string>","").toLowerCase();
                    if ((Utils.mKeyText != null && !summary.contains(Utils.mKeyText))) {
                        continue;
                    }

                    DescriptionView strings = new DescriptionView();
                    strings.setDrawable(ViewUtils.getColoredIcon(R.drawable.ic_arrow, requireContext()));
                    strings.setTitle(finalLine[0].replace("<string name=\"","").replace(" ",""));
                    if (Utils.mKeyText != null && !Utils.mKeyText.isEmpty()) {
                        strings.setSummary(Utils.htmlFrom(summary.replace(Utils.mKeyText,
                                "<b><font color=\"" + "#" + Integer.toHexString(Color.blue(Color.BLUE)) + "\">" + Utils.mKeyText + "</font></b>")));
                    } else {
                        strings.setSummary(finalLine[1].replace("</string>",""));
                    }

                    strings.setFullSpan(true);
                    strings.setOnItemClickListener(item -> {
                        if (Utils.isStorageWritePermissionDenied(requireActivity())) {
                            ActivityCompat.requestPermissions(requireActivity(), new String[]{
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            Utils.showSnackbar(getRootView(), getString(R.string.permission_denied_write_storage));
                        } else {
                            if (summary.contains("%s") || summary.contains("\n") || summary.contains("%d") ||
                                    summary.contains("&amp;") || summary.contains("b>") || summary.contains("i>")) {
                                Utils.showSnackbar(getRootView(), getString(R.string.edit_string_warning));
                            }
                            ViewUtils.dialogEditText(finalLine[1].replace("</string>",""),
                                    (dialogInterface1, i1) -> {
                                    }, text -> {
                                        if (text.isEmpty()) {
                                            return;
                                        }
                                        Utils.create(Objects.requireNonNull(Utils.readFile(Utils.mStringPath)).replace(finalLine[1], text + "</string>"), Utils.mStringPath);
                                        strings.setSummary(text);
                                    }, getActivity()).setOnDismissListener(dialogInterface -> {
                            }).show();
                        }
                    });

                    items.add(strings);
                }
            }
        } catch (Exception ignored) {
        }

    }

    public static class SearchFragment extends BaseFragment {

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Fragment fragment = getParentFragment();
            if (!(fragment instanceof TranslatorFragment)) {
                assert fragment != null;
                fragment = fragment.getParentFragment();
            }
            final TranslatorFragment translatorFragment = (TranslatorFragment) fragment;

            View rootView = inflater.inflate(R.layout.fragment_search, container, false);

            Utils.mKeyEdit = rootView.findViewById(R.id.searchText);
            Utils.mKeyEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    assert translatorFragment != null;
                    Utils.mKeyText = s.toString().toLowerCase();
                    translatorFragment.reload();
                }
            });

            assert translatorFragment != null;
            if (Utils.mKeyText != null) {
                Utils.mKeyEdit.append(Utils.mKeyText);
            }

            return rootView;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLoader != null) {
            mLoader.cancel(true);
        }
        Utils.mKeyText = null;
    }

}