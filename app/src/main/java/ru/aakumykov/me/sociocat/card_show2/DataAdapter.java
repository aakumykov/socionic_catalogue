package ru.aakumykov.me.sociocat.card_show2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.complexrecyclerview.MyUtils;
import ru.aakumykov.me.complexrecyclerview.R;
import ru.aakumykov.me.complexrecyclerview.card_show2.models.Card;
import ru.aakumykov.me.complexrecyclerview.card_show2.models.Comment;
import ru.aakumykov.me.complexrecyclerview.card_show2.models.CommentsThrobber;
import ru.aakumykov.me.complexrecyclerview.card_show2.models.Item;
import ru.aakumykov.me.complexrecyclerview.card_show2.models.LoadMore;
import ru.aakumykov.me.complexrecyclerview.card_show2.view_holders.Base_ViewHolder;
import ru.aakumykov.me.complexrecyclerview.card_show2.view_holders.Card_ViewHolder;
import ru.aakumykov.me.complexrecyclerview.card_show2.view_holders.Comment_ViewHolder;
import ru.aakumykov.me.complexrecyclerview.card_show2.view_holders.CommentsThrobber_ViewHolder;
import ru.aakumykov.me.complexrecyclerview.card_show2.view_holders.LoadMore_ViewHolder;

public class DataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    implements iDataAdapter
{
    private final static String TAG = "DataAdapter";
    private ArrayList<Item> itemsList;

    private CardShow_View view;


    // Конструктор
    DataAdapter(ArrayList<Item> list) {
        itemsList = new ArrayList<>();
        itemsList.addAll(list);
    }


    public void bindView(CardShow_View view) {
        this.view = view;
    }

    public void unbindView() {
        this.view = null;
    }


    // Системные методы
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
                itemView = layoutInflater.inflate(R.layout.list_item_card, parent, false);
                return new Card_ViewHolder(itemView);

            case Item.COMMENT_VIEW_TYPE:
                itemView = layoutInflater.inflate(R.layout.list_item_comment, parent, false);
                return new Comment_ViewHolder(itemView);

            case Item.LOAD_MORE_VIEW_TYPE:
                itemView = layoutInflater.inflate(R.layout.list_item_comments_load_more, parent, false);
                return new LoadMore_ViewHolder(itemView);

            case Item.COMMENTS_THROBBER_VIEW_TYPE:
                itemView = layoutInflater.inflate(R.layout.list_item_comments_throbber, parent, false);
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
                ((Card_ViewHolder) viewHolder).initialize((Card) item, view);
                break;

            case Item.COMMENT_VIEW_TYPE:
                ((Comment_ViewHolder) viewHolder).initialize((Comment) item, view);
                break;

            case Item.LOAD_MORE_VIEW_TYPE:
                Comment lastComment = getLastComment();
                ((LoadMore_ViewHolder) viewHolder).initialize(lastComment, view);

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
        hideLoadMoreItem();

        int startIndex = getLastIndex() + 1;
        int itemsCount = list.size();
        itemsList.addAll(list);
        notifyItemRangeChanged(startIndex, itemsCount);

        showLoadMoreItem();
    }

    @Override
    public void appendComment(Comment comment, @Nullable AppendCommentCallbacks callbacks) {
        hideLoadMoreItem();
        itemsList.add(comment);
        showLoadMoreItem();
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
    public void setComment(int position, Comment comment) {
        MyUtils.showCustomToast(view, "Замена комментария ещё не реализована");
    }

    @Override
    public void insertComment(int position, Comment comment) {
        MyUtils.showCustomToast(view, "Вставка комментария ещё не реализована");
    }

    @Override
    public void removeComment(int position) {
        MyUtils.showCustomToast(view, "Удаление комментария ещё не реализовано");
    }

    @Override
    public Comment getLastComment() {
        Item item; int index = getLastIndex();
        do item = itemsList.get(index--);
        while (!item.getItemType().equals(Item.ItemType.COMMENT_ITEM));
        return (Comment) item;
    }


    @Override
    public void showLoadMoreItem() {
        itemsList.add(new LoadMore());
        notifyItemChanged(getLastIndex());
    }

    @Override
    public void hideLoadMoreItem() {
        removeLastItemIfType(Item.ItemType.LOAD_MORE_ITEM);
    }


    @Override
    public void showCommentsThrobber() {
        itemsList.add(new CommentsThrobber());
        notifyItemChanged(getLastIndex());
    }

    @Override
    public void hideCommentsThrobber() {
        removeLastItemIfType(Item.ItemType.COMMENTS_THROBBER_ITEM);
//        itemsList.remove(getLastIndex());
//        notifyDataSetChanged();
    }


    // Внутренние методы
    private void removeLastItemIfType(Item.ItemType itemType) {
        int lastItemIndex = getLastIndex();
        Item lastItem = itemsList.get(lastItemIndex);

        if (lastItem.getItemType().equals(itemType)) {
            itemsList.remove(getLastIndex());
            notifyItemRemoved(lastItemIndex);
        }
    }

    private int getLastIndex() {
        int listSize = itemsList.size();
        return (0 == listSize) ? 0 : listSize - 1;
    }
}
