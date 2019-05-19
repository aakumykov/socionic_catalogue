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
import ru.aakumykov.me.sociocat.card_show.view_holders.Card_ViewHolder;
import ru.aakumykov.me.sociocat.card_show.view_holders.Comment_ViewHolder;
import ru.aakumykov.me.sociocat.card_show.view_holders.CommentsThrobber_ViewHolder;
import ru.aakumykov.me.sociocat.card_show.view_holders.LoadMore_ViewHolder;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.models.Item;

public class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        iListAdapter,
        iListAdapter_Card,
        iListAdapter_Comments
{
    private final static String TAG = "ListAdapter";
    private List<Item> itemsList;
    private CardPresenter cardPresenter;
    private CommentsPresenter commentsPresenter;

    public ListAdapter() {
        this.itemsList = new ArrayList<>();
    }


    // RecyclerView
    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @NonNull @Override
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Item item = itemsList.get(position);

        switch (viewHolder.getItemViewType()) {

            case Item.CARD_VIEW_TYPE:
                ((Card_ViewHolder) viewHolder).initialize((Card) item);
                break;

            case Item.COMMENT_VIEW_TYPE:
                ((Comment_ViewHolder) viewHolder).initialize((Comment) item);
                break;

            case Item.LOAD_MORE_VIEW_TYPE:
//                Comment lastComment = getLastComment();
//                ((LoadMore_ViewHolder) viewHolder).initialize(lastComment);

            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 0;
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

    }

    @Override
    public void hideCardThrobber() {

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

        if (0 == itemsList.size())
            itemsList.add(card);
        else
            itemsList.set(cardPosition, card);

        notifyItemChanged(cardPosition);
    }


    // iListAdapter_Comments
    @Override
    public void showCommentsThrobber() {

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
    }

    @Override
    public void addList(List<Comment> list) {
        int start = itemsList.size();
        int count = list.size();

        itemsList.addAll(list);
        notifyItemRangeChanged(start, count);
    }
}
