/*
 * Copyright (C) 2021-2022 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of The Translator, An application to help translate android apps.
 *
 */

package com.sunilpaulmathew.translator.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.translator.BuildConfig;
import com.sunilpaulmathew.translator.R;
import com.sunilpaulmathew.translator.adapters.SettingsAdapter;
import com.sunilpaulmathew.translator.utils.Translator;
import com.sunilpaulmathew.translator.utils.Utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

import in.sunilpaulmathew.sCommon.Utils.sSerializableItems;
import in.sunilpaulmathew.sCommon.Utils.sSingleItemDialog;
import in.sunilpaulmathew.sCommon.Utils.sThemeUtils;
import in.sunilpaulmathew.sCommon.Utils.sTranslatorUtils;
import in.sunilpaulmathew.sCommon.Utils.sUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 24, 2021
 */
public class SettingsActivity extends AppCompatActivity {

    private final ArrayList <sSerializableItems> mData = new ArrayList<>();
    private String mXMLString;

    @SuppressLint("SetTextI18n")
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
        mAppTitle.setTextColor(sUtils.isDarkTheme(this) ? Color.WHITE : Color.BLACK);
        mAppDescription.setText(BuildConfig.APPLICATION_ID);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        SettingsAdapter mRecycleViewAdapter = new SettingsAdapter(mData);
        mRecyclerView.setAdapter(mRecycleViewAdapter);

        mAppInfo.setOnClickListener(v -> {
            Intent settings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            settings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
            settings.setData(uri);
            startActivity(settings);
            finish();
        });

        mData.add(new sSerializableItems(sUtils.getDrawable(R.drawable.ic_theme, this), getString(R.string.app_theme), null,  null));
        if (sUtils.exist(new File(getFilesDir(), "strings.xml"))) {
            mData.add(new sSerializableItems(sUtils.getDrawable(R.drawable.ic_view, this), getString(R.string.view_string), null, null));
        } else {
            mData.add(new sSerializableItems( sUtils.getDrawable(R.drawable.ic_import, this), getString(R.string.import_string_sdcard), null,null));
        }
        mData.add(new sSerializableItems(sUtils.getDrawable(R.drawable.ic_delete, this), getString(R.string.delete_string), null, null));
        mData.add(new sSerializableItems(sUtils.getDrawable(R.drawable.ic_search, this), getString(R.string.find_and_replace), null, null));
        mData.add(new sSerializableItems(sUtils.getDrawable(R.drawable.ic_issue, this), getString(R.string.report_issue), getString(R.string.report_issue_summary), "https://github.com/sunilpaulmathew/Translator/issues/new"));
        mData.add(new sSerializableItems(sUtils.getDrawable(R.drawable.ic_playstore, this),getString(R.string.more_apps), getString(R.string.more_apps_summary), "https://play.google.com/store/apps/dev?id=5836199813143882901"));
        mData.add(new sSerializableItems(sUtils.getDrawable(R.drawable.ic_support, this), getString(R.string.support), getString(R.string.support_summary), "https://t.me/smartpack_kmanager"));
        mData.add(new sSerializableItems(sUtils.getDrawable(R.drawable.ic_github, this), getString(R.string.source_code), getString(R.string.source_code_summary), "https://github.com/sunilpaulmathew/Translator"));
        mData.add(new sSerializableItems(sUtils.getDrawable(R.drawable.ic_share, this), getString(R.string.share_app), getString(R.string.share_app_Summary), null));
        mData.add(new sSerializableItems(sUtils.getDrawable(R.drawable.ic_donate, this), getString(R.string.donations), getString(Utils.isPlayStoreAvailable(this) ? R.string.donations_message :
                R.string.donations_summary), Utils.isPlayStoreAvailable(this) ? "https://play.google.com/store/apps/details?id=com.smartpack.donate" :
                "https://smartpack.github.io/donation/"));
        mData.add(new sSerializableItems(sUtils.getDrawable(R.drawable.ic_rate, this), getString(R.string.rate_us), getString(R.string.rate_us_Summary), "https://play.google.com/store/apps/details?id=com.sunilpaulmathew.translator"));
        mData.add(new sSerializableItems(sUtils.getDrawable(R.drawable.ic_licence, this), getString(R.string.licence), null, "https://www.gnu.org/licenses/gpl-3.0-standalone.html"));
        mData.add(new sSerializableItems(sUtils.getDrawable(R.drawable.ic_credits, this), getString(R.string.credits), null, "https://github.com/sunilpaulmathew/Translator/blob/master/Credits.md"));
        mData.add(new sSerializableItems(sUtils.getDrawable(R.drawable.ic_info, this), getString(R.string.about), null, null));

