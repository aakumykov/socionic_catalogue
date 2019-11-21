package ru.aakumykov.me.sociocat.card_show2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show2.list_items.Card_Item;
import ru.aakumykov.me.sociocat.card_show2.list_items.Comment_Item;
import ru.aakumykov.me.sociocat.card_show2.list_items.LoadMore_Item;
import ru.aakumykov.me.sociocat.card_show2.list_items.iList_Item;
import ru.aakumykov.me.sociocat.card_show2.view_holders.Card_ViewHolder;
import ru.aakumykov.me.sociocat.card_show2.view_holders.Comment_ViewHolder;
import ru.aakumykov.me.sociocat.card_show2.view_holders.LoadMore_ViewHolder;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;

public class DataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
    iCardShow2.iDataAdapter
{
    private final static int CARD_INDEX = 0;
    private final static int MINIMAL_LIST_SIZE = 3; // Минимальный размер списка: карточка + кнопка списка комментариев
//    private List<iList_Item> itemsList = new ArrayList<>();
    private List<iList_Item> itemsList;
    private iCardShow2.iPresenter presenter;


    DataAdapter(iCardShow2.iPresenter presenter) {
        this.itemsList = new ArrayList<>();
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
                return new Card_ViewHolder(itemView);

            case iList_Item.COMMENT:
                itemView = layoutInflater.inflate(R.layout.card_show_comment, parent, false);
                return new Comment_ViewHolder(itemView);

            case iList_Item.LOAD_MORE:
                itemView = layoutInflater.inflate(R.layout.card_show_load_more, parent, false);
                return new LoadMore_ViewHolder(itemView, presenter);

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
                ((Comment_ViewHolder) holder).initialize(listItem);
                break;

            case iList_Item.LOAD_MORE:
                ((LoadMore_ViewHolder) holder).initialize(listItem);
                break;

            default:
                throw new RuntimeException("Unknown item type: "+itemType);
        }
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }


    @Override
    public void setCard(Card card) {
        iList_Item listItem = new Card_Item(card);

        if (0 == itemsList.size())
            itemsList.add(listItem);
        else {
            itemsList.set(CARD_INDEX, listItem);
        }

        notifyItemChanged(CARD_INDEX);
    }

    @Override
    public void setComments(List<Comment> commentsList) {

        // Сохраняю карточку
        iList_Item cardItem = itemsList.get(CARD_INDEX);

        // Очищаю список
        itemsList = new ArrayList<>();

        // Возвращаю карточку в список
        setCard((Card) cardItem.getPayload());

        // Добавляю комментарии
        for (Comment comment : commentsList) {
            itemsList.add(new Comment_Item(comment));
        }

        // Добавляю кнопку под комментариями
        int loadMoreButtonTextId =
                (0 == commentsList.size()) ?
                R.string.COMMENTS_there_is_no_comments_yet :
                R.string.COMMENTS_load_more_comments;

        itemsList.add(new LoadMore_Item(loadMoreButtonTextId));

        // Уведомляю об изменившемся списке
        notifyDataSetChanged();
    }

    @Override
    public void appendComments(List<Comment> commentsList) {
        int firstIndex = itemsList.size()-1;

        for (Comment comment : commentsList)
            itemsList.add(itemsList.size()-1, new Comment_Item(comment));

        int loadMoreButtonTextId = (0 == commentsList.size()) ?
                R.string.COMMENTS_no_more_comments :
                R.string.COMMENTS_load_more_comments;

        itemsList.set(itemsList.size()-1, new LoadMore_Item(loadMoreButtonTextId));

        notifyItemRangeChanged(firstIndex, commentsList.size()+1);
    }

    @Override
    public Comment getLastComment() {
        if (itemsList.size() > MINIMAL_LIST_SIZE) {
            Comment_Item commentItem = (Comment_Item) itemsList.get(itemsList.size()-2);
            return (Comment) commentItem.getPayload();
        }
        else {
            return null;
        }
    }
}
