package ru.aakumykov.me.sociocat.users.view_model;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class Users_ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    @NonNull @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new Users_ViewModel();
    }
}
