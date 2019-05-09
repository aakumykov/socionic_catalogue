package ru.aakumykov.me.sociocat.card_show_2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show_2.view_holders.Card_ViewHolder;
import ru.aakumykov.me.sociocat.card_show_2.view_holders.Comment_ViewHolder;
import ru.aakumykov.me.sociocat.card_show_2.view_holders.CommentsThrobber_ViewHolder;
import ru.aakumykov.me.sociocat.card_show_2.view_holders.LoadMore_ViewHolder;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.models.CommentsThrobber;
import ru.aakumykov.me.sociocat.models.Item;
import ru.aakumykov.me.sociocat.models.LoadMore;
import ru.aakumykov.me.sociocat.utils.MyUtils;

import static ru.aakumykov.me.sociocat.models.Item.ItemType.COMMENTS_THROBBER_ITEM;
import static ru.aakumykov.me.sociocat.models.Item.ItemType.COMMENT_ITEM;
import static ru.aakumykov.me.sociocat.models.Item.ItemType.LOAD_MORE_ITEM;

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
                itemView = layoutInflater.inflate(R.layout.card, parent, false);
                return new Card_ViewHolder(itemView);

            case Item.COMMENT_VIEW_TYPE:
                itemView = layoutInflater.inflate(R.layout.comment, parent, false);
                return new Comment_ViewHolder(itemView);

            case Item.LOAD_MORE_VIEW_TYPE:
                itemView = layoutInflater.inflate(R.layout.comments_load_more, parent, false);
                return new LoadMore_ViewHolder(itemView);

            case Item.COMMENTS_THROBBER_VIEW_TYPE:
                itemView = layoutInflater.inflate(R.layout.comments_throbber, parent, false);
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
        itemsList.addAll(list);
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
        MyUtils.showToast(view, "Замена комментария ещё не реализована");
    }

    @Override
    public void insertComment(int position, Comment comment) {
        MyUtils.showToast(view, "Вставка комментария ещё не реализована");
    }

    @Override
    public void removeComment(int position) {
        MyUtils.showToast(view, "Удаление комментария ещё не реализовано");
    }

    @Override
    public Comment getLastComment() {
        Item item; int index = itemsList.size()-1;
        do item = itemsList.get(index--);
        while (!item.getItemType().equals(COMMENT_ITEM));
        return (Comment) item;
    }


    @Override
    public void showLoadMoreItem() {
        itemsList.add(new LoadMore());
        notifyDataSetChanged();
    }

    @Override
    public void hideLoadMoreItem() {
        removeLastItemIfType(LOAD_MORE_ITEM);
    }


    @Override
    public void showCommentsThrobber() {
        itemsList.add(new CommentsThrobber());
        notifyItemChanged(itemsList.size()-1);
    }

    @Override
    public void hideCommentsThrobber() {
        removeLastItemIfType(COMMENTS_THROBBER_ITEM);
//        itemsList.remove(itemsList.size()-1);
//        notifyDataSetChanged();
    }


    // Внутренние методы
    private void removeLastItemIfType(Item.ItemType itemType) {
        int listSize = itemsList.size();
        if (0 != listSize) {
            int lastIndex = itemsList.size() - 1;
            Item lastItem = itemsList.get(lastIndex);
            if (lastItem.getItemType().equals(itemType))
                itemsList.remove(itemsList.size() - 1);
            notifyDataSetChanged();
        }
    }


}
