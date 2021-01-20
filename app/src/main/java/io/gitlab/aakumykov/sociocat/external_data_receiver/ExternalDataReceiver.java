package io.gitlab.aakumykov.sociocat.external_data_receiver;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import io.gitlab.aakumykov.sociocat.R;
import io.gitlab.aakumykov.sociocat.card_edit.CardEdit_View;
import io.gitlab.aakumykov.sociocat.card_show.CardShow_View;
import io.gitlab.aakumykov.sociocat.constants.Constants;
import io.gitlab.aakumykov.sociocat.eCardType;
import io.gitlab.aakumykov.sociocat.models.Card;
import io.gitlab.aakumykov.sociocat.utils.ContentType;
import io.gitlab.aakumykov.sociocat.utils.IntentUtils;
import io.gitlab.aakumykov.sociocat.z_base_view.BaseView;

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

        processInputIntent(getIntent());
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
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        String action = (null != intent) ? intent.getAction() : null;
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



    // Внутренние методы
    private void processInputIntent(@Nullable Intent intent) {
        if (null == intent) {
            showLongToast(R.string.EXTERNAL_DATA_RECIEVER_no_input_data);
            return;
        }

        String action = (null != intent) ? intent.getAction() : null;

        ContentType intentDataType = IntentUtils.detectContentType(intent);

        eCardType cardType;

        switch (intentDataType) {
            case TEXT:
                cardType = eCardType.TEXT_CARD;
                break;

            case IMAGE:
                cardType = eCardType.IMAGE_CARD;
                break;

            case YOUTUBE_VIDEO:
                cardType = eCardType.VIDEO_CARD;
                break;

            case OTHER:
                showLongToast(R.string.EXTERNAL_DATA_RECIEVER_unsupported_data_type);
                finish();
                return;

            default:
                showLongToast(R.string.EXTERNAL_DATA_RECIEVER_unknown_data_type);
                finish();
                return;
        }

        go2createCard(cardType, intent);
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

    private void go2createCard(eCardType cardType, Intent inputIntent) {

        Intent intent = new Intent(this, CardEdit_View.class);
        intent.setAction(Constants.ACTION_CREATE);
        intent.putExtra(Intent.EXTRA_INTENT, inputIntent);

        switch (cardType) {
            case TEXT_CARD:
                intent.putExtra(Constants.CARD_TYPE, Constants.TEXT_CARD);
                break;

            case IMAGE_CARD:
                intent.putExtra(Constants.CARD_TYPE, Constants.IMAGE_CARD);
                break;

            case AUDIO_CARD:
                intent.putExtra(Constants.CARD_TYPE, Constants.AUDIO_CARD);
                break;

            case VIDEO_CARD:
                intent.putExtra(Constants.CARD_TYPE, Constants.VIDEO_CARD);
                break;

            default:
                showErrorMsg(R.string.EXTERNAL_DATA_RECIEVER_unknown_card_type, String.valueOf(cardType));
                return;
        }

        startActivityForResult(intent, Constants.CODE_CREATE_CARD);
    }

    private void finishIfDone() {
        if (mWorkDone)
            finish();
    }
}
