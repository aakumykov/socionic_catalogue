package ru.aakumykov.me.sociocat.reset_password_step1.view_model;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import ru.aakumykov.me.sociocat.template_of_page.view_model.Page_ViewModel;

public class ResetPasswordStep1_ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    @NonNull @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ResetPasswordStep1_ViewModel();
    }
}
