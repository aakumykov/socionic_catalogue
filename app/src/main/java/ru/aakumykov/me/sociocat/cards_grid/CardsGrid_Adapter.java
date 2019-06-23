package ru.aakumykov.me.sociocat.cards_grid;

import android.util.Log;
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
import ru.aakumykov.me.sociocat.cards_grid.items.GridItem_Card;
import ru.aakumykov.me.sociocat.cards_grid.items.GridItem_LoadMore;
import ru.aakumykov.me.sociocat.cards_grid.items.GridItem_Throbber;
import ru.aakumykov.me.sociocat.cards_grid.items.iGridItem;
import ru.aakumykov.me.sociocat.cards_grid.view_holders.Card_ViewHolder;
import ru.aakumykov.me.sociocat.cards_grid.view_holders.LoadMore_ViewHolder;
import ru.aakumykov.me.sociocat.cards_grid.view_holders.Throbber_ViewHolder;
import ru.aakumykov.me.sociocat.cards_grid.view_holders.iGridViewHolder;
import ru.aakumykov.me.sociocat.models.Card;

public class CardsGrid_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        iCardsGrig.iGridView
{
    private final static String TAG = "CardsGrid_Adapter";
    private List<iGridItem> itemsList = new ArrayList<>();
    private iCardsGrig.iPresenter presenter;
    private iCardsGrig.iPageView pageView;

    public CardsGrid_Adapter(iCardsGrig.iPageView pageView) {
        this.pageView = pageView;
    }


    // Системные методы
    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView;
        RecyclerView.ViewHolder viewHolder;
        StaggeredGridLayoutManager.LayoutParams layoutParams;

        switch (viewType) {

            case iGridItem.LOAD_MORE_VIEW_TYPE:
                itemView = layoutInflater.inflate(R.layout.cards_grid_loadmore_item, parent, false);
                viewHolder = new LoadMore_ViewHolder(itemView, presenter);

                layoutParams = (StaggeredGridLayoutManager.LayoutParams) viewHolder.itemView.getLayoutParams();
                layoutParams.setFullSpan(true);
                break;

            case iGridItem.THROBBER_VIEW_TYPE:
                itemView = layoutInflater.inflate(R.layout.cards_grid_throbber_item, parent, false);
                viewHolder = new Throbber_ViewHolder(itemView);

                layoutParams = (StaggeredGridLayoutManager.LayoutParams) viewHolder.itemView.getLayoutParams();
                layoutParams.setFullSpan(true);
                break;

            case iGridItem.TEXT_CARD_VIEW_TYPE:
                itemView = layoutInflater.inflate(R.layout.cards_grid_text_card_item, parent, false);
                viewHolder = new Card_ViewHolder(itemView, presenter);
                break;

            case iGridItem.IMAGE_CARD_VIEW_TYPE:
                itemView = layoutInflater.inflate(R.layout.cards_grid_image_card_item, parent, false);
                viewHolder = new Card_ViewHolder(itemView, presenter);
                break;

            case iGridItem.AUDIO_CARD_VIEW_TYPE:
                itemView = layoutInflater.inflate(R.layout.cards_grid_audio_card_item, parent, false);
                viewHolder = new Card_ViewHolder(itemView, presenter);
                break;

            case iGridItem.VIDEO_CARD_VIEW_TYPE:
                itemView = layoutInflater.inflate(R.layout.cards_grid_video_card_item, parent, false);
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
    public void linkPresenter(iCardsGrig.iPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void unlinkPresenter() {
        this.presenter = null;
    }

    @Override
    public void setList(List<iGridItem> list) {
//        Log.d(TAG, "setList(), list size: "+list.size()+", itemsList size: "+itemsList.size());
        clearList();
        appendList(list, false, null);
    }

    @Override
    public void restoreList(List<iGridItem> inputList, @Nullable Integer scrollToPosition) {
        clearList();
        appendList(inputList, true, scrollToPosition);
    }

    @Override
    public void appendList(List<iGridItem> inputList, boolean forceLoadMoreItem, @Nullable Integer positionToScroll) {
        //Log.d(TAG, "appendList(), list size: "+list.size()+", itemsList size: "+itemsList.size()+", forceLoadMoreItem: "+forceLoadMoreItem);

        boolean loadMoreExpected = inputList.size() == Config.DEFAULT_CARDS_LOAD_COUNT;

        // Удаляю повтор карточки на стыке списков
        iGridItem lastContentItem = getLastContentItem();
        if (null != lastContentItem && inputList.size() > 0) {
            Card lastExistingCard = (Card) lastContentItem.getPayload();
            Card firstNewCard = (Card) inputList.get(0).getPayload();
            if (lastExistingCard.getKey().equals(firstNewCard.getKey()))
                inputList.remove(0);
        }

        // Добавляю новый список к старому
        int start = this.itemsList.size();
        int count = inputList.size();
        this.itemsList.addAll(inputList);
        notifyItemRangeChanged(start, count);

        if (loadMoreExpected || forceLoadMoreItem)
            showLoadMoreItem();

        if (null != positionToScroll)
            pageView.scrollToPosition(positionToScroll);
    }

    @Override
    public iGridItem getItem(int position) {
        return itemsList.get(position);
    }

    @Override
    public iGridItem getLastContentItem() {
        int index = getMaxIndex();

        if (index < 0)
            return null;

        iGridItem gridItem = itemsList.get(index);

        while (!(gridItem instanceof GridItem_Card))
            gridItem = itemsList.get(--index);

        return gridItem;
    }

    @Override
    public int getItemPosition(iGridItem item) {
        return itemsList.indexOf(item);
    }

    @Override
    public void updateItem(int position, iGridItem newGridItem) {
        if (position > 0) {
            itemsList.set(position, newGridItem);
            notifyItemChanged(position);
        }
    }

    @Override
    public void removeItem(iGridItem gridItem) {
        int index = itemsList.indexOf(gridItem);
        itemsList.remove(index);
        notifyItemRemoved(index);
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
        int index = getMaxIndex();
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
        int index = getMaxIndex();
        hideThrobber(index);
    }

    @Override
    public void hideThrobber(int position) {
        itemsList.remove(position);
        notifyItemChanged(position);
    }

    @Override
    public void showPopupMenu(int mode, int position, View view, iGridViewHolder gridViewHolder) {
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
                onPopupItemClicked(item, position);
                return true;
            }
        });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                gridViewHolder.unfade();
            }
        });

        gridViewHolder.fade();
        popupMenu.show();
    }


    // Внутренние методы
    private void showLoadMoreItem() {
        GridItem_LoadMore loadMoreItem = new GridItem_LoadMore();
        itemsList.add(loadMoreItem);
        int index = getMaxIndex();
        notifyItemChanged(index);
    }

    private void onPopupItemClicked(MenuItem menuItem, int position) {

        iGridItem gridItem = itemsList.get(position);

        switch (menuItem.getItemId()) {
            case R.id.actionEdit:
                presenter.onEditCardClicked(gridItem);
                break;
            case R.id.actionDelete:
                presenter.onDeleteCardClicked(gridItem);
                break;
            case R.id.actionShare:
                presenter.onShareCardClicked(gridItem);
                break;
        }
    }

    private int getMaxIndex() {
        return itemsList.size() - 1;
    }

    private void clearList() {
        int start = 0;
        int count = itemsList.size();
        itemsList.clear();
        notifyItemRangeRemoved(start, count);
    }
}
