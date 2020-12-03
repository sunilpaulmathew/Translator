/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of The Translator, An application to help translate android apps.
 *
 */

package com.sunilpaulmathew.translator;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sunilpaulmathew.translator.utils.AboutActivity;
import com.sunilpaulmathew.translator.utils.StringViewActivity;
import com.sunilpaulmathew.translator.utils.Utils;
import com.sunilpaulmathew.translator.views.RecycleViewAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on June 30, 2020
 */

public class MainActivity extends AppCompatActivity {

    private AppCompatEditText mSearchWord;
    private AppCompatImageView mHelpImg;
    private AppCompatTextView mAboutApp;
    private AppCompatTextView mHelpTxt;
    private boolean mExit;
    private FloatingActionButton mFab;
    private Handler mHandler = new Handler();
    private List<String> mData = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private String mPath;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Initialize App Theme
        Utils.initializeAppTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHelpImg = findViewById(R.id.help_image);
        mHelpTxt = findViewById(R.id.help_text);
        mAboutApp = findViewById(R.id.about_app);
        mSearchWord = findViewById(R.id.search_Text);
        mFab = findViewById(R.id.fab);
        mRecyclerView = findViewById(R.id.recycler_view);
        AppCompatImageButton mSettings = findViewById(R.id.settings_menu);
        AppCompatImageButton mSearch = findViewById(R.id.search_button);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new RecycleViewAdapter(getData()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mAboutApp.setVisibility(View.VISIBLE);

        mFab.setOnClickListener(v -> Utils.getInstance().saveString(mRecyclerView, this));

        mSettings.setImageDrawable(getResources().getDrawable(R.drawable.ic_settings));
        mSettings.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, mSettings);
            Menu menu = popupMenu.getMenu();
            if (Utils.existFile(getFilesDir().toString() + "/strings.xml")) {
                menu.add(Menu.NONE, 12, Menu.NONE, getString(R.string.view_string));
                menu.add(Menu.NONE, 7, Menu.NONE, getString(R.string.delete_string));
            } else {
                menu.add(Menu.NONE, 8, Menu.NONE, getString(R.string.import_string_sdcard));
            }
            SubMenu appTheme = menu.addSubMenu(Menu.NONE, 0, Menu.NONE, getString(R.string.dark_theme));
            appTheme.add(Menu.NONE, 1, Menu.NONE, getString(R.string.dark_theme_auto)).setCheckable(true)
                    .setChecked(Utils.getBoolean("theme_auto", true, this));
            appTheme.add(Menu.NONE, 2, Menu.NONE, getString(R.string.dark_theme_enable)).setCheckable(true)
                    .setChecked(Utils.getBoolean("dark_theme", false, this));
            appTheme.add(Menu.NONE, 3, Menu.NONE, getString(R.string.dark_theme_disable)).setCheckable(true)
                    .setChecked(Utils.getBoolean("light_theme", false, this));
            SubMenu about = menu.addSubMenu(Menu.NONE, 0, Menu.NONE, getString(R.string.about));
            about.add(Menu.NONE, 13, Menu.NONE, getString(R.string.share_app));
            about.add(Menu.NONE, 4, Menu.NONE, getString(R.string.support));
            about.add(Menu.NONE, 5, Menu.NONE, getString(R.string.more_apps));
            about.add(Menu.NONE, 10, Menu.NONE, getString(R.string.report_issue));
            about.add(Menu.NONE, 11, Menu.NONE, getString(R.string.source_code));
            if (Utils.isNotDonated(this)) {
                about.add(Menu.NONE, 9, Menu.NONE, getString(R.string.donations));
            }
            about.add(Menu.NONE, 6, Menu.NONE, getString(R.string.about));
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case 0:
                        break;
                    case 1:
                        if (!Utils.getBoolean("theme_auto", true, this)) {
                            Utils.saveBoolean("dark_theme", false, this);
                            Utils.saveBoolean("light_theme", false, this);
                            Utils.saveBoolean("theme_auto", true, this);
                            Utils.restartApp(this);
                        }
                        break;
                    case 2:
                        if (!Utils.getBoolean("dark_theme", false, this)) {
                            Utils.saveBoolean("dark_theme", true, this);
                            Utils.saveBoolean("light_theme", false, this);
                            Utils.saveBoolean("theme_auto", false, this);
                            Utils.restartApp(this);
                        }
                        break;
                    case 3:
                        if (!Utils.getBoolean("light_theme", false, this)) {
                            Utils.saveBoolean("dark_theme", false, this);
                            Utils.saveBoolean("light_theme", true, this);
                            Utils.saveBoolean("theme_auto", false, this);
                            Utils.restartApp(this);
                        }
                        break;
                    case 4:
                        launchURL("https://t.me/smartpack_kmanager");
                        break;
                    case 5:
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(
                                "https://play.google.com/store/apps/dev?id=5836199813143882901"));
                        startActivity(intent);
                        break;
                    case 6:
                        Intent aboutView = new Intent(this, AboutActivity.class);
                        startActivity(aboutView);
                        break;
                    case 7:
                        new AlertDialog.Builder(this)
                                .setMessage(getString(R.string.delete_string_message))
                                .setNegativeButton(getString(R.string.cancel), (dialogInterface3, iv) -> {
                                })
                                .setPositiveButton(getString(R.string.yes), (dialogInterface3, iv) -> {
                                    new File(getFilesDir().toString() + "/strings.xml").delete();
                                    Utils.restartApp(this);
                                })
                                .show();
                        break;
                    case 8:
                        if (Utils.isStorageWritePermissionDenied(this)) {
                            ActivityCompat.requestPermissions(this, new String[]{
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                            Utils.showSnackbar(mRecyclerView, getString(R.string.permission_denied_write_storage));
                        } else {
                            Intent importString = new Intent(Intent.ACTION_GET_CONTENT);
                            importString.setType("text/*");
                            startActivityForResult(importString, 0);
                        }
                        break;
                    case 9:
                        new AlertDialog.Builder(this)
                                .setIcon(R.mipmap.ic_launcher)
                                .setTitle(getString(R.string.donations))
                                .setMessage(getString(R.string.donations_message))
                                .setNeutralButton(getString(R.string.cancel), (dialog1, id1) -> {
                                })
                                .setPositiveButton(getString(R.string.donation_app), (dialogInterface, i) -> launchPS(
                                        "https://play.google.com/store/apps/details?id=com.smartpack.donate"))
                                .show();
                        break;
                    case 10:
                        launchURL("https://github.com/sunilpaulmathew/Translator/issues/new");
                        break;
                    case 11:
                        launchURL("https://github.com/sunilpaulmathew/Translator");
                        break;
                    case 12:
                        Intent stringView = new Intent(this, StringViewActivity.class);
                        startActivity(stringView);
                        break;
                    case 13:
                        Intent share_app = new Intent();
                        share_app.setAction(Intent.ACTION_SEND);
                        share_app.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                        share_app.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message, BuildConfig.VERSION_NAME));
                        share_app.setType("text/plain");
                        Intent shareIntent = Intent.createChooser(share_app, getString(R.string.share_with));
                        startActivity(shareIntent);
                        break;
                }
                return false;
            });
            popupMenu.show();
        });

        if (!Utils.existFile(getFilesDir().toString() + "/strings.xml")) {
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
                mData.clear();
                Utils.mKeyText = s.toString().toLowerCase();
                mRecyclerView.setAdapter(new RecycleViewAdapter(getSearchData()));
            }
        });
    }

    private List<String> getData() {
        if (Utils.existFile(getFilesDir().toString() + "/strings.xml")) {
            for (String line : Objects.requireNonNull(Utils.readFile(getFilesDir().toString() + "/strings.xml")).split("\\r?\\n")) {
                if (line.contains("<string name=") && line.endsWith("</string>") && !line.contains("translatable=\"false")) {
                    String[] finalLine = line.split("\">");
                    mData.add(finalLine[1].replace("</string>", ""));
                }
            }
        } else {
            mHelpImg.setVisibility(View.VISIBLE);
            mHelpTxt.setVisibility(View.VISIBLE);
            mFab.setVisibility(View.GONE);
        }
        return mData;
    }

    private List<String> getSearchData() {
        mData.clear();
        if (Utils.existFile(getFilesDir().toString() + "/strings.xml")) {
            for (String line : Objects.requireNonNull(Utils.readFile(getFilesDir().toString() + "/strings.xml")).split("\\r?\\n")) {
                if (line.contains("<string name=") && line.endsWith("</string>") && !line.contains("translatable=\"false")) {
                    String[] finalLine = line.split("\">");
                    if (Utils.mKeyText != null && finalLine[1].toLowerCase().contains(Utils.mKeyText)) {
                        mData.add(finalLine[1].replace("</string>", ""));
                    }
                }
            }
        }
        return mData;
    }

    private void launchPS(String url) {
        if (!Utils.isNetworkAvailable(this)) {
            Utils.showSnackbar(mRecyclerView, getString(R.string.no_internet));
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }
    }

    private void launchURL(String url) {
        if (Utils.isNetworkAvailable(this)) {
            Utils.launchURL(url, this);
        } else {
            Utils.showSnackbar(mRecyclerView, getString(R.string.no_internet));
        }
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
            if (!Utils.getExtension(mPath).equals("xml")) {
                Utils.showSnackbar(mRecyclerView, getString(R.string.wrong_extension, ".xml"));
                return;
            }
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.select_question, new File(mPath).toString()))
                    .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                    })
                    .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                        Utils.create(Utils.readFile(mPath), getFilesDir().toString() + "/strings.xml");
                        Utils.restartApp(this);
                    })
                    .show();
        }
    }

    @Override
    public void onBackPressed() {
        if (Utils.mKeyText != null) {
            mSearchWord.setText(null);
            Utils.mKeyText = null;
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
            Utils.showSnackbar(mRecyclerView, getString(R.string.press_back));
            mExit = true;
            mHandler.postDelayed(() -> mExit = false, 2000);
        }
    }

}