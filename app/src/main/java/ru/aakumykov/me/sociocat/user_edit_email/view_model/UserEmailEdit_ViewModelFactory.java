package ru.aakumykov.me.sociocat.user_edit_email.view_model;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class UserEmailEdit_ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    @NonNull @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new UserEmailEdit_ViewModel();
    }
}
