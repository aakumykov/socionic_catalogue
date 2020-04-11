package ru.aakumykov.me.sociocat.template_of_list.view_model;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class TemplateOfList_ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    @NonNull @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new TemplateOfList_ViewModel();
    }
}