        mRecycleViewAdapter.setOnItemClickListener((position, v) -> {
            if (mData.get(position).getTextThree() != null) {
                sUtils.launchUrl(mData.get(position).getTextThree(), this);
            } else if (position == 0) {
                sThemeUtils.setAppTheme(this);
            } else if (position == 1) {
                if (sUtils.exist(new File(getFilesDir(), "strings.xml"))) {
                    Intent stringView = new Intent(this, StringViewActivity.class);
                    startActivity(stringView);
                    finish();
                } else {
                    new sSingleItemDialog(R.drawable.ic_import, getString(R.string.import_string_sdcard), new String[] {
                            getString(R.string.device_storage),
                            getString(R.string.url)
                    }, this) {
                        @Override
                        public void onItemSelected(int itemPosition) {
                            switch (itemPosition) {
                                case 0:
                                    Intent importString = new Intent(Intent.ACTION_GET_CONTENT);
                                    importString.setType("text/*");
                                    importString.addCategory(Intent.CATEGORY_OPENABLE);
                                    startActivityForResult(importString, 0);
                                    break;
                                case 1:
                                    Translator.importStringFromURL(SettingsActivity.this);
                                    break;
                            }
                        }
                    }.show();
                }
            } else if (position == 2) {
                if (sUtils.exist(new File(getFilesDir(),"strings.xml"))) {
                    new MaterialAlertDialogBuilder(this)
                            .setMessage(getString(R.string.delete_string_message))
                            .setNegativeButton(getString(R.string.cancel), (dialogInterface3, iv) -> {
                            })
                            .setPositiveButton(getString(R.string.yes), (dialogInterface3, iv) -> {
                                new File(getFilesDir(), "strings.xml").delete();
                                Utils.restartApp(this);
                            }).show();
                } else {
                    sUtils.snackBar(findViewById(android.R.id.content), getString(R.string.import_string_snackbar)).show();
                }
            } else if (position == 3) {
                if (sUtils.exist(new File(getFilesDir(), "strings.xml"))) {
                    Translator.setKeyText(null);
                    Intent findAndReplace = new Intent(this, FindReplaceActivity.class);
                    startActivity(findAndReplace);
                    finish();
                } else {
                    sUtils.snackBar(findViewById(android.R.id.content), getString(R.string.import_string_snackbar)).show();
                }
            } else if (position == 7) {
                new sTranslatorUtils("Translator", "https://github.com/sunilpaulmathew/Translator/blob/master/app/src/main/res/values/strings.xml", this).show();
            } else if (position == 9) {
                Intent share_app = new Intent();
                share_app.setAction(Intent.ACTION_SEND);
                share_app.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                share_app.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message, BuildConfig.VERSION_NAME));
                share_app.setType("text/plain");
                Intent shareIntent = Intent.createChooser(share_app, getString(R.string.share_with));
                startActivity(shareIntent);
            } else if (position == 14) {
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
            Uri selectedFileUri = data.getData();

            if (selectedFileUri != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(selectedFileUri);
                    BufferedInputStream bis = new BufferedInputStream(inputStream);
                    ByteArrayOutputStream buf = new ByteArrayOutputStream();
                    for (int result = bis.read(); result != -1; result = bis.read()) {
                        buf.write((byte) result);
                    }
                    mXMLString = buf.toString("UTF-8");
                } catch (IOException ignored) {
                }

                if (mXMLString == null || !mXMLString.contains("<string name=\"")) {
                    sUtils.snackBar(findViewById(android.R.id.content), getString(R.string.import_string_error, new File(Objects.requireNonNull(selectedFileUri.getPath())).getName())).show();
                    return;
                }
                new MaterialAlertDialogBuilder(this)
                        .setMessage(getString(R.string.select_question, new File(Objects.requireNonNull(selectedFileUri.getPath())).getName()))
                        .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                        })
                        .setPositiveButton(getString(R.string.import_string), (dialogInterface, i) -> {
                            sUtils.create(mXMLString, new File(getFilesDir(), "strings.xml"));
                            Utils.restartApp(this);
                        })
                        .show();
            }
        }
    }

}