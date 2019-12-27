package ru.aakumykov.me.sociocat.external_data_receiver;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.Config;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_edit.CardEdit_View;
import ru.aakumykov.me.sociocat.card_show.CardShow_View;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.utils.ImageInfo;
import ru.aakumykov.me.sociocat.utils.ImageSelector;
import ru.aakumykov.me.sociocat.utils.MVPUtils.MVPUtils;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class ExternalDataReceiver extends BaseView {

    private boolean mWorkDone = false;


    // Системные методы
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        finishIfDone();

        setContentView(R.layout.data_reciever_activity);
        setPageTitle(R.string.EXTERNAL_DATA_RECIEVER_page_title);

        try {
            processInputIntent(getIntent());
        }
        catch (Exception e) {
            showErrorMsg(R.string.EXTERNAL_DATA_RECIEVER_error_starting_work, e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode) {
            case Constants.CODE_CREATE_CARD:
                try {
                    processCardCreationResult(resultCode, data);
                } catch (Exception e) {
                    showErrorMsg(R.string.EXTERNAL_DATA_RECIEVER_error_creating_card, e.getMessage());
                    e.printStackTrace();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        finishIfDone();
    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    // Внутренние методы
    private void processInputIntent(@Nullable Intent inputIntent) throws Exception {

        if (null == inputIntent)
            throw new IllegalArgumentException("Input intent is NULL");

        Card card = new Card();

        // Картинка?
        ImageInfo imageInfo = ImageSelector.extractImageInfo(this, inputIntent);
        if (null != imageInfo) {
            card.setType(Constants.IMAGE_CARD);
            card.setLocalImageURI(imageInfo.getLocalURI());
            card.setImageType(imageInfo.getTypeString());
            goToEditCard(card);
            return;
        }

        // Видео, Текст?
        String text = MVPUtils.getTextFromIntent(inputIntent);

        if (null == text) {
            showErrorMsg(R.string.EXTERNAL_DATA_RECIEVER_unsupported_input_data, "There is no text content in Intent");
            return;
        }

        String videoCode = MVPUtils.extractYoutubeVideoCode(text);

        if (null != videoCode) {
            card.setType(Constants.VIDEO_CARD);
            card.setVideoCode(videoCode);
        }
        else {
            card.setType(Constants.TEXT_CARD);
            card.setQuote(text);
            card.setTitle(MyUtils.cutToLength(text, Config.TITLE_MAX_LENGTH));
        }

        goToEditCard(card);
    }

    private void processCardCreationResult(int resultCode, @Nullable Intent data) {
        mWorkDone = true;

        if (null == data)
            throw  new IllegalArgumentException("Intent is null");

        if (RESULT_OK == resultCode) {
            Card card = data.getParcelableExtra(Constants.CARD);
            Intent intent = new Intent(this, CardShow_View.class);
            intent.putExtra(Constants.CARD, card);
            startActivity(intent);
        }
        else {
            finish();
        }
    }

    private void finishIfDone() {
        if (mWorkDone)
            finish();
    }

    private void goToEditCard(Card card) {
        Intent intent = new Intent(this, CardEdit_View.class);
        intent.putExtra(Constants.CARD, card);
        startActivityForResult(intent, Constants.CODE_CREATE_CARD);
    }
}
