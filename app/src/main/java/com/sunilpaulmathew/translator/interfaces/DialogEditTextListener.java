/*
 * Copyright (C) 2021-2022 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of The Translator, An application to help translate android apps.
 *
 */

package com.sunilpaulmathew.translator.interfaces;

import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sunilpaulmathew.translator.R;

import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 15, 2021
 */
public interface DialogEditTextListener {

    static MaterialAlertDialogBuilder dialogEditText(String text, String action, final DialogInterface.OnClickListener negativeListener,
                                                             final DialogEditTextListener onDialogEditTextListener, Context context) {
        LinearLayout layout = new LinearLayout(context);
        int padding = 75;
        layout.setPadding(padding, padding, padding, padding);

        final AppCompatEditText editText = new AppCompatEditText(context);
        editText.setGravity(Gravity.FILL_HORIZONTAL);
        editText.requestFocus();
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if (text != null) {
            editText.append(text);
        }

        layout.addView(editText);

        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(context).setView(layout);
        if (negativeListener != null) {
            dialog.setNegativeButton(context.getString(R.string.cancel), negativeListener);
        }
        if (onDialogEditTextListener != null) {
            dialog.setPositiveButton(action, (dialog1, which) -> onDialogEditTextListener.onClick(Objects.requireNonNull(editText.getText()).toString()));
            dialog.setOnDismissListener(dialog1 -> {
                if (negativeListener != null) {
                    negativeListener.onClick(dialog1, 0);
                }
            });
        }
        return dialog;
    }

    void onClick(String toString);

}