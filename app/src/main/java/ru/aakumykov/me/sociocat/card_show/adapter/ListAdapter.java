package ru.aakumykov.me.sociocat.card_show.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.presenters.CardPresenter;
import ru.aakumykov.me.sociocat.card_show.presenters.CommentsPresenter;
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
        iListAdapter_Card,
        iListAdapter_Comments
{
    private final static String TAG = "ListAdapter";
    private List<ListItem> list;
    private CardPresenter cardPresenter;
    private CommentsPresenter commentsPresenter;

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
                return new Card_ViewHolder(itemView);

            case ListItem.COMMENT_VIEW_TYPE:
                itemView = layoutInflater.inflate(R.layout.card_show_comment, parent, false);
                return new Comment_ViewHolder(itemView);

            case ListItem.THROBBER_VIEW_TYPE:
                itemView = layoutInflater.inflate(R.layout.card_show_throbber, parent, false);
                return new Throbber_ViewHolder(itemView);

            case ListItem.LOAD_MORE_VIEW_TYPE:
                itemView = layoutInflater.inflate(R.layout.card_show_load_more, parent, false);
                return new LoadMore_ViewHolder(itemView);

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
//                Comment lastComment = getLastComment();
//                ((LoadMore_ViewHolder) viewHolder).initialize(lastComment);

            default:
                break;
        }
    }


    // iListAdapter
    @Override
    public void bindPresenters(iCardPresenter cardPresenter, iCommentsPresenter commentPresenter) {

    }

    @Override
    public void unbindPresenters() {

    }


    // iListAdapter_Card
    @Override
    public void showCardThrobber() {
        int index = 0;
        list.add(index, new Throbber_Item(R.string.CARD_SHOW_loading_card));
        notifyItemChanged(index);
    }

    @Override
    public void hideCardThrobber() {
        removeItemIfType(ListItem.ItemType.CARD_THROBBER_ITEM, 0);
    }

    @Override
    public void showCardError(int errorMsgId, String consoleErrorMsg) {

    }

    @Override
    public void hideCardError() {

    }


    @Override
    public void setCard(Card card) {
        int cardPosition = 0;

        if (0 == list.size())
            list.add(card);
        else
            list.set(cardPosition, card);

        notifyItemChanged(cardPosition);
    }


    // iListAdapter_Comments
    @Override
    public void showCommentsThrobber() {
        list.add(new Throbber_Item(R.string.COMMENTS_loading_comments));
    }

    @Override
    public void hideCommentsThrobber() {

    }

    @Override
    public void showCommentsError(int errorMsgId, String consoleErrorMsg) {

    }

    @Override
    public void hideCommentsError() {

    }

    @Override
    public void setList(List<Comment> itemsList) {
        if (itemsList.size() >= 1) {
            itemsList.subList(1, itemsList.size() + 1).clear();
        }
//        notifyItemRangeChanged();
        notifyDataSetChanged();
    }

    @Override
    public void addList(List<Comment> list) {
        int start = this.list.size();
        int count = list.size();

        this.list.addAll(list);
        notifyItemRangeChanged(start, count);
    }


    // Внутренние методы
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
