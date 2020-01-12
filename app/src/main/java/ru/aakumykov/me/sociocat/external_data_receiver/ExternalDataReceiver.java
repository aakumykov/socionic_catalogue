package ru.aakumykov.me.sociocat.external_data_receiver;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_edit.CardEdit_View;
import ru.aakumykov.me.sociocat.card_show.CardShow_View;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.utils.ImageExtractor;
import ru.aakumykov.me.sociocat.utils.ImageInfo;
import ru.aakumykov.me.sociocat.utils.ImageType;
import ru.aakumykov.me.sociocat.utils.ImageUtils;
import ru.aakumykov.me.sociocat.utils.MVPUtils.MVPUtils;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class ExternalDataReceiver extends BaseView {

    private static final String TAG = "ExternalDataReceiver";
    private boolean mWorkDone = false;


    // Системные методы
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        finishIfDone();

        setContentView(R.layout.data_reciever_activity);
        setPageTitle(R.string.EXTERNAL_DATA_RECIEVER_page_title);


        Intent inputIntent = getIntent();

        if (null == inputIntent) {
            showErrorMsg(R.string.EXTERNAL_DATA_RECIEVER_input_data_error, "Intent is null");
            return;
        }
        detectDataType(inputIntent);
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
    private void finishIfDone() {
        if (mWorkDone)
            finish();
    }

    private void detectDataType(Intent inputIntent) {
        Constants.CardType cardType;
        Object data;

        ImageExtractor.extractImageFromIntent(this, inputIntent, new ImageExtractor.ImageExtractionCallbacks() {
            @Override
            public void onImageExtractionSuccess(Bitmap bitmap, ImageType imageType, Uri imageURI) {
                go2createCard(Constants.CardType.IMAGE_CARD, imageURI);
                return;
            }

            @Override
            public void onImageExtractionError(String errorMsg) {
                showErrorMsg(R.string.EXTERNAL_DATA_RECIEVER_error_processing_image, errorMsg);
                return;
            }
        });


        // Видео/текст
        String text = MVPUtils.getTextFromIntent(inputIntent);

        if (null == text) {
            showErrorMsg(R.string.EXTERNAL_DATA_RECIEVER_unsupported_input_data, "There is no text content in Intent");
            return;
        }

        String videoCode = MVPUtils.extractYoutubeVideoCode(text);

        if (null != videoCode) {
            cardType = Constants.CardType.VIDEO_CARD;
            data = videoCode;
        }
        else {
            cardType = Constants.CardType.TEXT_CARD;
            data = text;
        }


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

    private void go2createCard(Constants.CardType cardType, Object data) {

        Intent intent = new Intent(this, CardEdit_View.class);
        intent.setAction(Constants.ACTION_CREATE);

        switch (cardType) {
            case TEXT_CARD:
                intent.putExtra(Constants.CARD_TYPE, Constants.TEXT_CARD);
                intent.putExtra(Constants.EXTERNAL_DATA, (String) data);
                break;

            case IMAGE_CARD:
                intent.putExtra(Constants.CARD_TYPE, Constants.IMAGE_CARD);
                intent.putExtra(Constants.EXTERNAL_DATA, (String) data);
                break;

            case AUDIO_CARD:
                intent.putExtra(Constants.CARD_TYPE, Constants.AUDIO_CARD);
                intent.putExtra(Constants.EXTERNAL_DATA, (String) data);
                break;

            case VIDEO_CARD:
                intent.putExtra(Constants.CARD_TYPE, Constants.VIDEO_CARD);
                intent.putExtra(Constants.EXTERNAL_DATA, (String) data);
                break;

            default:
                showErrorMsg(R.string.EXTERNAL_DATA_RECIEVER_unknown_card_type, String.valueOf(cardType));
                return;
        }

        startActivityForResult(intent, Constants.CODE_CREATE_CARD);
    }
}
