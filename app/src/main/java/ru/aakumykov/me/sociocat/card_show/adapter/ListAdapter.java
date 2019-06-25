package ru.aakumykov.me.sociocat.card_show.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.Config;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.CardShow_View_Stub;
import ru.aakumykov.me.sociocat.card_show.iCardShow_View;
import ru.aakumykov.me.sociocat.card_show.presenters.iCardPresenter;
import ru.aakumykov.me.sociocat.card_show.presenters.iCommentsPresenter;
import ru.aakumykov.me.sociocat.card_show.view_holders.Throbber_ViewHolder;
import ru.aakumykov.me.sociocat.card_show.view_holders.Card_ViewHolder;
import ru.aakumykov.me.sociocat.card_show.view_holders.Comment_ViewHolder;
import ru.aakumykov.me.sociocat.card_show.view_holders.LoadMore_ViewHolder;
import ru.aakumykov.me.sociocat.interfaces.iMyDialogs;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.card_show.list_items.ListItem;
import ru.aakumykov.me.sociocat.card_show.list_items.Throbber_Item;
import ru.aakumykov.me.sociocat.card_show.list_items.LoadMore_Item;
import ru.aakumykov.me.sociocat.utils.MyDialogs;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        iListAdapter,
        iCardView,
        iCommentsView
{
    private final static String TAG = "ListAdapter";
    private List<ListItem> itemsList;
    private iCardPresenter cardPresenter;
    private iCommentsPresenter commentsPresenter;
    private iCardShow_View view;

    public ListAdapter() {
        this.itemsList = new ArrayList<>();
    }


    // RecyclerView
    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        ListItem listItem = itemsList.get(position);

        if (listItem instanceof Card) {
            return ListItem.CARD_VIEW_TYPE;
        }
        else if (listItem instanceof Comment) {
            return ListItem.COMMENT_VIEW_TYPE;
        }
        else if (listItem instanceof Throbber_Item) {
            return ListItem.THROBBER_VIEW_TYPE;
        }
        else if (listItem instanceof LoadMore_Item) {
            return ListItem.LOAD_MORE_VIEW_TYPE;
        }
        else {
            return -1;
        }
    }

    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ListItem.CARD_VIEW_TYPE:
                itemView = layoutInflater.inflate(R.layout.card_show_card, parent, false);
                return new Card_ViewHolder(itemView, cardPresenter);

            case ListItem.COMMENT_VIEW_TYPE:
                itemView = layoutInflater.inflate(R.layout.card_show_comment, parent, false);
                return new Comment_ViewHolder(itemView, commentsPresenter);

            case ListItem.THROBBER_VIEW_TYPE:
                itemView = layoutInflater.inflate(R.layout.card_show_throbber, parent, false);
                return new Throbber_ViewHolder(itemView);

            case ListItem.LOAD_MORE_VIEW_TYPE:
                itemView = layoutInflater.inflate(R.layout.card_show_load_more, parent, false);
                return new LoadMore_ViewHolder(itemView, commentsPresenter);

            default:
                // TODO: попробовать возвлащать заглушку
                throw new RuntimeException("Unknown view type: "+viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ListItem item = itemsList.get(position);

        switch (viewHolder.getItemViewType()) {

            case ListItem.CARD_VIEW_TYPE:
                ((Card_ViewHolder) viewHolder).initialize((Card) item);
                break;

            case ListItem.COMMENT_VIEW_TYPE:
                ((Comment_ViewHolder) viewHolder).initialize((Comment) item);
                break;

            case ListItem.THROBBER_VIEW_TYPE:
                ((Throbber_ViewHolder) viewHolder).initialize((Throbber_Item) item);
                break;

            case ListItem.LOAD_MORE_VIEW_TYPE:
                ((LoadMore_ViewHolder) viewHolder).initialize((LoadMore_Item) item, position);

            default:
                break;
        }
    }


    // iListAdapter
    @Override
    public void bindPresenters(iCardPresenter cardPresenter, iCommentsPresenter commP) {
        this.cardPresenter = cardPresenter;
        this.commentsPresenter = commP;
    }

    @Override
    public void unbindPresenters() {
        this.commentsPresenter = null;
        this.cardPresenter = null;
    }

    @Override public void bindView(iCardShow_View view) {
        this.view = view;
    }

    @Override public void unbindView() {
        this.view = new CardShow_View_Stub();
    }


    // iCardView
    @Override
    public void displayCard(Card card) {
        int cardPosition = 0;

        if (0 == itemsList.size())
            itemsList.add(card);
        else
            itemsList.set(cardPosition, card);

        notifyItemChanged(cardPosition);
    }


    @Override
    public void showCardThrobber() {
        int index = 0;
        itemsList.add(index, new Throbber_Item(R.string.CARD_SHOW_loading_card));
        notifyItemChanged(index);
    }

    @Override
    public void hideCardThrobber() {
        removeLastItemIfType(ListItem.ItemType.THROBBER_ITEM);
    }

    @Override
    public void showCardError(int errorMsgId, String consoleErrorMsg) {

    }

    @Override
    public void hideCardError() {

    }

    @Override
    public void showCardDeleteDialog(Card card) {
        MyDialogs.cardDeleteDialog(
                view.getActivity(),
                card.getTitle(),
                new iMyDialogs.Delete() {
                    @Override
                    public void onCancelInDialog() {

                    }

                    @Override
                    public void onNoInDialog() {

                    }

                    @Override
                    public boolean onCheckInDialog() {
                        return true;
                    }

                    @Override
                    public void onYesInDialog() {
                        cardPresenter.onDeleteConfirmed();
                    }
                }
        );
    }


    // iCommentsView
    @Override
    public void showCommentsThrobber(int position) {
        hideLoadMoreItem(position);

        Throbber_Item throbberItem = new Throbber_Item(R.string.COMMENTS_loading_comments);

        if (position > 0) {
            itemsList.add(position, throbberItem);
            notifyItemChanged(position);
        } else {
            itemsList.add(throbberItem);
            notifyItemChanged(itemsList.size() - 1);
        }
    }

    @Override
    public void hideCommentsThrobber(int position) {
        if (position > 0 && itemsList.size() >= position + 1) {
            itemsList.remove(position);
            notifyItemRemoved(position);
        } else {
            removeLastItemIfType(ListItem.ItemType.THROBBER_ITEM);
        }
    }

    @Override
    public void showCommentsError(int errorMsgId, String consoleErrorMsg) {
        MyUtils.showCustomToast(view.getAppContext(), errorMsgId);
        Log.e(TAG, consoleErrorMsg);
    }

    @Override
    public void hideCommentsError() {

    }

    @Override
    public void showDeleteDialog(Comment comment) {
        String text = MyUtils.cutToLength(comment.getText(), 20);
        String msg = view.getString(R.string.CARD_SHOW_delete_comment_dialog_message, text);

        MyDialogs.commentDeleteDialog(
                view.getActivity(),
                msg,
                new iMyDialogs.Delete() {
                    @Override
                    public void onCancelInDialog() {

                    }

                    @Override
                    public void onNoInDialog() {

                    }

                    @Override
                    public boolean onCheckInDialog() {
                        return true;
                    }

                    @Override
                    public void onYesInDialog() {
                        commentsPresenter.onDeleteConfirmed(comment);
                    }
                }
        );
    }

    @Override
    public void setList(List<Comment> inputList) {
        int listSize = itemsList.size();
        if (listSize > 1) {
            List<ListItem> existingCommentsList = itemsList.subList(1, listSize - 1);
            itemsList.removeAll(existingCommentsList);
            notifyItemRangeRemoved(1, existingCommentsList.size());
        }
        addList(inputList, 1, null);
    }

    @Override
    public void addList(List<Comment> inputList, int position, @Nullable Comment alreadyVisibleTailComment) {

//        int inputListSize = inputList.size();
//        Comment lastComment = null;
//
//        if (inputListSize > Config.DEFAULT_COMMENTS_LOAD_COUNT) {
//            int lastCommentIndex = inputListSize - 1;
//            lastComment = inputList.get(lastCommentIndex);
//            inputList.remove(lastCommentIndex);
//        }
//
//        int start = this.itemsList.size();
//        int count = inputList.size();
//
//        hideLoadMoreItem(position);
//
//        this.itemsList.addAll(position, inputList);
//        notifyItemRangeChanged(start, count);
//
//        if (null != lastComment)
//            showLoadMoreItem(position, lastComment);

        // Другой вариант

        // Убираю уже видимый конечный комментарий
        if (null != alreadyVisibleTailComment) {
            Comment tailComment = inputList.get(inputList.size() - 1);
            if (tailComment.getKey().equals(alreadyVisibleTailComment.getKey()))
                inputList.remove(inputList.size() - 1);
        }

        /* Если список больше порции комментариев, установленной для показа,
           от него отщипывается последний элемент для использования в загрузке
           следующей порции. */
        Comment loadMoreStartComment = (inputList.size() > Config.DEFAULT_COMMENTS_LOAD_COUNT) ?
                inputList.get(inputList.size()-1) : null;

        if (null != loadMoreStartComment)
            inputList.remove(inputList.size()-1);

        // Отправляю список на показ
        itemsList.addAll(position, inputList);
        notifyItemRangeChanged(position, inputList.size());

        // Добавляю элемент "Загрузить ещё"
        if (null != loadMoreStartComment)
            showLoadMoreItem(position + inputList.size(), loadMoreStartComment);
    }

    @Override
    public void attachComment(Comment comment, @Nullable AttachCommentCallbacks callbacks) {
        itemsList.add(comment);
        notifyItemChanged(getLastCommentIndex());

        if (null != callbacks)
            callbacks.onCommentAttached(comment);
    }

    @Override
    public void updateComment(Comment oldComment, Comment newComment) {
        int index = itemsList.indexOf(oldComment);
        itemsList.set(index, newComment);
        notifyItemChanged(index);
    }

    @Override
    public void removeComment(Comment comment) {
        int index = itemsList.indexOf(comment);
        itemsList.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public Comment getComment(String commentKey) {
        commentKey = commentKey + ""; // Защита от NULL
        for (ListItem listItem : itemsList) {
            if (listItem.isCommentItem()) {
                Comment comment = (Comment) listItem;
                if (commentKey.equals(comment.getKey()))
                    return comment;
            }
        }
        return null;
    }

    @Override
    public void scrollToComment(String commentKey) {
        Comment comment = getComment(commentKey);
        if (null != comment)
            view.scrollListToPosition(itemsList.indexOf(comment));
    }


    // Внутренние методы
    private void showLoadMoreItem(int position, Comment commentToStartFrom) {
        LoadMore_Item loadMoreItem = new LoadMore_Item(commentToStartFrom);
        itemsList.add(position, loadMoreItem);
    }

    private void hideLoadMoreItem(int position) {
        if (position > -1 && itemsList.size() >= position + 1) {
            itemsList.remove(position);
            notifyItemRemoved(position);
        } else {
            removeLastItemIfType(ListItem.ItemType.LOAD_MORE_ITEM);
        }
    }

    private int getMaxIndex() {
        return itemsList.size() - 1;
    }

    private ListItem getLastItem(ListItem.ItemType itemType) {
        int maxIndex = getMaxIndex();

        if (maxIndex < 0)
            return null;

        ListItem listItem = itemsList.get(maxIndex);

        if (listItem.is(itemType))
            return listItem;
        else
            return null;
    }

    private int getLastCommentIndex() {
        return itemsList.size()-1;
    }

    private void removeLastItemIfType(ListItem.ItemType itemType) {
        int maxIndex = itemsList.size() - 1;

        ListItem lastItem = itemsList.get(maxIndex);

        if (null != lastItem && lastItem.is(itemType)) {
            itemsList.remove(maxIndex);
            notifyItemRemoved(maxIndex);
        }
    }

}
