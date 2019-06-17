package ru.aakumykov.me.sociocat.cards_grid_3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_grid_3.items.LoadMore_Item;
import ru.aakumykov.me.sociocat.cards_grid_3.items.Throbber_Item;
import ru.aakumykov.me.sociocat.cards_grid_3.items.iGridItem;
import ru.aakumykov.me.sociocat.cards_grid_3.view_holders.Card_ViewHolder;
import ru.aakumykov.me.sociocat.cards_grid_3.view_holders.LoadMore_ViewHolder;
import ru.aakumykov.me.sociocat.cards_grid_3.view_holders.Throbber_ViewHolder;
import ru.aakumykov.me.sociocat.models.Card;

public class CG3_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        iCG3.iGridView
{
    private List<iGridItem> list = new ArrayList<>();
    private iCG3.iPresenter presenter;

    // Системные методы
    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView;
        RecyclerView.ViewHolder viewHolder;
        StaggeredGridLayoutManager.LayoutParams layoutParams;

        switch (viewType) {

            case iGridItem.TEXT_CARD_VIEW_TYPE:
                itemView = layoutInflater.inflate(R.layout.cg3_text_card_item, parent, false);
                viewHolder = new Card_ViewHolder(itemView, presenter);
                break;

            case iGridItem.IMAGE_CARD_VIEW_TYPE:
                itemView = layoutInflater.inflate(R.layout.cg3_tile_image, parent, false);
                viewHolder = new Card_ViewHolder(itemView, presenter);
                break;

            case iGridItem.AUDIO_CARD_VIEW_TYPE:
                itemView = layoutInflater.inflate(R.layout.cg3_audio_card_item, parent, false);
                viewHolder = new Card_ViewHolder(itemView, presenter);
                break;

            case iGridItem.VIDEO_CARD_VIEW_TYPE:
                itemView = layoutInflater.inflate(R.layout.cg3_video_card_item, parent, false);
                viewHolder = new Card_ViewHolder(itemView, presenter);
                break;

            case iGridItem.LOAD_MORE_VIEW_TYPE:
                itemView = layoutInflater.inflate(R.layout.cg3_loadmore_item, parent, false);
                viewHolder = new LoadMore_ViewHolder(itemView);

                layoutParams = (StaggeredGridLayoutManager.LayoutParams) viewHolder.itemView.getLayoutParams();
                layoutParams.setFullSpan(true);
                break;

            case iGridItem.THROBBER_VIEW_TYPE:
                itemView = layoutInflater.inflate(R.layout.cg3_throbber_item, parent, false);
                viewHolder = new Throbber_ViewHolder(itemView);

                layoutParams = (StaggeredGridLayoutManager.LayoutParams) viewHolder.itemView.getLayoutParams();
                layoutParams.setFullSpan(true);
                break;

            default:
                throw new RuntimeException("Unknown item view type: "+viewType);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        iGridItem item = list.get(position);

        if (item instanceof Card) {
            Card card = (Card) item;
            Card_ViewHolder gridItemViewHolder = (Card_ViewHolder) viewHolder;
            gridItemViewHolder.initialize(card, position);
        }
        else if (item instanceof LoadMore_Item) {
            LoadMore_ViewHolder loadMoreViewHolder = (LoadMore_ViewHolder) viewHolder;
            loadMoreViewHolder.initialize();
        }
        else if (item instanceof Throbber_Item) {
            Throbber_ViewHolder throbberViewHolder = (Throbber_ViewHolder) viewHolder;
        }
        else {
            throw new RuntimeException("Unknown item type: "+item);
        }

    }

    @Override
    public int getItemViewType(int position) {
        iGridItem item = list.get(position);

        if (item instanceof Card) {
            Card card = (Card) item;
            if (card.isTextCard())  return iGridItem.TEXT_CARD_VIEW_TYPE;
            if (card.isImageCard()) return iGridItem.IMAGE_CARD_VIEW_TYPE;
            if (card.isAudioCard()) return iGridItem.AUDIO_CARD_VIEW_TYPE;
            if (card.isVideoCard()) return iGridItem.VIDEO_CARD_VIEW_TYPE;
            else return -1;
        }
        else if (item instanceof LoadMore_Item)
            return iGridItem.LOAD_MORE_VIEW_TYPE;
        else if (item instanceof Throbber_Item)
            return iGridItem.THROBBER_VIEW_TYPE;
        else
            return -1;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    // iGridView
    @Override
    public void linkPresenter(iCG3.iPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void unlinkPresenter() {
        this.presenter = null;
    }

    @Override
    public void setList(List<iGridItem> itemsList) {
        this.list.clear();
        this.list.addAll(itemsList);
        this.list.add(new LoadMore_Item());
        notifyDataSetChanged();
    }

    @Override
    public void appendList(List<iGridItem> list) {

    }

    @Override
    public void addItem(iGridItem item) {

    }

    @Override
    public void removeItem(iGridItem item) {

    }

    @Override
    public void updateItem(iGridItem item) {

    }

    @Override
    public iGridItem getItem(int position) {
        return list.get(position);
    }
}
