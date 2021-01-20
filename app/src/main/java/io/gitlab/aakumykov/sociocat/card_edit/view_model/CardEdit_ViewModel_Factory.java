package io.gitlab.aakumykov.sociocat.card_edit.view_model;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class CardEdit_ViewModel_Factory extends ViewModelProvider.NewInstanceFactory {
    @NonNull @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new CardEdit_ViewModel();
    }
}
