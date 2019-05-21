package ru.aakumykov.me.sociocat.card_show.comment_form;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.list_items.ListItem;
import ru.aakumykov.me.sociocat.models.Comment;
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
        View formLayout = layoutInflater.inflate(R.layout.card_show_comment_form, container, true);

        commentForm = formLayout.findViewById(R.id.commentForm);

        quoteContainer = formLayout.findViewById(R.id.quoteContainer);
        quoteTextView = formLayout.findViewById(R.id.quoteTextView);
        clearQuoteWidget = formLayout.findViewById(R.id.clearQuoteWidget);
        commentTextInput = formLayout.findViewById(R.id.commentTextInput);
        sendCommentWidget = formLayout.findViewById(R.id.sendCommentWidget);

        sendCommentWidget.setOnClickListener(this);
        clearQuoteWidget.setOnClickListener(this);
    }

    @Override
    public void addSendButtonListener(SendButtonListener listener) {
        this.sendButtonListener = listener;
    }

    @Override
    public void setQuote(ListItem listItem) {
        String text = ((Comment)listItem).getText();
        quoteTextView.setText(text);
        MyUtils.show(quoteContainer);
    }

    @Override
    public void show() {
        MyUtils.show(commentForm);
    }

    @Override
    public void remove() {
        quoteTextView.setText("");
        MyUtils.hide(quoteContainer);

        commentTextInput.setText("");
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
