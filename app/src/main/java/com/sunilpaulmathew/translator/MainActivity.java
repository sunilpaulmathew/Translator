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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.view.Menu;
import android.view.SubMenu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.sunilpaulmathew.translator.fragments.TranslatorFragment;
import com.sunilpaulmathew.translator.utils.AboutActivity;
import com.sunilpaulmathew.translator.utils.PagerAdapter;
import com.sunilpaulmathew.translator.utils.Utils;
import com.sunilpaulmathew.translator.utils.ViewUtils;
import com.sunilpaulmathew.translator.views.dialog.Dialog;

import java.io.File;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on June 30, 2020
 */

public class MainActivity extends AppCompatActivity {

    private boolean mExit;
    private Handler mHandler = new Handler();
    private String copyright = Environment.getExternalStorageDirectory().toString() + "/Translator/copyright";
    private String mPath;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Initialize App Theme
        Utils.initializeAppTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatImageButton settings = findViewById(R.id.settings_menu);
        settings.setImageDrawable(getResources().getDrawable(R.drawable.ic_settings));
        settings.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, settings);
            Menu menu = popupMenu.getMenu();
            if (Utils.existFile(Utils.mStringPath)) {
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
                        if (!Utils.existFile(Utils.mStringPath)) {
                            Utils.showSnackbar(mViewPager, getString(R.string.delete_string_error));
                        } else {
                            new Dialog(this)
                                    .setMessage(getString(R.string.delete_string_message))
                                    .setNegativeButton(getString(R.string.cancel), (dialogInterface3, iv) -> {
                                    })
                                    .setPositiveButton(getString(R.string.yes), (dialogInterface3, iv) -> {
                                        new File(Utils.mStringPath).delete();
                                        Utils.restartApp(this);
                                    })
                                    .show();
                        }
                        break;
                    case 8:
                        if (Utils.isStorageWritePermissionDenied(this)) {
                            ActivityCompat.requestPermissions(this, new String[]{
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                            Utils.showSnackbar(mViewPager, getString(R.string.permission_denied_write_storage));
                        } else {
                            Intent importString = new Intent(Intent.ACTION_GET_CONTENT);
                            importString.setType("text/*");
                            startActivityForResult(importString, 0);
                        }
                        break;
                    case 9:
                        new Dialog(this)
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
                }
                return false;
            });
            popupMenu.show();
        });

        mViewPager = findViewById(R.id.viewPagerID);
        AppCompatTextView copyRightText = findViewById(R.id.copyright_Text);

        // Allow changing Copyright Text
        if (Utils.readFile(copyright) != null) {
            copyRightText.setText(Utils.readFile(copyright));
        } else {
            copyRightText.setText(R.string.copyright);
        }
        copyRightText.setOnLongClickListener(item -> {
            setCopyRightText();
            return false;
        });

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new TranslatorFragment(), "");
        mViewPager.setAdapter(adapter);
    }

    private void setCopyRightText() {
        if (Utils.isStorageWritePermissionDenied(this)) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            Utils.showSnackbar(mViewPager, getString(R.string.permission_denied_write_storage));
            return;
        }
        Utils.makeTranslatorFolder();
        ViewUtils.dialogEditText(Utils.readFile(copyright),
                (dialogInterface, i) -> {
                }, text -> {
                    if (text.equals(Utils.readFile(copyright))) return;
                    if (text.isEmpty()) {
                        new File(copyright).delete();
                        Utils.showSnackbar(mViewPager, getString(R.string.copyright_default, getString(R.string.copyright)));
                        return;
                    }
                    Utils.create(text, copyright);
                    Utils.showSnackbar(mViewPager, getString(R.string.copyright_message, text));
                }, this).setOnDismissListener(dialogInterface -> {
        }).show();
    }

    private void launchPS(String url) {
        if (!Utils.isNetworkAvailable(this)) {
            Utils.showSnackbar(mViewPager, getString(R.string.no_internet));
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
            Utils.showSnackbar(mViewPager, getString(R.string.no_internet));
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
                Utils.showSnackbar(mViewPager, getString(R.string.wrong_extension, ".xml"));
                return;
            }
            new Dialog(this)
                    .setMessage(getString(R.string.select_question, new File(mPath).toString()))
                    .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                    })
                    .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                        Utils.makeTranslatorFolder();
                        Utils.create(Utils.readFile(mPath), Utils.mStringPath);
                        Utils.restartApp(this);
                    })
                    .show();
        }
    }

    @Override
    public void onBackPressed() {
        if (Utils.mKeyText != null) {
            Utils.mKeyEdit.setText(null);
            Utils.mKeyText = null;
            return;
        }
        if (mExit) {
            mExit = false;
            super.onBackPressed();
        } else {
            Utils.showSnackbar(mViewPager, getString(R.string.press_back));
            mExit = true;
            mHandler.postDelayed(() -> mExit = false, 2000);
        }
    }

}