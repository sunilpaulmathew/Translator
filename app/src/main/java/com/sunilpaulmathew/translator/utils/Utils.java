/*
 * Copyright (C) 2021-2022 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of The Translator, An application to help translate android apps.
 *
 */

package com.sunilpaulmathew.translator.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.util.TypedValue;

import com.sunilpaulmathew.translator.BuildConfig;
import com.sunilpaulmathew.translator.MainActivity;
import com.sunilpaulmathew.translator.R;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import in.sunilpaulmathew.sCommon.Utils.sPackageUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on June 30, 2020
 */
public class Utils {

    public static boolean isDonated(Context context) {
        return BuildConfig.DEBUG || sPackageUtils.isPackageInstalled("com.smartpack.donate", context);
    }

    public static boolean isPlayStoreAvailable(Context context) {
        return sPackageUtils.isPackageInstalled("com.android.vending", context);
    }

    /*
     * The following code is partly taken from https://github.com/Grarak/KernelAdiutor
     * Ref: https://github.com/Grarak/KernelAdiutor/blob/master/app/src/main/java/com/grarak/kerneladiutor/utils/ViewUtils.java
     */
    public static int getThemeAccentColor(Context context) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorAccent, value, true);
        return value.data;
    }

    public static void download(String url, String dest) {
        try (InputStream input = new URL(url).openStream();
             OutputStream output = new FileOutputStream(dest)) {
            byte[] data = new byte[4096];
            int count;
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }
        } catch (Exception ignored) {
        }
    }

    public static void restartApp(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }

    public static CharSequence fromHtml(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(text);
        }
    }

}