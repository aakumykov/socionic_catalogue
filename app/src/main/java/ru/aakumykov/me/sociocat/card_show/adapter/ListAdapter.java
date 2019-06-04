package ru.aakumykov.me.sociocat.card_show.adapter;

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
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.card_show.list_items.ListItem;
import ru.aakumykov.me.sociocat.card_show.list_items.Throbber_Item;
import ru.aakumykov.me.sociocat.card_show.list_items.LoadMore_Item;

public class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        iListAdapter,
        iCardView,
        iCommentsView
{
    private final static String TAG = "ListAdapter";
    private List<ListItem> list;
    private iCardPresenter cardPresenter;
    private iCommentsPresenter commentsPresenter;
    private iCardShow_View view;

    public ListAdapter() {
        this.list = new ArrayList<>();
    }


    // RecyclerView
    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        ListItem listItem = list.get(position);

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
        ListItem item = list.get(position);

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

        if (0 == list.size())
            list.add(card);
        else
            list.set(cardPosition, card);

        notifyItemChanged(cardPosition);
    }


    @Override
    public void showCardThrobber() {
        int index = 0;
        list.add(index, new Throbber_Item(R.string.CARD_SHOW_loading_card));
        notifyItemChanged(index);
    }

    @Override
    public void hideCardThrobber() {
        removeItemIfType(ListItem.ItemType.THROBBER_ITEM, 0);
    }

    @Override
    public void showCardError(int errorMsgId, String consoleErrorMsg) {

    }

    @Override
    public void hideCardError() {

    }


    // iCommentsView
    @Override
    public void showCommentsThrobber() {
        hideLoadMoreItem();
        list.add(new Throbber_Item(R.string.COMMENTS_loading_comments));
        notifyItemChanged(getMaxIndex());
    }

    @Override
    public void hideCommentsThrobber() {
        int maxIndex = getMaxIndex();
        // TODO: проверить с -1
        removeItemIfType(ListItem.ItemType.THROBBER_ITEM, maxIndex);
    }

    @Override
    public void showCommentsError(int errorMsgId, String consoleErrorMsg) {

    }

    @Override
    public void hideCommentsError() {

    }

    @Override
    public void setList(List<Comment> inputList) {
        int listSize = list.size();
        if (listSize > 1) {
            List<ListItem> existingCommentsList = list.subList(1, listSize - 1);
            list.removeAll(existingCommentsList);
            notifyItemRangeRemoved(1, existingCommentsList.size());
        }
        appendList(inputList, 1);
    }

    @Override
    public void appendList(List<Comment> inputList, int position) {

        int inputListSize = inputList.size();
        Comment lastComment = null;

        if (inputListSize > Config.DEFAULT_COMMENTS_LOAD_COUNT) {
            int lastCommentIndex = inputListSize - 1;
            lastComment = inputList.get(lastCommentIndex);
            inputList.remove(lastCommentIndex);
        }

        int start = this.list.size();
        int count = inputList.size();

        hideLoadMoreItem();

        this.list.addAll(inputList);
        notifyItemRangeChanged(start, count);

        if (null != lastComment)
            showLoadMoreItem(lastComment);
    }

    @Override
    public void attachComment(Comment comment, @Nullable AttachCommentCallbacks callbacks) {
        list.add(comment);
        notifyItemChanged(getLastCommentIndex());

        if (null != callbacks)
            callbacks.onCommentAttached(comment);
    }

    @Override
    public Comment getComment(String commentKey) {
        commentKey = commentKey + ""; // Защита от NULL
        for (ListItem listItem : list) {
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
            view.scrollListToPosition(list.indexOf(comment));
    }


    // Внутренние методы
    private void showLoadMoreItem(Comment lastComment) {
        LoadMore_Item loadMoreItem = new LoadMore_Item(lastComment);
        list.add(loadMoreItem);
    }

    private void hideLoadMoreItem() {
        int maxIndex = getMaxIndex();
        removeItemIfType(ListItem.ItemType.LOAD_MORE_ITEM, maxIndex);
    }

    private int getMaxIndex() {
        return list.size() - 1;
    }

    private ListItem getLastItem(ListItem.ItemType itemType) {
        int maxIndex = getMaxIndex();

        if (maxIndex < 0)
            return null;

        ListItem listItem = list.get(maxIndex);

        if (listItem.is(itemType))
            return listItem;
        else
            return null;
    }

    private int getLastCommentIndex() {
        return list.size()-1;
    }

    private void removeItemIfType(ListItem.ItemType itemType, int index) {

        if (0 != list.size() && list.size() > index) {

            ListItem listItem = list.get(index);

            if (listItem.is(itemType)) {
                list.remove(index);
                notifyItemRemoved(index);
            }

        }
    }

}
