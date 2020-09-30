/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of The Translator, An application to help translate android apps.
 *
 */

package com.sunilpaulmathew.translator.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;
import com.sunilpaulmathew.translator.BuildConfig;
import com.sunilpaulmathew.translator.MainActivity;
import com.sunilpaulmathew.translator.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on June 30, 2020
 * Based on the original implementation on Kernel Adiutor by
 * Willi Ye <williye97@gmail.com>
 */

public class Utils {

    private static Utils sUtils;

    public static Utils getInstance() {
        if (sUtils == null) {
            sUtils = new Utils();
        }
        return sUtils;
    }

    public static boolean isPackageInstalled(String packageID, Context context) {
        try {
            context.getPackageManager().getApplicationInfo(packageID, 0);
            return true;
        } catch (PackageManager.NameNotFoundException ignored) {
            return false;
        }
    }

    public static boolean isNotDonated(Context context) {
        if (BuildConfig.DEBUG) return false;
        return !isPackageInstalled("com.smartpack.donate", context);
    }

    public static void initializeAppTheme(Context context) {
        if (getBoolean("dark_theme", false, context)) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);
        } else if (getBoolean("light_theme", false, context)) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    static String readAssetFile(Context context, String file) {
        InputStream input = null;
        BufferedReader buf = null;
        try {
            StringBuilder s = new StringBuilder();
            input = context.getAssets().open(file);
            buf = new BufferedReader(new InputStreamReader(input));

            String str;
            while ((str = buf.readLine()) != null) {
                s.append(str).append("\n");
            }
            return s.toString().trim();
        } catch (IOException ignored) {
        } finally {
            try {
                if (input != null) input.close();
                if (buf != null) buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void launchURL(String url, Activity activity) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            activity.startActivity(i);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String readFile(String file) {
        BufferedReader buf = null;
        try {
            buf = new BufferedReader(new FileReader(file));

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = buf.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            return stringBuilder.toString().trim();
        } catch (IOException ignored) {
        } finally {
            try {
                if (buf != null) buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean existFile(String file) {
        return new File(file).exists();
    }

    public static void create(String text, String path) {
        try {
            File logFile = new File(path);
            logFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(logFile);
            OutputStreamWriter myOutWriter =
                    new OutputStreamWriter(fOut);
            myOutWriter.append(text);
            myOutWriter.close();
            fOut.close();
        } catch (Exception ignored) {
        }
    }

    public static void showSnackbar(View view, String message) {
        Snackbar snackBar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
        snackBar.setAction(R.string.dismiss, v -> snackBar.dismiss());
        snackBar.show();
    }

    public void saveString(View view, Activity context) {
        if (isStorageWritePermissionDenied(context)) {
            ActivityCompat.requestPermissions(context, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            showSnackbar(view, context.getString(R.string.permission_denied_write_storage));
            return;
        }
        ViewUtils.dialogEditText("strings-" + java.util.Locale.getDefault().getLanguage(), context.getString(R.string.save),
                (dialogInterface2, iii) -> {},
                text -> {
                    if (text.isEmpty()) {
                        showSnackbar(view, context.getString(R.string.name_empty));
                        return;
                    }
                    if (!text.endsWith(".xml")) {
                        text += ".xml";
                    }
                    if (existFile(Environment.getExternalStorageDirectory().toString() + "/" + text)) {
                        showSnackbar(view, context.getString(R.string.already_exists, text));
                        return;
                    }
                    create(getStrings(context), Environment.getExternalStorageDirectory().toString() + "/" + text);
                    showSnackbar(view, context.getString(R.string.save_string_message, Environment.getExternalStorageDirectory().toString() + "/" + text));
                }, context).setOnDismissListener(dialogInterface2 -> {
        }).show();
    }

    public static String getStrings(Context context) {
        List<String> mData = new ArrayList<>();
        if (existFile(context.getFilesDir().toString() + "/strings.xml")) {
            for (String line : Objects.requireNonNull(readFile(context.getFilesDir().toString() + "/strings.xml")).split("\\r?\\n")) {
                if (line.contains("<string name=") && line.endsWith("</string>") && !line.contains("translatable=\"false")) {
                    mData.add(line);
                }
            }
        }
        return "<resources xmlns:tools=\"http://schemas.android.com/tools\" tools:ignore=\"MissingTranslation\">\n<!--Created by The Translator-->\n\n" +
                mData.toString().replace("[","").replace("]","").replace(",","\n") + "\n</resources>";
    }

    public static void restartApp(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }

    public static String getPath(File file) {
        String path = file.getAbsolutePath();
        if (path.startsWith("/document/raw:")) {
            path = path.replace("/document/raw:", "");
        } else if (path.startsWith("/document/primary:")) {
            path = (Environment.getExternalStorageDirectory() + ("/") + path.replace("/document/primary:", ""));
        } else if (path.startsWith("/document/")) {
            path = path.replace("/document/", "/storage/").replace(":", "/");
        }
        if (path.startsWith("/storage_root/storage/emulated/0")) {
            path = path.replace("/storage_root/storage/emulated/0", "/storage/emulated/0");
        } else if (path.startsWith("/storage_root")) {
            path = path.replace("storage_root", "storage/emulated/0");
        }
        if (path.startsWith("/external")) {
            path = path.replace("external", "storage/emulated/0");
        } if (path.startsWith("/root/")) {
            path = path.replace("/root", "");
        }
        if (path.contains("file%3A%2F%2F%2F")) {
            path = path.replace("file%3A%2F%2F%2F", "").replace("%2F", "/");
        }
        return path;
    }

    public static boolean isDocumentsUI(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        return (cm.getActiveNetworkInfo() != null) && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    /**
     * Taken and used almost as such from the following stackoverflow discussion
     * https://stackoverflow.com/questions/3571223/how-do-i-get-the-file-extension-of-a-file-in-java
     */
    public static String getExtension(String string) {
        return android.webkit.MimeTypeMap.getFileExtensionFromUrl(string);
    }

    /*
     * Taken and used almost as such from the following stackoverflow discussion
     * Ref: https://stackoverflow.com/questions/7203668/how-permission-can-be-checked-at-runtime-without-throwing-securityexception
     */
    public static boolean isStorageWritePermissionDenied(Context context) {
        String permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int res = context.checkCallingOrSelfPermission(permission);
        return (res != PackageManager.PERMISSION_GRANTED);
    }

    public static boolean getBoolean(String name, boolean defaults, Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(name, defaults);
    }

    public static void saveBoolean(String name, boolean value, Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(name, value).apply();
    }

}