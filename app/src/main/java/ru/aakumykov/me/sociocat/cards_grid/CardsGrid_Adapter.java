package ru.aakumykov.me.sociocat.cards_grid;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

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
        iCardsGrid.iGridView,
        Filterable
{
    private final static String TAG = "CardsGrid_Adapter";

    private List<iGridItem> itemsList = new ArrayList<>();
    private List<iGridItem> originalItemsList = new ArrayList<>();
    private List<iGridItem> filteredItemsList = new ArrayList<>();
    private boolean filterIsEnabled = false;

    private iCardsGrid.iPresenter presenter;
    private iCardsGrid.iGridItemClickListener gridItemClickListener;

    private int fakeIndex = 0;

    public CardsGrid_Adapter(iCardsGrid.iPageView pageView, iCardsGrid.iGridItemClickListener gridItemClickListener) {
//        this.pageView = pageView;
        this.gridItemClickListener = gridItemClickListener;
    }


    // TODO: добавил карточку при неполной загрузке, подгрузил, потом при открытии этой (?) новой падение.


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
            cardViewHolder.bindClickListener(gridItemClickListener);
            cardViewHolder.bindLongClickListener(gridItemClickListener);
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
    public void linkPresenter(iCardsGrid.iPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void unlinkPresenter() {
        this.presenter = null;
    }

    @Override
    public void setList(List<iGridItem> list) {
        clearList();
        addList(list, 0, false, null, false);
    }

    @Override
    public void addList(List<iGridItem> inputList, int position,
                        boolean forceLoadMoreItem, @Nullable Integer positionToScroll, boolean isTemporaryList)
    {
        iGridItem lastExistingItem = getLastContentItem(position);
        iGridItem firstNewItem = (inputList.size()>0) ? inputList.get(0) : null;

        if (null != lastExistingItem && null != firstNewItem) {
            Card lastExistingCard = (Card) lastExistingItem.getPayload();
            Card firstNewCard = (Card) firstNewItem.getPayload();
            if (lastExistingCard.getKey().equals(firstNewCard.getKey()))
                inputList.remove(0);
        }

        this.itemsList.addAll(position, inputList);

        if (!isTemporaryList)
            refreshOriginalItemsList();

        int count = inputList.size();
        notifyItemRangeChanged(position, count);

        showLoadMoreItem(position + count);
    }

    @Override
    public void restoreOriginalList() {
        List<iGridItem> restoredList = new ArrayList<>(this.originalItemsList);
        refreshOriginalItemsList();
        setList(restoredList);
    }

    @Override
    public void addItem(Card card) {
        GridItem_Card cardItem = new GridItem_Card();
        cardItem.setPayload(card);
        itemsList.add(cardItem);
        refreshOriginalItemsList();
        notifyItemChanged(getMaxIndex());
    }

    @Override
    public void addItem(iGridItem gridItem) {
        itemsList.add(gridItem);
        refreshOriginalItemsList();
        notifyItemChanged(getMaxIndex());
    }

    @Override
    public void updateItem(int position, iGridItem newGridItem) {
        if (position > 0) {
            itemsList.set(position, newGridItem);
            refreshOriginalItemsList();
            notifyItemChanged(position);
        }
    }

    @Override
    public void removeItem(iGridItem gridItem) {
        int index = itemsList.indexOf(gridItem);
        itemsList.remove(index);
        refreshOriginalItemsList();
        notifyItemRemoved(index);
    }

    @Override
    public iGridItem getGridItem(int position) {
        return (position >= 0 && position <= getMaxIndex()) ? itemsList.get(position) : null;
    }

    @Override
    public int getItemPosition(iGridItem item) {
        return itemsList.indexOf(item);
    }

    @Override
    public iGridItem getItemBeforeLoadmore(int loadmorePosition) {
        iGridItem loadmoreItem = getGridItem(loadmorePosition);
        // TODO: выбрасывать исключение бы...
        if (loadmoreItem instanceof GridItem_LoadMore)
            return getGridItem(loadmorePosition - 1);
        else
            return null;
    }

    @Override
    public iGridItem getItemAfterLoadmore(int loadmorePosition) {
        iGridItem loadmoreItem = getGridItem(loadmorePosition);
        // TODO: выбрасывать исключение бы...
        if (loadmoreItem instanceof GridItem_LoadMore)
            return getGridItem(loadmorePosition + 1);
        else
            return null;
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
    public void showThrobber(int position) {
        itemsList.add(new GridItem_Throbber());
        notifyItemChanged(position);
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

    @Override
    public void enableFiltering() {
        this.filterIsEnabled = true;
    }

    @Override
    public void disableFiltering() {
        this.filterIsEnabled = false;
    }

    @Override
    public boolean filterIsEnabled() {
        return this.filterIsEnabled;
    }


    // Filterable
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterKey = String.valueOf(constraint).toLowerCase();

                if (filterKey.isEmpty()) {
                    filteredItemsList.clear();
                    filteredItemsList.addAll(originalItemsList);
                }
                else {
                    List<iGridItem> justFilteredItems = new ArrayList<>();

                    List<iGridItem> haystack = new ArrayList<>(originalItemsList);

                    for (iGridItem gridItem : haystack) {
                        if (gridItem instanceof GridItem_Card) {

                            Card card = (Card) gridItem.getPayload();

                            String title = card.getTitle().toLowerCase();

                            if (title.contains(filterKey))
                                justFilteredItems.add(gridItem);
                        }
                    }

                    filteredItemsList.clear();
                    filteredItemsList.addAll(justFilteredItems);
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredItemsList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (filterIsEnabled) {
                    List<iGridItem> resultsList = (ArrayList<iGridItem>) results.values;
//                    Log.d(TAG, "results count: "+resultsList.size());
                    updateList(resultsList);
                }
            }
        };
    }


    // Внутренние методы
    private void showLoadMoreItem(int position) {
        GridItem_LoadMore loadMoreItem = new GridItem_LoadMore();
        itemsList.add(position, loadMoreItem);
//        notifyItemChanged(position);
        notifyItemInserted(position);
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

    private iGridItem getLastContentItem(int bottomBorder) {
        if (0 == itemsList.size())
            return null;

        bottomBorder -= 1;

        iGridItem gridItem ;

        for (int i=bottomBorder; i>=0; i--) {
            gridItem = itemsList.get(i);
            if (gridItem instanceof GridItem_Card)
                return gridItem;
        }
        return null;
    }

    private void refreshOriginalItemsList() {
        originalItemsList.clear();
        originalItemsList.addAll(itemsList);
    }

    private void updateList(List<iGridItem> newItemsList) {

        hideLoadMoreItem(getMaxIndex());

        int oldItemsCount = getItemCount();
        int newItemsCount = newItemsList.size();
        int listsSizeDifference = oldItemsCount - newItemsCount;

        /*Log.d(TAG, " ======> oldItemsCount: "+oldItemsCount+", newItemsCount: "+
                newItemsCount+", listsSizeDifference: "+listsSizeDifference);*/

        if (listsSizeDifference >= 0) {
            for (int i = 0; i < newItemsCount; i++) {
                iGridItem newItem = newItemsList.get(i);
                itemsList.set(i, newItem);
                notifyItemChanged(i, newItem);
            }

            for (int i = oldItemsCount - 1; i >= newItemsCount; i--) {
                iGridItem oldItem = itemsList.get(i);
                itemsList.remove(i);
                notifyItemRemoved(i);
            }
        }
        else {
            for (int i=0; i<itemsList.size(); i++) {
                iGridItem newItem = newItemsList.get(i);
                itemsList.set(i, newItem);
                notifyItemChanged(i, newItem);
            }

            for (int i=itemsList.size(); i<newItemsCount; i++) {
                iGridItem newItem = newItemsList.get(i);
                itemsList.add(newItem);
                notifyItemChanged(i, newItem);
            }
        }

        showLoadMoreItem(newItemsCount);
    }
}
