package ru.aakumykov.me.sociocat.external_data_receiver;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;

import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_edit.CardEdit_View;
import ru.aakumykov.me.sociocat.card_edit3.CardEdit3_View;
import ru.aakumykov.me.sociocat.card_show.CardShow_View;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.utils.MVPUtils.MVPUtils;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class ExternalDataReceiver extends BaseView {

    public static final String TAG = "ExternalDataReceiver";
    private Card currentCard;

    // Системные методы
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_reciever_activity);

        setPageTitle(R.string.EXTERNAL_DATA_RECIEVER_page_title);

        currentCard = new Card();

        try {
            processRecievedData(getIntent());
        } catch (Exception e) {
            showErrorMsg(e.getMessage());
            e.printStackTrace();
        }
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


    // Внутренние методы
    private void processRecievedData(Intent data) throws Exception {

        if (null == data)
            throw new IllegalArgumentException("Intent is NULL");

        String inputDataMode = MVPUtils.detectInputDataMode(data);

        switch (inputDataMode) {

            case Constants.TYPE_TEXT:
                currentCard.setType(Constants.TEXT_CARD);
                procesIncomingText(data);
                break;

            case Constants.TYPE_IMAGE_LINK:
                currentCard.setType(Constants.IMAGE_CARD);
                //processLinkToImage(intent);
                break;

            case Constants.TYPE_IMAGE_DATA:
                currentCard.setType(Constants.IMAGE_CARD);
                //processIncomingImage(intent);
                break;

            case Constants.TYPE_YOUTUBE_VIDEO:
                currentCard.setType(Constants.VIDEO_CARD);
                //processYoutubeVideo(intent);
                break;

            default:
                String msg = getResources().getString(R.string.CARD_EDIT_unknown_data_mode);
                throw new Exception(msg);
        }

        Intent intent = new Intent(this, CardEdit3_View.class);
        intent.setAction(Constants.ACTION_CREATE);
        intent.putExtra(Constants.CARD, currentCard);
        startActivityForResult(intent, Constants.CODE_CREATE_CARD);
    }

    private void procesIncomingText(Intent intent) throws Exception {
        String text = intent.getStringExtra(Intent.EXTRA_TEXT);

        if (null == text) {
            throw new IllegalArgumentException("Intent.EXTRA_TEXT is null.");
        }

        String autoTitle = MyUtils.cutToLength(text, Constants.TITLE_MAX_LENGTH);

        currentCard.setTitle(autoTitle);
        currentCard.setQuote(text);
    }

    private void processCardCreationResult(int resultCode, @Nullable Intent data) {
        if (null == data) {
            throw  new IllegalArgumentException("Intent is null");
        }

        if (RESULT_CANCELED == resultCode) {
            finish();
            return;
        }

        Card card = data.getParcelableExtra(Constants.CARD);
        Intent intent = new Intent(this, CardShow_View.class);
        intent.putExtra(Constants.CARD, card);
        startActivity(intent);
    }
}
