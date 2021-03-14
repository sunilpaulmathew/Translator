/*
 * Copyright (C) 2021-2022 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of The Translator, An application to help translate android apps.
 *
 */

package com.sunilpaulmathew.translator.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.translator.BuildConfig;
import com.sunilpaulmathew.translator.R;
import com.sunilpaulmathew.translator.adapters.RecycleViewItem;
import com.sunilpaulmathew.translator.adapters.RecycleViewSettingsAdapter;
import com.sunilpaulmathew.translator.utils.Translator;
import com.sunilpaulmathew.translator.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 24, 2021
 */
public class SettingsActivity extends AppCompatActivity {

    private ArrayList <RecycleViewItem> mData = new ArrayList<>();
    private String mPath;

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        AppCompatImageButton mBack = findViewById(R.id.back_button);
        LinearLayout mAppInfo = findViewById(R.id.app_info);
        MaterialTextView mAppTitle = findViewById(R.id.title);
        MaterialTextView mAppDescription = findViewById(R.id.description);
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);

        mAppTitle.setText(getString(R.string.app_name) + (Utils.isDonated(this) ? " Pro " :  " ") + BuildConfig.VERSION_NAME);
        mAppTitle.setTextColor(Utils.isDarkTheme(this) ? Color.WHITE : Color.BLACK);
        mAppDescription.setText(BuildConfig.APPLICATION_ID);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        RecycleViewSettingsAdapter mRecycleViewAdapter = new RecycleViewSettingsAdapter(mData);
        mRecyclerView.setAdapter(mRecycleViewAdapter);

        mAppInfo.setOnClickListener(v -> {
            Intent settings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            settings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
            settings.setData(uri);
            startActivity(settings);
            finish();
        });

        mData.add(new RecycleViewItem(getString(R.string.dark_theme), null, getResources().getDrawable(R.drawable.ic_theme), null));
        if (Utils.exist(getFilesDir().toString() + "/strings.xml")) {
            mData.add(new RecycleViewItem(getString(R.string.view_string), null, getResources().getDrawable(R.drawable.ic_view), null));
        } else {
            mData.add(new RecycleViewItem(getString(R.string.import_string_sdcard), null, getResources().getDrawable(R.drawable.ic_import), null));
        }
        mData.add(new RecycleViewItem(getString(R.string.delete_string), null, getResources().getDrawable(R.drawable.ic_delete), null));
        mData.add(new RecycleViewItem(getString(R.string.report_issue), getString(R.string.report_issue_summary), getResources().getDrawable(R.drawable.ic_issue), "https://github.com/sunilpaulmathew/Translator/issues/new"));
        mData.add(new RecycleViewItem(getString(R.string.more_apps), getString(R.string.more_apps_summary), getResources().getDrawable(R.drawable.ic_playstore), "https://play.google.com/store/apps/dev?id=5836199813143882901"));
        mData.add(new RecycleViewItem(getString(R.string.support), getString(R.string.support_summary), getResources().getDrawable(R.drawable.ic_support), "https://t.me/smartpack_kmanager"));
        mData.add(new RecycleViewItem(getString(R.string.translations), getString(R.string.translations_summary), getResources().getDrawable(R.drawable.ic_translate), "https://github.com/sunilpaulmathew/Translator/blob/master/app/src/main/res/values/strings.xml"));
        mData.add(new RecycleViewItem(getString(R.string.source_code), getString(R.string.source_code_summary), getResources().getDrawable(R.drawable.ic_github), "https://github.com/sunilpaulmathew/Translator"));
        mData.add(new RecycleViewItem(getString(R.string.share_app), getString(R.string.share_app_Summary), getResources().getDrawable(R.drawable.ic_share), null));
        mData.add(new RecycleViewItem(getString(R.string.donations), getString(Utils.isPlayStoreAvailable(this) ? R.string.donations_message :
                R.string.donations_summary), getResources().getDrawable(R.drawable.ic_donate), Utils.isPlayStoreAvailable(
                        this) ? "https://play.google.com/store/apps/details?id=com.smartpack.donate" : "https://smartpack.github.io/donation/"));
        mData.add(new RecycleViewItem(getString(R.string.rate_us), getString(R.string.rate_us_Summary), getResources().getDrawable(R.drawable.ic_rate), "https://play.google.com/store/apps/details?id=com.sunilpaulmathew.translator"));
        mData.add(new RecycleViewItem(getString(R.string.licence), null, getResources().getDrawable(R.drawable.ic_licence), null));
        mData.add(new RecycleViewItem(getString(R.string.credits), null, getResources().getDrawable(R.drawable.ic_credits), "https://github.com/sunilpaulmathew/Translator/blob/master/Credits.md"));
        mData.add(new RecycleViewItem(getString(R.string.about), null, getResources().getDrawable(R.drawable.ic_info), null));

        mRecycleViewAdapter.setOnItemClickListener((position, v) -> {
            if (mData.get(position).getURL() != null) {
                Utils.launchURL(mData.get(position).getURL(), this);
            } else if (position == 0) {
                new MaterialAlertDialogBuilder(this).setItems(getResources().getStringArray(
                        R.array.app_theme), (dialogInterface, i) -> {
                    switch (i) {
                        case 0:
                            if (!Utils.getBoolean("theme_auto", true, this)) {
                                Utils.saveBoolean("dark_theme", false, this);
                                Utils.saveBoolean("light_theme", false, this);
                                Utils.saveBoolean("theme_auto", true, this);
                                Utils.restartApp(this);
                            }
                            break;
                        case 1:
                            if (!Utils.getBoolean("dark_theme", false, this)) {
                                Utils.saveBoolean("dark_theme", true, this);
                                Utils.saveBoolean("light_theme", false, this);
                                Utils.saveBoolean("theme_auto", false, this);
                                Utils.restartApp(this);
                            }
                            break;
                        case 2:
                            if (!Utils.getBoolean("light_theme", false, this)) {
                                Utils.saveBoolean("dark_theme", false, this);
                                Utils.saveBoolean("light_theme", true, this);
                                Utils.saveBoolean("theme_auto", false, this);
                                Utils.restartApp(this);
                            }
                            break;
                    }
                }).setOnDismissListener(dialogInterface -> {
                }).show();
            } else if (position == 1) {
                if (Utils.exist(getFilesDir().toString() + "/strings.xml")) {
                    Intent stringView = new Intent(this, StringViewActivity.class);
                    startActivity(stringView);
                    finish();
                } else {
                    if (Utils.isStorageWritePermissionDenied(this)) {
                        ActivityCompat.requestPermissions(this, new String[] {
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                        Utils.showSnackbar(findViewById(android.R.id.content), getString(R.string.permission_denied_write_storage));
                    } else {
                        new MaterialAlertDialogBuilder(this).setItems(getResources().getStringArray(
                                R.array.import_options), (dialogInterface, i) -> {
                            switch (i) {
                                case 0:
                                    Intent importString = new Intent(Intent.ACTION_GET_CONTENT);
                                    importString.setType("text/*");
                                    startActivityForResult(importString, 0);
                                    break;
                                case 1:
                                    Translator.importStringFromURL(this);
                                    break;
                            }
                        }).setOnDismissListener(dialogInterface -> {
                        }).show();
                    }
                }
            } else if (position == 2) {
                if (Utils.exist(getFilesDir().toString() + "/strings.xml")) {
                    new MaterialAlertDialogBuilder(this)
                            .setMessage(getString(R.string.delete_string_message))
                            .setNegativeButton(getString(R.string.cancel), (dialogInterface3, iv) -> {
                            })
                            .setPositiveButton(getString(R.string.yes), (dialogInterface3, iv) -> {
                                new File(getFilesDir().toString() + "/strings.xml").delete();
                                Utils.restartApp(this);
                            })
                            .show();
                } else {
                    Utils.showSnackbar(findViewById(android.R.id.content), getString(R.string.import_string_snackbar));
                }
            } else if (position == 8) {
                Intent share_app = new Intent();
                share_app.setAction(Intent.ACTION_SEND);
                share_app.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                share_app.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message, BuildConfig.VERSION_NAME));
                share_app.setType("text/plain");
                Intent shareIntent = Intent.createChooser(share_app, getString(R.string.share_with));
                startActivity(shareIntent);
            } else if (position == 11) {
                Intent licence = new Intent(this, LicenceActivity.class);
                startActivity(licence);
                finish();
            } else if (position == 13) {
                Intent aboutView = new Intent(this, AboutActivity.class);
                startActivity(aboutView);
                finish();
            }
        });

        mBack.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            assert uri != null;
            File file = new File(Objects.requireNonNull(uri.getPath()));
            if (Utils.isDocumentsUI(uri)) {
                @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    mPath = Environment.getExternalStorageDirectory().toString() + "/Download/" +
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } else {
                mPath = Utils.getPath(file);
            }
            if (!mPath.endsWith(".xml")) {
                Utils.showSnackbar(findViewById(android.R.id.content), getString(R.string.wrong_extension, ".xml"));
                return;
            }
            if (!Objects.requireNonNull(Utils.read(mPath)).contains("<string name=\"")) {
                Utils.showSnackbar(findViewById(android.R.id.content), getString(R.string.import_string_error, new File(mPath).getName()));
                return;
            }
            new MaterialAlertDialogBuilder(this)
                    .setMessage(getString(R.string.select_question, new File(mPath).toString()))
                    .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                    })
                    .setPositiveButton(getString(R.string.import_string), (dialogInterface, i) -> {
                        Utils.create(Utils.read(mPath), getFilesDir().toString() + "/strings.xml");
                        Utils.restartApp(this);
                    })
                    .show();
        }
    }

}