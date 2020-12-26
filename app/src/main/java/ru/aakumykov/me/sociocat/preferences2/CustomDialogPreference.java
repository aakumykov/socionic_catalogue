package ru.aakumykov.me.sociocat.preferences2;

import android.content.Context;

import androidx.preference.DialogPreference;

public class CustomDialogPreference extends DialogPreference {

    public CustomDialogPreference(Context context) {
        super(context);
    }

    @Override
    public void setDialogLayoutResource(int dialogLayoutResId) {
        super.setDialogLayoutResource(dialogLayoutResId);
    }
}
