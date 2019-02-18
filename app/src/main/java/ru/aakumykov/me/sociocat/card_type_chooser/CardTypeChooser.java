package ru.aakumykov.me.sociocat.card_type_chooser;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_edit3.CardEdit3_View;
import ru.aakumykov.me.sociocat.models.Card;

public class CardTypeChooser extends BaseView {

    private Card card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_type_chooser_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.CARD_TYPE_CHOOSER_page_title);
        activateUpButton();

        card = new Card();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onUserLogin() {

    }
    @Override
    public void onUserLogout() {

    }


    @OnClick(R.id.textChooser)
    void createTextCard() {
        card.setType(Constants.TEXT_CARD);
        startEditActivity();
    }

    @OnClick(R.id.imageChooser)
    void createImageCard() {
        card.setType(Constants.IMAGE_CARD);
        startEditActivity();
    }

    @OnClick(R.id.youtubeChooser)
    void createYoutubeCard() {
        card.setType(Constants.VIDEO_CARD);
        startEditActivity();
    }

    @OnClick(R.id.soundChooser)
    void createAudioCard() {
        showToast(R.string.not_implemented_yet);
    }


    private void startEditActivity() {
        Intent intent = new Intent(this, CardEdit3_View.class);
        intent.setAction(Constants.ACTION_CREATE);
        intent.putExtra(Constants.CARD, card);
        startActivityForResult(intent, Constants.CODE_CREATE_CARD);
    }
}
