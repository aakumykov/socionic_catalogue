package ru.aakumykov.me.sociocat.utils.comment_form;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class CommentForm implements
        iCommentForm,
        View.OnClickListener
{
    private Context context;
    private SendButtonListener sendButtonListener;

    private View commentForm;

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
        MyUtils.show(commentContainer);
        MyUtils.show(commentForm);
        MyUtils.showKeyboardOnFocus(context, commentTextInput);
    }

    @Override
    public void hide() {
        enable();

        quoteTextView.setText("");
        MyUtils.hide(quoteContainer);

        commentTextInput.setText("");
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

    @Override public boolean isVisible() {
        return View.VISIBLE == commentForm.getVisibility();
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
        sendButtonListener.onSendCommentClicked(getText());
    }

    private String getText() {
        return commentTextInput.getText().toString();
    }

    private void clearQuote() {
        quoteTextView.setText("");
        MyUtils.hide(quoteContainer);
    }

}
