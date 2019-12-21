package ru.aakumykov.me.sociocat.cards_grid.view_model;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class CardsGrid_ViewModel_Factory extends ViewModelProvider.NewInstanceFactory {

    @NonNull @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new CardsGrid_ViewModel();
    }
}
