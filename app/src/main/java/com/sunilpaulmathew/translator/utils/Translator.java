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
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sunilpaulmathew.translator.BuildConfig;
import com.sunilpaulmathew.translator.R;
import com.sunilpaulmathew.translator.adapters.TranslatorAdapter;
import com.sunilpaulmathew.translator.interfaces.DialogEditTextListener;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import in.sunilpaulmathew.sCommon.Utils.sExecutor;
import in.sunilpaulmathew.sCommon.Utils.sPermissionUtils;
import in.sunilpaulmathew.sCommon.Utils.sUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 03, 2020
 */
public class Translator {

    private static RecyclerView mRecyclerView;
    private static String mKeyText;

    public static boolean isTextMatched(String text) {
        for (int a = 0; a < text.length() - mKeyText.length() + 1; a++) {
            if (mKeyText.equalsIgnoreCase(text.substring(a, a + mKeyText.length()))) {
                return true;
            }
        }
        return false;
    }

    public static List<StringsItem> getRawData(Context context) {
        List<StringsItem> mData = new ArrayList<>();
        if (sUtils.exist(new File(context.getFilesDir(), "strings.xml"))) {
            for (String line : Objects.requireNonNull(sUtils.read(new File(context.getFilesDir(), "strings.xml"))).split("\\r?\\n")) {
                if (line.contains("<string name=") && line.endsWith("</string>") && !line.contains("translatable=\"false")) {
                    if (line.endsWith("\"</string>")) {
                        line = line.replace("\"</string>", "");
                    }
                    if (line.endsWith("</string>")) {
                        line = line.replace("</string>", "");
                    }
                    if (line.contains("\">\"")) {
                        line = line.replace("\">\"", "\">");
                    }
                    String[] finalLine = line.split("\">");
                    mData.add(new StringsItem(finalLine[0], finalLine[1]));
                }
            }
        }
        return mData;
    }

    public static List<StringsItem> getData(Context context) {
        List<StringsItem> mData = new ArrayList<>();
        for (StringsItem item : getRawData(context)) {
            if (mKeyText == null) {
                mData.add(item);
            } else if (isTextMatched(item.getDescription())) {
                mData.add(item);
            }
        }
        return mData;
    }

    public static RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    private static String getExportPath() {
        if (Build.VERSION.SDK_INT >= 29) {
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        } else {
            return Environment.getExternalStorageDirectory().toString();
        }
    }

    public static String getKeyText() {
        return mKeyText;
    }

    public static String getStrings(Context context) {
        StringBuilder sb = new StringBuilder();
        sb.append("<resources xmlns:tools=\"http://schemas.android.com/tools\" tools:ignore=\"MissingTranslation\">").append("\n");
        sb.append("<!--Created by The Translator <https://play.google.com/store/apps/details?id=com.sunilpaulmathew.translator>-->\n\n");
        for (StringsItem item : getRawData(context)) {
            sb.append(item.getTitle()).append("\">\"").append(item.getDescription()).append("\"</string>").append("\n");
        }
        sb.append("</resources>");
        return sb.toString();
    }

    public static void deleteSingleString(StringsItem itemToDelete, Context context) {
        StringBuilder sb = new StringBuilder();
        sb.append("<resources xmlns:tools=\"http://schemas.android.com/tools\" tools:ignore=\"MissingTranslation\">").append("\n");
        sb.append("<!--Created by The Translator <https://play.google.com/store/apps/details?id=com.sunilpaulmathew.translator>-->\n\n");
        for (StringsItem item : getRawData(context)) {
            if (!item.getDescription().equals(itemToDelete.getDescription())) {
                sb.append(item.getTitle()).append("\">\"").append(item.getDescription()).append("\"</string>").append("\n");
            }
        }
        sb.append("</resources>");
        sUtils.create(sb.toString(), new File(context.getFilesDir(), "strings.xml"));
    }

