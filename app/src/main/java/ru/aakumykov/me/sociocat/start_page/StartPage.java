package ru.aakumykov.me.sociocat.start_page;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.b_cards_list.CardsList_View;
import ru.aakumykov.me.sociocat.card_edit.CardEdit_View;
import ru.aakumykov.me.sociocat.card_show.CardShow_View;
import ru.aakumykov.me.sociocat.constants.Constants;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.z_base_view.BaseView;

public class StartPage extends BaseView {

    private static final String TAG = "StartPage";
    private boolean dryRun = true;

    @Override public void onUserLogin() {}
    @Override public void onUserLogout() {}

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            processInputIntent(getIntent());
        }
        catch (Exception e) {
            showErrorMsg(R.string.SHORTCUT_PROCESSOR_error_processing_shortcut, e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dryRun)
            dryRun = false;
        else
            finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case Constants.CODE_CREATE_CARD:
                processCardCreationResult(resultCode, data);
                break;
            case Constants.CODE_SHOW_CARD:
                go2cardsGrid();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    
    // Внутренние методы
    private void processInputIntent(@Nullable Intent data) throws Exception {
        if (null == data) {
            go2cardsGrid();
            return;
        }

        Bundle bundle = data.getExtras();
        if (null != bundle && bundle.containsKey("isPushNotification")) {
            processPushNotification(bundle);
            return;
        }


        String cardType;

        switch (data.getAction() + "") {
            case Constants.SHORTCUT_CREATE_TEXT_CARD:
                cardType = Constants.TEXT_CARD;
                break;

            case Constants.SHORTCUT_CREATE_IMAGE_CARD:
                cardType = Constants.IMAGE_CARD;
                break;

            case Constants.SHORTCUT_CREATE_AUDIO_CARD:
                cardType = Constants.AUDIO_CARD;
                break;

            case Constants.SHORTCUT_CREATE_VIDEO_CARD:
                cardType = Constants.VIDEO_CARD;
                break;

            default:
                go2cardsGrid();
                return;

        }

        Intent intent = new Intent(this, CardEdit_View.class);
        intent.setAction(Constants.ACTION_CREATE);
        intent.putExtra(Constants.CARD_TYPE, cardType);
        startActivityForResult(intent, Constants.CODE_CREATE_CARD);
    }

    private void processPushNotification(@NonNull Bundle bundle) {
        if (bundle.containsKey("isCardNotification")) {
            processCardNotification(bundle);
            return;
        }
    }

    private void processCardNotification(@NonNull Bundle bundle) {
        String cardKey = bundle.getString("key");
        if (!TextUtils.isEmpty(cardKey)) {
            Intent intent = new Intent(this, CardShow_View.class);
            intent.putExtra(Constants.CARD_KEY, cardKey);
            startActivityForResult(intent, Constants.CODE_SHOW_CARD);
        }
        else {
            Log.e(TAG, "There is no card key in bundle");
            go2cardsGrid();
        }
    }

    private void processCardCreationResult(int resultCode, @Nullable Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                go2showCard(data);
                break;

            case RESULT_CANCELED:
                showToast(R.string.SHORTCUT_PROCESSOR_card_creation_cencelled);
                go2cardsGrid();
                break;

            default:
                showToast(R.string.SHORTCUT_PROCESSOR_unknown_result_code);
                go2cardsGrid();
                break;
        }
    }

    private void go2showCard(@Nullable Intent data) {
        if (null == data) {
            showToast(R.string.SHORTCUT_PROCESSOR_data_error);
            Log.e(TAG, "Intent is null");
            go2cardsGrid();
            return;
        }

        Card card = data.getParcelableExtra(Constants.CARD);
        Intent cardShowIntent = new Intent(this, CardShow_View.class);
        cardShowIntent.putExtra(Constants.CARD, card);
        startActivity(cardShowIntent);
    }

    private void go2cardsGrid() {
        Intent cardsGridIntent = new Intent(this, CardsList_View.class);
        startActivity(cardsGridIntent);
    }
}
