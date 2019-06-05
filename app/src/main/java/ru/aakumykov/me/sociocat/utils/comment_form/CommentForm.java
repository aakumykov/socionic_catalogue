package ru.aakumykov.me.sociocat.utils.comment_form;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.Config;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class CommentForm implements
        iCommentForm,
        View.OnClickListener
{
    private final static String TAG = "CommentForm";

    private Context context;
    private ButtonListeners buttonListeners;

    private View commentForm;

    private TextView errorView;

    private View quoteContainer;
    private TextView quoteTextView;
    private View clearQuoteWidget;

    private View commentContainer;
    private EditText commentTextInput;
    private View sendCommentWidget;


    public CommentForm(Context context) {
        this.context = context;
    }


    // Интерфесные методы
    @Override
    public void attachTo(ViewGroup container) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        container.removeAllViews();
        View layout = layoutInflater.inflate(R.layout.comment_form, container, true);

        commentForm = layout.findViewById(R.id.commentForm);
        errorView = layout.findViewById(R.id.errorView);
        quoteContainer = layout.findViewById(R.id.quoteContainer);
        quoteTextView = layout.findViewById(R.id.quoteTextView);
        clearQuoteWidget = layout.findViewById(R.id.clearQuoteWidget);
        commentContainer = layout.findViewById(R.id.commentContainer);
        commentTextInput = layout.findViewById(R.id.commentTextInput);
        sendCommentWidget = layout.findViewById(R.id.sendCommentWidget);

        sendCommentWidget.setOnClickListener(this);
        clearQuoteWidget.setOnClickListener(this);
    }

    @Override
    public void addButtonListeners(ButtonListeners listeners) {
        this.buttonListeners = listeners;
    }

    @Override
    public void setQuote(String text) {
        quoteTextView.setText(text);
        MyUtils.show(quoteContainer);
    }

    @Override
    public void setText(String text) {
        commentTextInput.setText(text);
    }

    @Override
    public String getText() {
        return commentTextInput.getText().toString();
    }

    @Override
    public void clear() {
        commentTextInput.setText("");
    }

    @Override
    public void show() {
        enable();
        MyUtils.show(commentContainer);
        MyUtils.show(commentForm);
        MyUtils.showKeyboardOnFocus(context, commentTextInput);
    }

    @Override
    public void hide() {
        quoteTextView.setText("");

        /* Если раскомментировать следующую строку (то есть, очищать поле ввода),
           клавиатура выскакивает при скрытии формы. */
        // commentTextInput.setText("");

        hideKeyboard();

        MyUtils.hide(quoteContainer);
        MyUtils.hide(commentContainer);
        MyUtils.hide(commentForm);
    }

    @Override
    public void enable() {
        MyUtils.enable(clearQuoteWidget);
        MyUtils.enable(commentTextInput);
        MyUtils.enable(sendCommentWidget);
    }

    @Override
    public void disable() {
        MyUtils.disable(clearQuoteWidget);
        MyUtils.disable(commentTextInput);
        MyUtils.disable(sendCommentWidget);
    }

    @Override
    public boolean isVisible() {
        return View.VISIBLE == commentForm.getVisibility();
    }

    @Override
    public void showError(int messageId, @Nullable String consoleMessage) {
//        hideKeyboard();

        String msg = (Config.DEBUG_MODE) ? consoleMessage : context.getResources().getString(messageId);

        errorView.setText(msg);
        MyUtils.show(errorView);

        if (null != consoleMessage)
            Log.e(TAG, consoleMessage);
    }

    @Override
    public void hideError() {
        MyUtils.hide(errorView);
    }


    // Системные методы
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.sendCommentWidget:
                sendComment();
                break;

            case R.id.clearQuoteWidget:
                clearQuote();
                break;

            default:
                break;
        }
    }


    // Внутренние методы
    private void sendComment() {
        hideError();
        buttonListeners.onSendCommentClicked(getText());
    }

    private void clearQuote() {
        quoteTextView.setText("");
        MyUtils.hide(quoteContainer);
        buttonListeners.onClearQuoteClicked();
    }

    private void hideKeyboard() {
        commentTextInput.clearFocus();
        MyUtils.hideKeyboard(context, commentTextInput);
    }
}
