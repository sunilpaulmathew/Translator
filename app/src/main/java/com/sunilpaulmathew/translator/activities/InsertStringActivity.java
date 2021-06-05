/*
 * Copyright (C) 2021-2022 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of The Translator, An application to help translate android apps.
 *
 */

package com.sunilpaulmathew.translator.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.translator.R;
import com.sunilpaulmathew.translator.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on June 05, 2021
 */
public class InsertStringActivity extends AppCompatActivity {

    private AppCompatEditText mText;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_string);

        mText = findViewById(R.id.text);
        MaterialTextView mCancel = findViewById(R.id.cancel_button);
        MaterialTextView mInsert = findViewById(R.id.insert_button);

        mText.setTextColor(Utils.isDarkTheme(this) ? getResources().getColor(R.color.white) : getResources()
                .getColor(R.color.black));

        mInsert.setOnClickListener(v -> {
            if (mText.getText() == null || mText.getText().toString().isEmpty()) {
                return;
            }
            if (getStrings().size() > 0) {
                Utils.create(getStrings().toString().replace("[","").replace("]","")
                        .replace(",","\n"), getFilesDir().toString() + "/strings.xml");
                Utils.restartApp(this);
            } else {
                Utils.showSnackbar(findViewById(android.R.id.content), getString(R.string.insert_invalid_string));
            }
        });

        mCancel.setOnClickListener(v -> onBackPressed());
    }

    private List<String> getStrings() {
        List<String> mData = new ArrayList<>();
        for (String line : Objects.requireNonNull(mText.getText()).toString().split("\\r?\\n")) {
            if (line.contains("<string name=") && line.endsWith("</string>") && !line.contains("translatable=\"false")) {
                mData.add(line);
            }
        }
        return mData;
    }

    @Override
    public void onBackPressed() {
        if (mText.getText() != null && !mText.getText().toString().isEmpty()) {
            new MaterialAlertDialogBuilder(this)
                    .setMessage(getString(R.string.discard_message))
                    .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                    })
                    .setPositiveButton(getString(R.string.discard), (dialogInterface, i) -> finish()).show();
        } else {
            finish();
        }
    }

}