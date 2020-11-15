package ru.aakumykov.me.sociocat.tag_edit;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.base_view.BaseView;
import ru.aakumykov.me.sociocat.basic_view_states.ProgressViewState;
import ru.aakumykov.me.sociocat.basic_view_states.iBasicViewState;
import ru.aakumykov.me.sociocat.models.Tag;
import ru.aakumykov.me.sociocat.tag_edit.view_model.TagEdit_PageController;
import ru.aakumykov.me.sociocat.tag_edit.view_model.TagEdit_ViewModelFactory;
import ru.aakumykov.me.sociocat.utils.MyUtils;
import ru.aakumykov.me.sociocat.utils.SimpleYesNoDialog;

public class TagEdit_View extends BaseView
        implements iTagEdit_View, LifecycleOwner
{
    @BindView(R.id.tagNameInput) TextInputEditText tagNameInput;
    @BindView(R.id.saveButton) Button saveButton;
    @BindView(R.id.cancelButton) Button cancelButton;

    private TagEdit_PageController mPageController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_edit_activity);
        ButterKnife.bind(this);

        activateUpButton();
        setPageTitle(R.string.TAG_EDIT_page_title);

        mPageController = new ViewModelProvider(this, new TagEdit_ViewModelFactory())
                .get(TagEdit_PageController.class);

        getLifecycle().addObserver(mPageController);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPageController.bindView(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPageController.unbindView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public void onBackPressed() {
        if (!mPageController.onBackPressed())
            super.onBackPressed();
    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }

    @Override
    public void setViewState(iBasicViewState viewState) {
        if (viewState instanceof  TagEditViewState)
            setTagEditViewState((TagEditViewState) viewState);
        else
            super.setViewState(viewState);
    }

    @Override
    protected void setProgressViewState(ProgressViewState progressViewState) {
        super.setProgressViewState(progressViewState);
        disableForm();
    }

    @Override
    protected void setNeutralViewState() {
        super.setNeutralViewState();
        enableForm();
    }


    @OnClick(R.id.saveButton)
    void onSaveButtonClicked() {
        mPageController.onSaveClicked();
    }

    @OnClick(R.id.cancelButton)
    void onCancelButtonClicked() {
        mPageController.onCancelClicked();
    }


    private void setTagEditViewState(TagEditViewState tagEditViewState) {
        setNeutralViewState();

        Tag tag = tagEditViewState.getTag();

        tagNameInput.setText(tag.getName());
        setPageTitle(R.string.TAG_EDIT_page_title_extended, tag.getName());
    }

    private void enableForm() {
        MyUtils.enable(tagNameInput);
        MyUtils.enable(saveButton);
    }

    private void disableForm() {
        MyUtils.disable(tagNameInput);
        MyUtils.disable(saveButton);
    }


    @Override
    public String getTagName() {
        return tagNameInput.getText().toString();
    }

    @Override
    public void setTagName(String name) {
        tagNameInput.setText(name);
    }

    @Override
    public void confirmCancel() {
        SimpleYesNoDialog.show(
                this,
                R.string.TAG_EDIT_cancel_editing_tag,
                null,
                new SimpleYesNoDialog.AbstractCallbacks() {
                    @Override
                    public void onYes() {
                        mPageController.onCancelConfirmed();
                    }
                }
        );
    }

    @Override
    public void showTagError(int messageId) {
        showTagError(MyUtils.getString(this, messageId));
    }

    @Override
    public void showTagError(String msg) {
        tagNameInput.setError(msg);
    }

    @Override
    public void finishWithSuccess(@NonNull Tag tag) {
        Intent intent = new Intent();
        intent.putExtra(Constants.TAG_NAME, tag.getName());
        intent.setAction(Intent.ACTION_EDIT);

        setResult(RESULT_OK, intent);
        finish();
    }
}
