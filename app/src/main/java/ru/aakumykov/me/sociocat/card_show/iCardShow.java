package ru.aakumykov.me.sociocat.card_show;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.card_show.adapter.iCardView;
import ru.aakumykov.me.sociocat.card_show.adapter.iCommentsView;
import ru.aakumykov.me.sociocat.card_show.list_items.iTextItem;
import ru.aakumykov.me.sociocat.card_show.view_holders.iCard_ViewHolder;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.utils.comment_form.iCommentForm;

public interface iCardShow {

    interface iCardPresenter {

        void bindPageView(iPageView pageView);
        void unbindPageView();

        void bindListAdapter(iCardView listAdapter);
        void unbindListAdapter();

        void onWorkBegins(@Nullable String cardKey, @Nullable String commentKey);
        void onErrorOccurs();

        void onReplyClicked();

        Card getCard();

        boolean canEditCard();
        boolean canDeleteCard();

        void onEditClicked();
        void onCardEdited(Card card);

        void onDeleteClicked();
        void onDeleteConfirmed();

        void onTagClicked(String tag);

        void onRatingUpClicked(iCard_ViewHolder cardViewHolder);
        void onRatingDownClicked(iCard_ViewHolder cardViewHolder);
    }

    interface iCommentsPresenter {

        void bindPageView(iPageView pageView);
        void unbindPageView();

        void bindCommentsView(iCommentsView listAdapter);
        void unbindCommentsView();


        void onWorkBegins(String cardKey, @Nullable String scrollToCommentKey);

        void onLoadMoreClicked(int insertPosition, @Nullable Comment beginningComment);


        void onReplyClicked(iTextItem repliedItem);

        void onEditCommentClicked(Comment comment);

        void onDeleteCommentClicked(Comment comment);

        void onDeleteConfirmed(Comment comment);

        void onSendCommentClicked(iCommentForm commentForm);
    }
}
