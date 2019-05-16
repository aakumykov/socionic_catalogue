package ru.aakumykov.me.sociocat.card_show2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show2.controllers.iCardController;
import ru.aakumykov.me.sociocat.card_show2.controllers.iCommentsController;
import ru.aakumykov.me.sociocat.card_show2.view_holders.Base_ViewHolder;
import ru.aakumykov.me.sociocat.card_show2.view_holders.Card_ViewHolder;
import ru.aakumykov.me.sociocat.card_show2.view_holders.Comment_ViewHolder;
import ru.aakumykov.me.sociocat.card_show2.view_holders.CommentsThrobber_ViewHolder;
import ru.aakumykov.me.sociocat.card_show2.view_holders.LoadMore_ViewHolder;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.models.CommentsThrobber;
import ru.aakumykov.me.sociocat.models.Item;
import ru.aakumykov.me.sociocat.models.LoadMore;

public class DataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    implements iDataAdapter
{
    private final static String TAG = "DataAdapter";
    private ArrayList<Item> itemsList;

    private iCardController cardController;
    private iCommentsController commentsController;


    // Конструктор
    DataAdapter() {
        itemsList = new ArrayList<>();
    }


    // Системные методы
    @Override
    public void bindControllers(iCardController cardController, iCommentsController commentsController) {
        this.cardController = cardController;
        this.commentsController = commentsController;
    }

    @Override
    public void unbindControllers() {
        this.cardController = null;
        this.commentsController = null;
    }

    @Override
    public int getItemCount() {
        return itemsList == null ? 0 : itemsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Item item = itemsList.get(position);

        switch (item.getItemType()) {
            case CARD_ITEM:
                return Item.CARD_VIEW_TYPE;

            case COMMENT_ITEM:
                return Item.COMMENT_VIEW_TYPE;

            case LOAD_MORE_ITEM:
                return Item.LOAD_MORE_VIEW_TYPE;

            case COMMENTS_THROBBER_ITEM:
                return Item.COMMENTS_THROBBER_VIEW_TYPE;

            default:
                return -1;
        }
    }

    @Override @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case Item.CARD_VIEW_TYPE:
                itemView = layoutInflater.inflate(R.layout.card_show_card, parent, false);
                return new Card_ViewHolder(itemView);

            case Item.COMMENT_VIEW_TYPE:
                itemView = layoutInflater.inflate(R.layout.card_show_comment, parent, false);
                return new Comment_ViewHolder(itemView);

            case Item.LOAD_MORE_VIEW_TYPE:
                itemView = layoutInflater.inflate(R.layout.card_show_comments_load_more, parent, false);
                return new LoadMore_ViewHolder(itemView);

            case Item.COMMENTS_THROBBER_VIEW_TYPE:
                itemView = layoutInflater.inflate(R.layout.card_show_comments_throbber, parent, false);
                return new CommentsThrobber_ViewHolder(itemView);

            default:
                // TODO: попробовать возвлащать заглушку
                throw new RuntimeException("Unknown view type: "+viewType);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int listPosition) {

        Item item = itemsList.get(listPosition);

        switch (viewHolder.getItemViewType()) {

            case Item.CARD_VIEW_TYPE:
                ((Card_ViewHolder) viewHolder).initialize((Card) item, cardController);
                break;

            case Item.COMMENT_VIEW_TYPE:
                ((Comment_ViewHolder) viewHolder).initialize((Comment) item, commentsController);
                break;

            case Item.LOAD_MORE_VIEW_TYPE:
                Comment lastComment = getLastComment();
                ((LoadMore_ViewHolder) viewHolder).initialize(lastComment, commentsController);

            default:
                break;
        }
    }


    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ((Base_ViewHolder) holder).onAttached();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        ((Base_ViewHolder) holder).onDetached();
    }


    // Интерфейсные методы
    @Override
    public void setCard(Card card) {
        if (itemsList.size() > 0) itemsList.set(0, card);
        else itemsList.add(card);
        notifyItemChanged(0);
    }

    @Override
    public void appendComments(List<Comment> list) {
        int startIndex = getLastItemIndex() + 1;
        int itemsCount = list.size();
        itemsList.addAll(list);
        notifyItemRangeChanged(startIndex, itemsCount);
    }

    @Override
    public void appendComment(Comment comment, @Nullable AppendCommentCallbacks callbacks) {
        List<Comment> list = new ArrayList<>();
        list.add(comment);
        appendComments(list);

        if (null != callbacks)
            callbacks.onCommentAppended();
    }

    @Override
    public int findCommentPosition(Comment comment) {
        return itemsList.indexOf(comment);
    }

    @Override
    public void clearList() {
        itemsList.clear();
        notifyDataSetChanged();
    }

    @Override
    public void updateComment(int position, Comment comment) {

    }

    @Override
    public void insertComment(int position, Comment comment) {

    }

    @Override
    public void removeComment(int position) {

    }


    @Override public Comment getComment(int position) {
        // TODO: проверка на существование
        Item item = itemsList.get(position);
        return (Comment) item;
    }

    @Override
    public Comment getLastComment() {
        int maxListIndex = getLastItemIndex();

        if (0 == maxListIndex)
            return null;

        Item item;
        do {
            item = itemsList.get(maxListIndex);
            if (item.isCommentItem())
                return (Comment) item;
            maxListIndex -= 1;
        } while (maxListIndex > 0);

        return null;
    }


    @Override
    public void hideLastServiceItem() {
        hideLoadMoreItem();
        hideCommentsThrobber();
    }

    @Override
    public void showLoadMoreItem() {
        itemsList.add(new LoadMore());
        notifyItemChanged(getLastItemIndex());
    }

    @Override
    public void hideLoadMoreItem() {
        removeLastItemIfType(Item.ItemType.LOAD_MORE_ITEM);
    }


    @Override
    public void showCommentsThrobber() {
        itemsList.add(new CommentsThrobber());
        notifyItemChanged(getLastItemIndex());
    }

    @Override
    public void hideCommentsThrobber() {
        removeLastItemIfType(Item.ItemType.COMMENTS_THROBBER_ITEM);
    }


    // Внутренние методы
    private void removeLastItemIfType(Item.ItemType itemType) {
        int lastItemIndex = getLastItemIndex();
        Item lastItem = itemsList.get(lastItemIndex);

        if (lastItem.is(itemType)) {
            itemsList.remove(getLastItemIndex());
            notifyItemRemoved(lastItemIndex);
        }
    }

    private int getLastItemIndex() {
        int listSize = itemsList.size();
        if (0 == listSize) return 0;
        else return listSize - 1;
    }
}
