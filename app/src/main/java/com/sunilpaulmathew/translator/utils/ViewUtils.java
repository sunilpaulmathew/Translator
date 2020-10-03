package com.sunilpaulmathew.translator.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;

import com.sunilpaulmathew.translator.R;

import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on June 30, 2020
 * Based on the original implementation on Kernel Adiutor by
 * Willi Ye <williye97@gmail.com>
 */

public class ViewUtils {

    public static int getThemeAccentColor(Context context) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorAccent, value, true);
        return value.data;
    }

    public interface OnDialogEditTextListener {
        void onClick(String text);
    }

    public static AlertDialog.Builder dialogEditText(String text, String action, final DialogInterface.OnClickListener negativeListener,
                                                     final OnDialogEditTextListener onDialogEditTextListener,
                                                     Context context) {
        return dialogEditText(text, action, negativeListener, onDialogEditTextListener, -1, context);
    }

    private static AlertDialog.Builder dialogEditText(String text, String action, final DialogInterface.OnClickListener negativeListener,
                                                      final OnDialogEditTextListener onDialogEditTextListener, int inputType,
                                                      Context context) {
        LinearLayout layout = new LinearLayout(context);
        int padding = 75;
        layout.setPadding(padding, padding, padding, padding);

        final AppCompatEditText editText = new AppCompatEditText(context);
        editText.setGravity(Gravity.FILL_HORIZONTAL);
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if (text != null) {
            editText.append(text);
        }
        editText.setTextColor(getThemeAccentColor(context));
        if (inputType >= 0) {
            editText.setInputType(inputType);
        }

        layout.addView(editText);

        AlertDialog.Builder dialog = new AlertDialog.Builder(context).setView(layout);
        if (negativeListener != null) {
            dialog.setNegativeButton(context.getString(R.string.cancel), negativeListener);
        }
        if (onDialogEditTextListener != null) {
            dialog.setPositiveButton(action, (dialog1, which) -> {
                if (Utils.checkIllegalCharacters(Objects.requireNonNull(editText.getText()).toString())) {
                    return;
                }
                onDialogEditTextListener.onClick(Objects.requireNonNull(editText.getText()).toString());
            });
            dialog.setOnDismissListener(dialog1 -> {
                if (negativeListener != null) {
                    negativeListener.onClick(dialog1, 0);
                }
            });
        }
        return dialog;
    }

}