package ru.aakumykov.me.sociocat.card_show;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.list_items.ListItem;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class CommentForm implements
        iCommentForm,
        View.OnClickListener
{
    private Context context;

    private View quoteContainer;
    private TextView quoteTextView;
    private View clearQuoteWidget;

    private EditText commentTextInput;
    private View sendCommentWidget;


    @Override
    public void attachTo(Context context, ViewGroup container) {
        this.context = context;

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        container.removeAllViews();
        View formLayout = layoutInflater.inflate(R.layout.card_show_comment_form, container, true);

        quoteContainer = formLayout.findViewById(R.id.quoteContainer);
        quoteTextView = formLayout.findViewById(R.id.quoteTextView);
        clearQuoteWidget = formLayout.findViewById(R.id.clearQuoteWidget);
        commentTextInput = formLayout.findViewById(R.id.commentTextInput);
        sendCommentWidget = formLayout.findViewById(R.id.sendCommentWidget);

        sendCommentWidget.setOnClickListener(this);
        clearQuoteWidget.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.sendCommentWidget:
                sendCommentClicked();
                break;

            case R.id.clearQuoteWidget:
                clearQuoteClicked();
                break;

            default:
                break;
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    @Override
    public void setQuote(ListItem listItem) {
        String text = ((Comment)listItem).getText();
        quoteTextView.setText(text);
        MyUtils.show(quoteContainer);
    }

    @Override
    public void clearQuote() {

    }

    @Override
    public String getText() {
        return null;
    }


    // Внутренние методы
    private void sendCommentClicked() {
        MyUtils.showCustomToast(context, commentTextInput.getText().toString());
    }

    private void clearQuoteClicked() {
        quoteTextView.setText("");
        MyUtils.hide(quoteContainer);
    }
}
