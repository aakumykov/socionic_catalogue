package ru.aakumykov.me.sociocat.card_type_chooser;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_edit.CardEdit_View;
import ru.aakumykov.me.sociocat.card_show.CardShow_View;
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
        switch (requestCode) {
            case Constants.CODE_CREATE_CARD:
                processCardCreationResult(resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
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
        card.setType(Constants.AUDIO_CARD);
        startEditActivity();
    }


    private void startEditActivity() {
        Intent intent = new Intent(this, CardEdit_View.class);
        intent.setAction(Constants.ACTION_CREATE);
        intent.putExtra(Constants.CARD, card);
        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        startActivity(intent);
    }

    private void processCardCreationResult(int resultCode, @Nullable Intent data) {
        switch (resultCode) {

            case RESULT_OK:
                if (null == data) {
                    showErrorMsg(R.string.CARD_TYPE_CHOOSER_error_creating_card, "Intent is NULL");
                    return;
                } else {
                    Card card = data.getParcelableExtra(Constants.CARD);
                    if (null != card) {
                        Intent intent = new Intent(this, CardShow_View.class);
                        intent.putExtra(Constants.CARD_KEY, card.getKey());
                        startActivity(intent);
                    } else {
                        showErrorMsg(R.string.CARD_TYPE_CHOOSER_error_creating_card, "Card is NULL");
                    }
                }
                break;

            case RESULT_CANCELED:
                showToast(R.string.CARD_TYPE_CHOOSER_card_creation_cancelled);
                return;

            default:
                showErrorMsg(R.string.unknown_rsult_code, "Unknown result code: "+resultCode);
        }
    }
}