    public static void importStringFromURL(Activity activity) {
        DialogEditTextListener.dialogEditText(null, activity.getString(R.string.import_string),
                (dialogInterface1, i1) -> {
                }, text -> {
                    if (text.isEmpty()) {
                        return;
                    }
                    if (text.contains("blob")) {
                        text = text.replace("blob","raw");
                    }
                    String url = text;
                    new sExecutor() {
                        private ProgressDialog mProgressDialog;

                        @Override
                        public void onPreExecute() {
                            mProgressDialog = new ProgressDialog(activity);
                            mProgressDialog.setMessage(activity.getString(R.string.importing));
                            mProgressDialog.setCancelable(false);
                            mProgressDialog.show();
                        }

                        @Override
                        public void doInBackground() {
                            Utils.download(url, activity.getFilesDir().toString() + "/strings.xml");
                        }

                        @Override
                        public void onPostExecute() {
                            try {
                                mProgressDialog.dismiss();
                            } catch (IllegalArgumentException ignored) {}
                            Utils.restartApp(activity);
                        }
                    }.execute();
                }, activity).setOnDismissListener(dialogInterface -> {
        }).show();
    }

    public static void initializeRecyclerView(View view, int id) {
        mRecyclerView = view.findViewById(id);
    }

    public static void reloadUI(Context context) {
        mRecyclerView.setAdapter(new TranslatorAdapter(getData(context)));
    }

    public static void saveString(Activity activity) {
        DialogEditTextListener.dialogEditText("strings-" + Locale.getDefault().getLanguage(), activity.getString(R.string.save), (dialogInterface2, iii) -> {},
                text -> {
                    if (text.isEmpty()) {
                        sUtils.snackBar(activity.findViewById(android.R.id.content), activity.getString(R.string.name_empty)).show();
                        return;
                    }
                    if (!text.endsWith(".xml")) {
                        text += ".xml";
                    }
                    if (sUtils.exist(new File(getExportPath(), text))) {
                        String finalText = text;
                        new MaterialAlertDialogBuilder(activity)
                                .setMessage(activity.getString(R.string.save_string_replace, text))
                                .setNegativeButton(activity.getString(R.string.cancel), (dialogInterface, i) -> {
                                })
                                .setPositiveButton(activity.getString(R.string.replace), (dialogInterface, i) -> writeFile(finalText, activity))
                                .show();
                        return;
                    }
                    writeFile(text, activity);
                }, activity).setOnDismissListener(dialogInterface2 -> {
        }).show();
    }

    public static void setKeyText(String keyText) {
        mKeyText = keyText;
    }

    private static void writeFile(String name, Activity activity) {
        if (Build.VERSION.SDK_INT >= 29) {
            try {
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "application/xml");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                Uri uri = activity.getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
                OutputStream outputStream = activity.getContentResolver().openOutputStream(Objects.requireNonNull(uri));
                Objects.requireNonNull(outputStream).write(getStrings(activity).getBytes());
                outputStream.close();
            } catch (IOException ignored) {
            }
        } else {
            if (sPermissionUtils.isPermissionDenied(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, activity)) {
                ActivityCompat.requestPermissions(activity, new String[] {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                sUtils.snackBar(activity.findViewById(android.R.id.content), activity.getString(R.string.permission_denied_write_storage)).show();
                return;
            }
            sUtils.create(getStrings(activity), new File(getExportPath(), name));
        }
        new MaterialAlertDialogBuilder(activity)
                .setMessage(activity.getString(R.string.save_string_message, getExportPath() + "/" + name))
                .setNegativeButton(activity.getString(R.string.cancel), (dialogInterface, i) -> {
                })
                .setPositiveButton(activity.getString(R.string.share), (dialogInterface, i) -> {
                    Uri uriFile = FileProvider.getUriForFile(activity,
                            BuildConfig.APPLICATION_ID + ".provider", new File(getExportPath() + "/" + name));
                    Intent shareScript = new Intent(Intent.ACTION_SEND);
                    shareScript.setType("application/xml");
                    shareScript.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.shared_by, new File(getExportPath(), name).getName()));
                    shareScript.putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.share_message, BuildConfig.VERSION_NAME));
                    shareScript.putExtra(Intent.EXTRA_STREAM, uriFile);
                    shareScript.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    activity.startActivity(Intent.createChooser(shareScript, activity.getString(R.string.share_with)));
                })
                .show();
    }

}