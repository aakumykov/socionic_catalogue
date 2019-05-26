package ru.aakumykov.me.sociocat.utils.comment_form;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import ru.aakumykov.me.sociocat.Config;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class CommentForm implements
        iCommentForm,
        View.OnClickListener
{
    private final static String TAG = "CommentForm";

    private Context context;
    private ViewGroup container;

    private SendButtonListener sendButtonListener;

    private View commentForm;

    private TextView errorView;

    private View quoteContainer;
    private TextView quoteTextView;
    private View discardQuoteWidget;

    private View commentContainer;
    private EditText commentTextInput;
    private View sendCommentWidget;


    public CommentForm(Context context, ViewGroup container) {
        this.context = context;
        this.container = container;
    }


    // Интерфесные методы
    @Override
    public void addSendButtonListener(SendButtonListener listener) {
        this.sendButtonListener = listener;
    }

    @Override
    public void setQuote(String text) {
        quoteTextView.setText(text);
        MyUtils.show(quoteContainer);
    }

    @Override
    public void show() {
        constructAndAttach();
        MyUtils.showKeyboardOnFocus(context, commentTextInput);
    }

    @Override
    public void hide() {
        container.removeAllViews();
    }

    @Override
    public void enable() {
        MyUtils.enable(discardQuoteWidget);
        MyUtils.enable(commentTextInput);
        MyUtils.enable(sendCommentWidget);
    }

    @Override
    public void disable() {
        MyUtils.disable(discardQuoteWidget);
        MyUtils.disable(commentTextInput);
        MyUtils.disable(sendCommentWidget);
    }

    @Override
    public boolean isVisible() {
        return View.VISIBLE == commentForm.getVisibility();
    }

    @Override
    public void showError(int messageId, String consoleMessage) {
//        hideKeyboard();

        String msg = (Config.DEBUG_MODE) ? consoleMessage : context.getResources().getString(messageId);

        errorView.setText(msg);
        MyUtils.show(errorView);

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
    private void constructAndAttach() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        container.removeAllViews();
        View layout = layoutInflater.inflate(R.layout.comment_form, container, true);

        commentForm = layout.findViewById(R.id.commentForm);
        errorView = layout.findViewById(R.id.errorView);
        quoteContainer = layout.findViewById(R.id.quoteContainer);
        quoteTextView = layout.findViewById(R.id.quoteTextView);
        discardQuoteWidget = layout.findViewById(R.id.clearQuoteWidget);
        commentContainer = layout.findViewById(R.id.commentContainer);
        commentTextInput = layout.findViewById(R.id.commentTextInput);
        sendCommentWidget = layout.findViewById(R.id.sendCommentWidget);

        sendCommentWidget.setOnClickListener(this);
        discardQuoteWidget.setOnClickListener(this);
    }

    private void sendComment() {
        hideError();
        sendButtonListener.onSendCommentClicked(getText());
    }

    private String getText() {
        return commentTextInput.getText().toString();
    }

    private void clearQuote() {
        quoteTextView.setText("");
        MyUtils.hide(quoteContainer);
    }

    private void hideKeyboard() {
        MyUtils.hideKeyboard(context, commentTextInput);
    }
}
