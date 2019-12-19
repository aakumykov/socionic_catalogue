package ru.aakumykov.me.sociocat.card_show;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import ru.aakumykov.me.sociocat.card_show.CardShow_ViewModel;

public class CardShow_ViewModel_Factory extends ViewModelProvider.NewInstanceFactory {
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new CardShow_ViewModel();
    }
}
