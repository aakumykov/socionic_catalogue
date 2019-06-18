package ru.aakumykov.me.sociocat.cards_grid_3;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.Config;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_grid_3.items.GridItem_Card;
import ru.aakumykov.me.sociocat.cards_grid_3.items.GridItem_LoadMore;
import ru.aakumykov.me.sociocat.cards_grid_3.items.GridItem_Throbber;
import ru.aakumykov.me.sociocat.cards_grid_3.items.iGridItem;
import ru.aakumykov.me.sociocat.cards_grid_3.view_holders.Card_ViewHolder;
import ru.aakumykov.me.sociocat.cards_grid_3.view_holders.LoadMore_ViewHolder;
import ru.aakumykov.me.sociocat.cards_grid_3.view_holders.Throbber_ViewHolder;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iAuthSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;

public class CG3_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        iCG3.iGridView
{
    private List<iGridItem> itemsList = new ArrayList<>();
    private iCG3.iPresenter presenter;
    private iAuthSingleton authSingleton = AuthSingleton.getInstance();
    private iUsersSingleton usersSingleton = UsersSingleton.getInstance();

    private Drawable originalBackground;


    // Системные методы
    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView;
        RecyclerView.ViewHolder viewHolder;
        StaggeredGridLayoutManager.LayoutParams layoutParams;

        switch (viewType) {

            case iGridItem.LOAD_MORE_VIEW_TYPE:
                itemView = layoutInflater.inflate(R.layout.cg3_loadmore_item, parent, false);
                viewHolder = new LoadMore_ViewHolder(itemView, presenter);

                layoutParams = (StaggeredGridLayoutManager.LayoutParams) viewHolder.itemView.getLayoutParams();
                layoutParams.setFullSpan(true);
                break;

            case iGridItem.THROBBER_VIEW_TYPE:
                itemView = layoutInflater.inflate(R.layout.cg3_throbber_item, parent, false);
                viewHolder = new Throbber_ViewHolder(itemView);

                layoutParams = (StaggeredGridLayoutManager.LayoutParams) viewHolder.itemView.getLayoutParams();
                layoutParams.setFullSpan(true);
                break;

            case iGridItem.TEXT_CARD_VIEW_TYPE:
                itemView = layoutInflater.inflate(R.layout.cg3_text_card_item, parent, false);
                viewHolder = new Card_ViewHolder(itemView, presenter);
                break;

            case iGridItem.IMAGE_CARD_VIEW_TYPE:
                itemView = layoutInflater.inflate(R.layout.cg3_image_card_item, parent, false);
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

            default:
                throw new RuntimeException("Unknown item view type: "+viewType);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        iGridItem gridItem = itemsList.get(position);

        Object payload = gridItem.getPayload();

        if (gridItem instanceof GridItem_Card) {
            Card_ViewHolder cardViewHolder = (Card_ViewHolder) viewHolder;
            cardViewHolder.initialize(gridItem, position, payload);
        }
        else if (gridItem instanceof GridItem_LoadMore) {
            LoadMore_ViewHolder loadMoreViewHolder = (LoadMore_ViewHolder) viewHolder;
            loadMoreViewHolder.initialize(position, payload);
        }
        else if (gridItem instanceof GridItem_Throbber) {
            Throbber_ViewHolder throbberViewHolder = (Throbber_ViewHolder) viewHolder;
        }
        else {
            throw new RuntimeException("Unknown item type: "+gridItem);
        }

    }

    @Override
    public int getItemViewType(int position) {
        iGridItem item = itemsList.get(position);

        if (item instanceof GridItem_Card) {
            Card card = (Card) item.getPayload();
            if (card.isTextCard())  return iGridItem.TEXT_CARD_VIEW_TYPE;
            if (card.isImageCard()) return iGridItem.IMAGE_CARD_VIEW_TYPE;
            if (card.isAudioCard()) return iGridItem.AUDIO_CARD_VIEW_TYPE;
            if (card.isVideoCard()) return iGridItem.VIDEO_CARD_VIEW_TYPE;
            else return -1;
        }
        else if (item instanceof GridItem_LoadMore)
            return iGridItem.LOAD_MORE_VIEW_TYPE;
        else if (item instanceof GridItem_Throbber)
            return iGridItem.THROBBER_VIEW_TYPE;
        else
            return -1;
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
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
    public void setList(List<iGridItem> list) {
        this.itemsList.clear();
        appendList(list);
    }

    @Override
    public void appendList(List<iGridItem> list) {

        Card lastCard = null;

        if (list.size() > Config.DEFAULT_CARDS_LOAD_COUNT) {
            int maxIndex = list.size() - 1;
            lastCard = (Card) list.get(maxIndex).getPayload();
            list.remove(maxIndex);
        }

        int start = this.itemsList.size();
        int count = list.size();
        this.itemsList.addAll(list);
        notifyItemRangeChanged(start, count);

        showLoadMoreItem(lastCard);
    }

    @Override
    public iGridItem getItem(int position) {
        return itemsList.get(position);
    }

    @Override
    public void hideLoadMoreItem(int position) {
        iGridItem gridItem = itemsList.get(position);
        if (gridItem instanceof GridItem_LoadMore) {
            itemsList.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public void showThrobber() {
        itemsList.add(new GridItem_Throbber());
        int index = itemsList.size() - 1;
        notifyItemChanged(index);
    }

    @Override
    public void showThrobber(int position) {
        itemsList.remove(position);
        itemsList.add(new GridItem_Throbber());
        notifyItemChanged(position);
    }

    @Override
    public void hideThrobber() {
        int index = itemsList.size() - 1;
        hideThrobber(index);
    }

    @Override
    public void hideThrobber(int position) {
        itemsList.remove(position);
        notifyItemChanged(position);
    }

    @Override
    public void showPopupMenu(int mode, View view, int position) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);

//        if (mode >= 10)
//            popupMenu.inflate();

        if (mode >= 20)
            popupMenu.inflate(R.menu.edit);

        if (mode >= 100)
            popupMenu.inflate(R.menu.delete);

        popupMenu.inflate(R.menu.share);


        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                popupItemClicked(item, position);
                return true;
            }
        });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                unfadeItem(position);
            }
        });

        fadeItem(position);
        popupMenu.show();
    }


    // Внутренние методы
    private void showLoadMoreItem(@Nullable Card card) {
        if (null != card) {
            GridItem_LoadMore loadMoreItem = new GridItem_LoadMore();
            loadMoreItem.setPayload(card);
            itemsList.add(loadMoreItem);
            notifyItemChanged(itemsList.size());
        }
    }

    private void fadeItem(int position) {
        iGridItem fadedGridItem = itemsList.get(position);
        fadedGridItem.setIsPressed(true);

        itemsList.set(position, fadedGridItem); // TODO: нужно?
        notifyItemChanged(position);
    }

    private void unfadeItem(int position) {
        iGridItem fadedGridItem = itemsList.get(position);
        fadedGridItem.setIsPressed(false);

        itemsList.set(position, fadedGridItem); // TODO: нужно?
        notifyItemChanged(position);
    }

    private void popupItemClicked(MenuItem menuItem, int position) {

        iGridItem gridItem = itemsList.get(position);

        switch (menuItem.getItemId()) {
            case R.id.actionEdit:
                presenter.onEditClicked(gridItem);
                break;
            case R.id.actionDelete:
                presenter.onDeleteClicked(gridItem);
                break;
            case R.id.actionShare:
                presenter.onShareClicked(gridItem);
                break;
        }
    }
}
