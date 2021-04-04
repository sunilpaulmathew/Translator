/*
 * Copyright (C) 2021-2022 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of The Translator, An application to help translate android apps.
 *
 */

package com.sunilpaulmathew.translator.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sunilpaulmathew.translator.BuildConfig;
import com.sunilpaulmathew.translator.R;
import com.sunilpaulmathew.translator.adapters.RecycleViewAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 03, 2020
 */

public class Translator {

    public static List<String> mData = new ArrayList<>();
    public static RecyclerView mRecyclerView;
    public static String mFindText, mKeyText;

    public static void saveString(Activity activity) {
        if (Utils.isStorageWritePermissionDenied(activity)) {
            ActivityCompat.requestPermissions(activity, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            Utils.showSnackbar(activity.findViewById(android.R.id.content), activity.getString(R.string.permission_denied_write_storage));
            return;
        }
        Utils.dialogEditText("strings-" + java.util.Locale.getDefault().getLanguage(), activity.getString(R.string.save),
                activity.findViewById(android.R.id.content), (dialogInterface2, iii) -> {},
                text -> {
                    if (text.isEmpty()) {
                        Utils.showSnackbar(activity.findViewById(android.R.id.content), activity.getString(R.string.name_empty));
                        return;
                    }
                    if (!text.endsWith(".xml")) {
                        text += ".xml";
                    }
                    String mString = getExportPath() + "/" + text;
                    if (Utils.exist(getExportPath() + "/" + text)) {
                        new MaterialAlertDialogBuilder(activity)
                                .setMessage(activity.getString(R.string.save_string_replace, text))
                                .setNegativeButton(activity.getString(R.string.cancel), (dialogInterface, i) -> {
                                })
                                .setPositiveButton(activity.getString(R.string.replace), (dialogInterface, i) -> {
                                    Utils.create(getStrings(activity), mString);
                                })
                                .show();
                        return;
                    }
                    Utils.create(getStrings(activity), mString);
                    new MaterialAlertDialogBuilder(activity)
                            .setMessage(activity.getString(R.string.save_string_message, mString))
                            .setNegativeButton(activity.getString(R.string.cancel), (dialogInterface, i) -> {
                            })
                            .setPositiveButton(activity.getString(R.string.share), (dialogInterface, i) -> {
                                Uri uriFile = FileProvider.getUriForFile(activity,
                                        BuildConfig.APPLICATION_ID + ".provider", new File(mString));
                                Intent shareScript = new Intent(Intent.ACTION_SEND);
                                shareScript.setType("application/xml");
                                shareScript.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.shared_by, new File(mString).getName()));
                                shareScript.putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.share_message, BuildConfig.VERSION_NAME));
                                shareScript.putExtra(Intent.EXTRA_STREAM, uriFile);
                                shareScript.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                activity.startActivity(Intent.createChooser(shareScript, activity.getString(R.string.share_with)));
                            })
                            .show();
                }, activity).setOnDismissListener(dialogInterface2 -> {
        }).show();
    }

    public static String getStrings(Context context) {
        List<String> mData = new ArrayList<>();
        if (Utils.exist(context.getFilesDir().toString() + "/strings.xml")) {
            for (String line : Objects.requireNonNull(Utils.read(context.getFilesDir().toString() + "/strings.xml")).split("\\r?\\n")) {
                if (line.contains("<string name=") && line.endsWith("</string>") && !line.contains("translatable=\"false")) {
                    mData.add(line);
                }
            }
        }
        return "<resources xmlns:tools=\"http://schemas.android.com/tools\" tools:ignore=\"MissingTranslation\">\n<!--Created by The Translator <https://play.google.com/store/apps/details?id=com.sunilpaulmathew.translator>-->\n\n" +
                mData.toString().replace("[","").replace("]","").replace(",","\n") + "\n</resources>";
    }

    public static void deleteSingleString(String string, Context context) {
        for (String line : Objects.requireNonNull(Utils.read(context.getFilesDir().toString() + "/strings.xml")).split("\\r?\\n")) {
            if (line.contains("<string name=") && line.endsWith("</string>") && !line.contains("translatable=\"false")) {
                if (line.endsWith(string)) {
                    Utils.create(Objects.requireNonNull(Utils.read(context.getFilesDir().toString() + "/strings.xml")).replace(line, ""), context.getFilesDir().toString() + "/strings.xml");
                }
            }
        }
    }

    public static boolean checkIllegalCharacters(String string) {
        String[] array = string.trim().split("\\s+");
        for (String s : array) {
            if (!s.matches("&gt;|&lt;|&amp;") && s.startsWith("&")
                    || s.startsWith("<") && s.length() == 1 || s.startsWith(">") && s.length() == 1
                    || s.startsWith("<b") && s.length() <= 3 || s.startsWith("</") && s.length() <= 4
                    || s.startsWith("<i") && s.length() <= 3 || s.startsWith("\"") || s.startsWith("'"))
                return true;
        }
        return false;
    }

    public static String getSpecialCharacters(String string) {
        StringBuilder sb = new StringBuilder();
        if (string.contains("%s")) {
            sb.append(" - %s");
        }
        if (string.contains("\n")) {
            sb.append(" - \n");
        }
        if (string.contains("\\'")) {
            sb.append(" - \\'");
        }
        if (string.contains("<b>")) {
            sb.append(" - <b>");
        }
        if (string.contains("</b>")) {
            sb.append(" - </b>");
        }
        if (string.contains("<i>")) {
            sb.append(" - <i>");
        }
        if (string.contains("</i>")) {
            sb.append(" - </i>");
        }
        return sb.toString().replaceFirst(" - ","");
    }

    private static String getExportPath() {
        return Environment.getExternalStorageDirectory().toString();
    }

    public static List<String> getData(Context context) {
        mData.clear();
        if (Utils.exist(context.getFilesDir().toString() + "/strings.xml")) {
            for (String line : Objects.requireNonNull(Utils.read(context.getFilesDir().toString() + "/strings.xml")).split("\\r?\\n")) {
                if (line.contains("<string name=") && line.endsWith("</string>") && !line.contains("translatable=\"false")) {
                    String[] finalLine = line.split("\">");
                    if (mKeyText == null) {
                        mData.add(finalLine[1].replace("</string>", ""));
                    } else if (finalLine[1].toLowerCase().contains(mKeyText)) {
                        mData.add(finalLine[1].replace("</string>", ""));
                    }
                }
            }
        }
        return mData;
    }

    public static void importStringFromURL(Activity activity) {
        Utils.dialogEditText(null, activity.getString(R.string.import_string), mRecyclerView,
                (dialogInterface1, i1) -> {
                }, text -> {
                    if (text.isEmpty()) {
                        return;
                    }
                    if (text.contains("blob")) {
                        text = text.replace("blob","raw");
                    }
                    String url = text;
                    new AsyncTask<Void, Void, Void>() {
                        private ProgressDialog mProgressDialog;
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            mProgressDialog = new ProgressDialog(activity);
                            mProgressDialog.setMessage(activity.getString(R.string.importing));
                            mProgressDialog.setCancelable(false);
                            mProgressDialog.show();
                        }
                        @Override
                        protected Void doInBackground(Void... voids) {
                            Utils.download(url, activity.getFilesDir().toString() + "/strings.xml");
                            return null;
                        }
                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            try {
                                mProgressDialog.dismiss();
                            } catch (IllegalArgumentException ignored) {}
                            Utils.restartApp(activity);
                        }
                    }.execute();
                }, activity).setOnDismissListener(dialogInterface -> {
        }).show();
    }

    public static void reloadUI(Context context) {
        mRecyclerView.setAdapter(new RecycleViewAdapter(getData(context)));
    }

}