package ru.aakumykov.me.sociocat.card_show2;

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
import ru.aakumykov.me.sociocat.card_show2.list_items.CardThrobber_Item;
import ru.aakumykov.me.sociocat.card_show2.list_items.Card_Item;
import ru.aakumykov.me.sociocat.card_show2.list_items.Comment_Item;
import ru.aakumykov.me.sociocat.card_show2.list_items.CommentsThrobber_Item;
import ru.aakumykov.me.sociocat.card_show2.list_items.LoadMore_Item;
import ru.aakumykov.me.sociocat.card_show2.list_items.iList_Item;
import ru.aakumykov.me.sociocat.card_show2.view_holders.CardThrobber_ViewHolder;
import ru.aakumykov.me.sociocat.card_show2.view_holders.Card_ViewHolder;
import ru.aakumykov.me.sociocat.card_show2.view_holders.CommentThrobber_ViewHolder;
import ru.aakumykov.me.sociocat.card_show2.view_holders.Comment_ViewHolder;
import ru.aakumykov.me.sociocat.card_show2.view_holders.LoadMore_ViewHolder;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;

public class DataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
    iCardShow2.iDataAdapter
{
    private final static int CARD_POSITION = 0;
    private final static int FIRST_COMMENT_POSITION = 1;
    private List<iList_Item> itemsList = new ArrayList<>();
    private iCardShow2.iPresenter presenter;


    DataAdapter(iCardShow2.iPresenter presenter) {
        this.presenter = presenter;
    }


    // RecyclerView
    @Override
    public int getItemViewType(int position) {
        iList_Item listItem = itemsList.get(position);
        return listItem.getItemType();
    }

    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case iList_Item.CARD:
                itemView = layoutInflater.inflate(R.layout.card_show_card, parent, false);
                return new Card_ViewHolder(itemView, presenter);

            case iList_Item.COMMENT:
                itemView = layoutInflater.inflate(R.layout.card_show_comment, parent, false);
                return new Comment_ViewHolder(itemView, presenter);

            case iList_Item.LOAD_MORE:
                itemView = layoutInflater.inflate(R.layout.card_show_load_more, parent, false);
                return new LoadMore_ViewHolder(itemView, presenter);

            case iList_Item.CARD_THROBBER:
                itemView = layoutInflater.inflate(R.layout.card_show_card_throbber, parent, false);
                return new CardThrobber_ViewHolder(itemView);

            case iList_Item.COMMENT_THROBBER:
                itemView = layoutInflater.inflate(R.layout.card_show_comment_throbber, parent, false);
                return new CommentThrobber_ViewHolder(itemView);

            default:
                throw new RuntimeException("Unknown vew type: "+viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        iList_Item listItem = itemsList.get(position);

        int itemType = listItem.getItemType();
        switch (itemType) {
            case iList_Item.CARD:
                ((Card_ViewHolder) holder).initialize(listItem);
                break;

            case iList_Item.COMMENT:
                Comment_ViewHolder commentViewHolder = (Comment_ViewHolder) holder;
                commentViewHolder.initialize(listItem);
                break;

            case iList_Item.LOAD_MORE:
                LoadMore_ViewHolder loadMoreViewHolder = (LoadMore_ViewHolder) holder;
                loadMoreViewHolder.initialize(listItem);
                break;

            case iList_Item.CARD_THROBBER:
                ((CardThrobber_ViewHolder) holder).initialize(listItem);
                break;

            case iList_Item.COMMENT_THROBBER:
                ((CommentThrobber_ViewHolder) holder).initialize(listItem);
                break;

            default:
                throw new RuntimeException("Unknown item type: "+itemType);
        }
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }


    // iDataAdapter
    @Override
    public void showCardThrobber() {
        displayAtCardPosition(new CardThrobber_Item());
    }

    @Override
    public void showCommentsThrobber(@Nullable Integer position) {
        CommentsThrobber_Item commentsThrobberItem = new CommentsThrobber_Item();
        if (null == position)
            itemsList.add(commentsThrobberItem);
        else {
            itemsList.add(position, commentsThrobberItem);
            itemsList.remove(position+1);
            notifyItemChanged(position);
            notifyItemChanged(position+1);
        }
    }

    @Override
    public void showCard(Card card) {
        displayAtCardPosition(new Card_Item(card));
    }

    private void displayAtCardPosition(iList_Item listItem) {
        if (listSize() > 0)
            itemsList.set(CARD_POSITION, listItem);
        else
            itemsList.add(CARD_POSITION, listItem);

        notifyItemChanged(CARD_POSITION);
    }

    @Override
    public void appendComments(List<Comment> commentsList) {
        insertComments(commentsList, FIRST_COMMENT_POSITION);
    }

    @Override
    public void insertComments(List<Comment> commentsList, int position) {

        int currentInsertPosition = position;

        for (Comment comment : commentsList) {
            itemsList.add(currentInsertPosition++, new Comment_Item(comment));
        }

        if (commentsList.size() >= Config.DEFAULT_COMMENTS_LOAD_COUNT)
            itemsList.set(currentInsertPosition, new LoadMore_Item(R.string.COMMENTS_load_more_comments));
        else
            itemsList.remove(currentInsertPosition);

        notifyDataSetChanged();
    }

    @Override
    public void appendOneComment(Comment comment) {
        itemsList.add(new Comment_Item(comment));
    }

    @Override
    public void removeComment(iList_Item listItem) {
        int index = itemsList.indexOf(listItem);
        if (index > 0) {
            itemsList.remove(listItem);
            notifyItemRemoved(index);
        }
    }

    @Override
    public Comment getComment(int position) {
        if (itemsList.size() > position) {
            Object payload = itemsList.get(position).getPayload();
            if (payload instanceof Comment)
                return (Comment) payload;
        }
        return null;
    }

    @Override
    public Comment getComment(iList_Item listItem) {
        if (itemsList.contains(listItem)) {
            iList_Item item = itemsList.get(itemsList.indexOf(listItem));
            Object payload = item.getPayload();
            if (payload instanceof Comment)
                return (Comment) payload;
        }
        return null;
    }


    // Внутренние методы
    private int listSize() {
        return itemsList.size();
    }

    private int maxIndex() {
        return itemsList.size() - 1;
    }
}
