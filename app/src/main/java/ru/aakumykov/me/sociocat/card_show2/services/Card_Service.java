package ru.aakumykov.me.sociocat.card_show2.services;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.models.Card;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class Card_Service {

    public interface iCardLoadCallbacks {
        void onCardLoadSuccess(Card card);
        void onCardLoadFail(String errorMsg);
    }

    public void loadCard(String key, iCardLoadCallbacks callbacks) {

        Observable<Card> observable = Observable
                .fromCallable(new FetchCardCallable())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        Observer<Card> observer = new Observer<Card>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                callbacks.onCardLoadFail(e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onNext(Card card) {
                callbacks.onCardLoadSuccess(card);
            }
        };

        observable.subscribe(observer);
    }


    private Card composeCard() {
        Card card = new Card();

        card.setTitle("Открыты исходные тексты языка программирования Flow9");
        card.setQuote("Компания Area9 открыла исходные тексты функционального языка программирования Flow9, ориентированного на создание пользовательских интерфейсов.");
        card.setDescription("Язык развивается с 2010 года в качестве универсальной и многоплатформенной альтернативы Adobe Flash.");

        String imageURL = "https://mota.ru/upload/resize/2560/1600/upload/wallpapers/2019/05/04/11/22/62447/15569580945ccd4b8e07e7a8.45010140-0aa.jpg";
        card.setImageURL(imageURL);

        card.setAudioCode("CfihYWRWRTQ");
        card.setVideoCode("BgfcToAjfdc");

        card.setType(Constants.VIDEO_CARD);
//        list_item_card.setType("AUDIO_CARD");
//        list_item_card.setType("IMAGE_CARD");
//        list_item_card.setType("TEXT_CARD");

        return card;
    }


    private Card fetchCardFromServer() {
        Card card = composeCard();

        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return card;
    }

    class FetchCardCallable implements Callable<Card> {
        @Override
        public Card call() throws Exception {
            return fetchCardFromServer();
        }
    }
}
