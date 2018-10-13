package ru.aakumykov.me.mvp.template_mvvm;

import android.arch.lifecycle.MutableLiveData;

import ru.aakumykov.me.mvp.models.Card;

public class TemplateViewModel extends android.arch.lifecycle.ViewModel
        implements Interfaces.ViewModel, Interfaces.ModelCallbacks {

    private final static String TAG = "TemplateViewModel";
    private MutableLiveData<Card> mutableLiveData = new MutableLiveData<>();


    @Override
    public MutableLiveData<Card> getLiveData() {
        return null;
    }

    @Override
    public void onLoadSuccess(Card card) {

    }

    @Override
    public void onLoadError(String message) {

    }
}