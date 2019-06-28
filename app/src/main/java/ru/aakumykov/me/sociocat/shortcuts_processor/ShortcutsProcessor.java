package ru.aakumykov.me.sociocat.shortcuts_processor;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_edit.CardEdit_View;
import ru.aakumykov.me.sociocat.card_show.CardShow_View;
import ru.aakumykov.me.sociocat.models.Card;

public class ShortcutsProcessor extends BaseView {

    @Override public void onUserLogin() {}
    @Override public void onUserLogout() {}

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Constants.CODE_CREATE_CARD) {

            try {
                processCardCreationResult(resultCode, data);
            } catch (Exception e) {
                showErrorMsg(R.string.SHORTCUT_PROCESSOR_error_creating_card, e.getMessage());
                e.printStackTrace();
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // Внутренние методы
    private void processInputIntent(@Nullable Intent data) throws Exception {
        if (null == data)
            return;

        Card card = new Card();

        String action = data.getAction() + "";

        switch (action) {
            case Constants.SHORTCUT_CREATE_TEXT_CARD:
                card.setType(Constants.TEXT_CARD);
                break;

            case Constants.SHORTCUT_CREATE_IMAGE_CARD:
                card.setType(Constants.IMAGE_CARD);
                break;

            case Constants.SHORTCUT_CREATE_AUDIO_CARD:
                card.setType(Constants.AUDIO_CARD);
                break;

            case Constants.SHORTCUT_CREATE_VIDEO_CARD:
                card.setType(Constants.VIDEO_CARD);
                break;

            default:
                throw new IllegalArgumentException("Unknown action '"+action+"'");
        }

        Intent intent = new Intent(this, CardEdit_View.class);
        intent.setAction(Constants.ACTION_CREATE);
        intent.putExtra(Constants.CARD, card);
        startActivityForResult(intent, Constants.CODE_CREATE_CARD);
    }

    private void processCardCreationResult(int resultCode, @Nullable Intent data) throws Exception {
        switch (resultCode) {
            case RESULT_OK:
                Card card = data.getParcelableExtra(Constants.CARD);
                Intent intent = new Intent(this, CardShow_View.class);
                intent.putExtra(Constants.CARD, card);
                startActivity(intent);
                break;
            case RESULT_CANCELED:
                showToast(R.string.SHORTCUT_PROCESSOR_card_creation_cencelled);
                break;
            default:
                break;
        }
    }
}
