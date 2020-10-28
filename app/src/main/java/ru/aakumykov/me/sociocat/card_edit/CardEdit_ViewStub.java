package ru.aakumykov.me.sociocat.card_edit;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import java.util.HashMap;

import ru.aakumykov.me.sociocat.base_view.BaseView_Stub;
import ru.aakumykov.me.sociocat.models.Card;

public class CardEdit_ViewStub extends BaseView_Stub implements iCardEdit.View {


    @Override
    public void setViewState(eCardEdit_ViewState viewState, @Nullable Object viewStateData1) {

    }

    @Override
    public void setViewState(eCardEdit_ViewState viewState, @Nullable Object viewStateData1, @Nullable Object viewStateData2) {

    }

    @Override
    public void displayCard(Card card, boolean omitImage) {

    }

    @Override
    public <T> void displayImage(T imageURI) {

    }

    @Override
    public void displayVideo(String videoCode, @Nullable Float timecode) {

    }

    @Override
    public void displayAudio(String audioCode, @Nullable Float timecode) {

    }

    @Override
    public void removeImage() {

    }

    @Override
    public boolean hasImage() {
        return false;
    }

    @Override
    public void removeMedia() {

    }

    @Override
    public String getCardTitle() {
        return null;
    }

    @Override
    public String getQuote() {
        return null;
    }

    @Override
    public String getQuoteSource() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public Float getTimecode() {
        return null;
    }

    @Override
    public HashMap<String, Boolean> getTags() {
        return null;
    }

    @Override
    public void convert2audio() {

    }

    @Override
    public void convert2video() {

    }

    @Override
    public void showTitleError(int msgId) {

    }

    @Override
    public void showQuoteError(int msgId) {

    }

    @Override
    public void showVideoError(int msgId) {

    }

    @Override
    public void showAudioError(int msgId) {

    }

    @Override
    public void showMediaError() {

    }

    @Override
    public void hideMediaError() {

    }

    @Override
    public void showDescriptionError(int msgId) {

    }

    @Override
    public void showImageError(int msgId) {

    }

    @Override
    public void hideImageError() {

    }

    @Override
    public void disableForm() {

    }

    @Override
    public void enableForm() {

    }

    @Override
    public void prepareForQuote(String title, String quote) {

    }

    @Override
    public void prepareForImage(Bitmap imageBitmap) {

    }

    @Override
    public void prepareForVideo(String videoCode, String timeCode) {

    }

    @Override
    public void prepareForAudio(@Nullable String audioCode, Float timeCode) {

    }

    @Override
    public void showImageThrobber() {

    }

    @Override
    public void hideImageThrobber() {

    }

    @Override
    public boolean isFormFilled() {
        return false;
    }

    @Override
    public void finishEdit(Card card) {

    }

    @Override
    public void addTag(String tag) {

    }

    @Override
    public float pauseMedia() {
        return 0;
    }

    @Override
    public void resumeMedia(float position) {

    }

    @Override
    public void pickImage() {

    }
}
