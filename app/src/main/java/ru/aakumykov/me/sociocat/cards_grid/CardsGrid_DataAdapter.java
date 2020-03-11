package ru.aakumykov.me.sociocat.cards_grid;

import android.text.TextUtils;
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
import java.util.HashMap;
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

public class CardsGrid_DataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        iCardsGrid.iDataAdapter
{
    private final static String TAG = "CardsGrid_DataAdapter";

    private List<iGridItem> itemsList = new ArrayList<>();
    private List<iGridItem> originalItemsList = new ArrayList<>();
    private List<iGridItem> filteredItemsList = new ArrayList<>();
    private boolean filterIsEnabled = false;

    private iCardsGrid.iPresenter presenter;
    private iCardsGrid.iPageView pageView;
    private iCardsGrid.iGridItemClickListener gridItemClickListener;
    private iCardsGrid.iLoadMoreClickListener loadMoreClickListener;


    public CardsGrid_DataAdapter(iCardsGrid.iPageView pageView,
                                 iCardsGrid.iGridItemClickListener gridItemClickListener,
                                 iCardsGrid.iLoadMoreClickListener loadMoreClickListener
    ) {
        this.pageView = pageView;
        this.gridItemClickListener = gridItemClickListener;
        this.loadMoreClickListener = loadMoreClickListener;
    }


    // TODO: добавил карточку при неполной загрузке, подгрузил, потом при открытии этой (?) новой падение.


    // Системные методы RecyclerView
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
            loadMoreViewHolder.initialize(position, gridItem);
            loadMoreViewHolder.bindClickListener(loadMoreClickListener);
        }
        else if (gridItem instanceof GridItem_Throbber) {
            Throbber_ViewHolder throbberViewHolder = (Throbber_ViewHolder) viewHolder;
        }
        else {
            throw new RuntimeException("Unknown item type: "+gridItem);
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
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


    // iDataAdapter
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
        insertList(0, list);
        append_LoadMore_Item();
    }

    @Override
    public void insertList(int position, List<iGridItem> list) {
        itemsList.addAll(position, list);
        notifyItemRangeChanged(position, list.size());
    }

    @Override
    public void insertItem(int position, iGridItem gridItem) {
        itemsList.add(position, gridItem);
        notifyItemInserted(position);
    }

    @Override
    public void addList(List<iGridItem> inputList,
                        int position,
                        boolean forceLoadMoreItem,
                        @Nullable Integer positionToScroll
    ) {
        // Удаляю дубликаты на стыке двух списков
        iGridItem lastExistingItem = getLastContentItem();
        iGridItem firstNewItem = (inputList.size()>0) ? inputList.get(0) : null;

        if (null != lastExistingItem && null != firstNewItem) {
            Card lastExistingCard = (Card) lastExistingItem.getPayload();
            Card firstNewCard = (Card) firstNewItem.getPayload();
            if (lastExistingCard.getKey().equals(firstNewCard.getKey()))
                inputList.remove(0);
        }

        // Присоединяю добавляемый список к существующему
        originalItemsList.addAll(inputList);

        // Фильтрую список перед демонстрацией
        List<iGridItem> filteredList = filterList(inputList);
        int filteredItemsCount = filteredList.size();
        itemsList.addAll(position, filteredList);
        notifyItemRangeInserted(position, filteredItemsCount);

        showLoadMoreItem(position + filteredItemsCount, inputList);
    }

    @Override
    public void restoreOriginalList() {
        List<iGridItem> restoredList = new ArrayList<>(this.originalItemsList);

        setList(restoredList);
    }

    @Override
    public void removeItem(iGridItem gridItem) {
        int index = itemsList.indexOf(gridItem);
        itemsList.remove(index);
        synchronizeOriginalItemsList();
        notifyItemRemoved(index);
    }

    @Override
    public void removeItem(int position) {
        itemsList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void updateItem(int position, Card card) {
        iGridItem gridItem = new GridItem_Card();
        gridItem.setPayload(card);
        itemsList.set(position, gridItem);
        notifyItemChanged(position);
    }

    @Override
    public iGridItem getGridItem(int position) {
        return (position >= 0 && position <= getMaxIndex()) ? itemsList.get(position) : null;
    }

    @Override
    public iGridItem getGridItem(@NonNull Card searchedCard) {
        for (iGridItem gridItem : itemsList) {
            Object object = gridItem.getPayload();
            if (object instanceof Card) {
                Card checkedCard = (Card) object;
                if (checkedCard.getKey().equals(searchedCard.getKey()))
                    return gridItem;
            }
        }
        return null;
    }

    @Override
    public int getItemPosition(iGridItem item) {
        return itemsList.indexOf(item);
    }

    @Override
    public GridItem_Card getLastCardItem() {
        if (0 == itemsList.size())
            return null;

        int subtractedNumber = (itemsList.size() < 2) ? 1 : 2;

        iGridItem gridItem = itemsList.get(itemsList.size() - subtractedNumber);

        return (gridItem instanceof GridItem_Card) ? (GridItem_Card) gridItem : null;
    }

    @Override
    public GridItem_Card getFirstCardItem() {
        if (0 == itemsList.size())
            return null;

        iGridItem gridItem = itemsList.get(0);

        return (gridItem instanceof GridItem_Card) ? (GridItem_Card) gridItem : null;
    }

    @Override
    public List<iGridItem> getList() {
        return itemsList;
    }

    @Override
    public void showLoadMoreItem() {
        GridItem_LoadMore itemLoadMore = new GridItem_LoadMore(R.string.CARDS_GRID_load_old, true);
        itemsList.add(itemLoadMore);
        notifyItemInserted(itemsList.size());
    }

    @Override
    public void hideLoadMoreItem(int position) {
        iGridItem gridItem = itemsList.get(position);
        if (gridItem instanceof GridItem_LoadMore) {
            itemsList.remove(position);
            notifyItemRemoved(position);
        }
    }

    private void hideCheckingNewCardsThrobber() {

        iGridItem gridItem = getGridItem(0);

        if (gridItem instanceof GridItem_Throbber) {
            itemsList.remove(0);
            notifyItemRemoved(0);
        }
    }

    @Override
    public void addNewCards(List<iGridItem> gridItemsList, @Nullable Card newCardsBoundaryCard) {

        hideCheckingNewCardsThrobber();

        // Удаляю элементы, добавленные позже сообщения о новых карточках
        if (null != newCardsBoundaryCard) {
            iGridItem newCardsBoundaryElement = getGridItem(newCardsBoundaryCard);
            int boundaryIndex = itemsList.indexOf(newCardsBoundaryElement);
            List<iGridItem> list2delete = itemsList.subList(0, boundaryIndex);
            itemsList.removeAll(list2delete);
        }

        insertList(0, gridItemsList);
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

        popupMenu.inflate(R.menu.share);

        if (mode >= 20)
            popupMenu.inflate(R.menu.edit);

        if (mode >= 100)
            popupMenu.inflate(R.menu.delete);


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

    @Override
    public void applyFilterToGrid(String filterKey) {
        pageView.showToast("Не реализовано");
        /*List<iGridItem> filteredList = presenter.filterList(originalItemsList);

        itemsList.clear();
        itemsList.addAll(filteredList);
        notifyDataSetChanged();

        showLoadMoreItem(itemsList.size(), itemsList);*/
    }

    @Override
    public boolean hasData() {
        return itemsList.size() > 0;
    }


    // Внутренние методы
    private List<iGridItem> filterList(final List<iGridItem> inputList) {
        String filterWord = pageView.getCurrentFilterWord();
        String filterTag = pageView.getCurrentFilterTag();

        List<iGridItem> resultsList = new ArrayList<>(inputList);

        if (!TextUtils.isEmpty(filterWord))
            resultsList = filterCardsByTitle(filterWord, inputList);

        if (!TextUtils.isEmpty(filterTag))
            resultsList = filterCardsByTag(filterTag, inputList);

        return resultsList;
    }

    private List<iGridItem> filterCardsByTitle(@Nullable String filterWord, final List<iGridItem> inputList) {

        List<iGridItem> resultsList = new ArrayList<>(inputList);

        if (null != filterWord) {
            filterWord = filterWord.toLowerCase();

            for (iGridItem item : inputList) {
                Card card = (Card) item.getPayload();
                String cardTitle = card.getTitle().toLowerCase();
                if (!cardTitle.contains(filterWord))
                    resultsList.remove(item);
            }
        }

        return resultsList;
    }

    private List<iGridItem> filterCardsByTag(@Nullable String filterTag, final List<iGridItem> inputList) {

        List<iGridItem> resultsList = new ArrayList<>(inputList);

        if (null != filterTag) {
            filterTag = filterTag.toLowerCase();

            for (iGridItem item : inputList) {
                Card card = (Card) item.getPayload();
                HashMap<String, Boolean> cardTags = card.getTagsHash();
                Boolean tag = cardTags.get(filterTag);
                if (null != tag) {
                    if (!cardTags.containsKey(filterTag) && tag) {
                        resultsList.remove(item);
                    }
                }
            }
        }

        return resultsList;
    }

    private <T> void showLoadMoreItem(int position, List<T> workableList) {
        int messageId = (workableList.size() > 0) ? R.string.CARDS_GRID_load_old : R.string.CARDS_GRID_no_more_cards;
        boolean enableClickListener = workableList.size() > 0;

        GridItem_LoadMore loadMoreItem = new GridItem_LoadMore(messageId, enableClickListener);
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
        originalItemsList.clear();

        notifyItemRangeRemoved(start, count);
    }

    private iGridItem getLastContentItem() {
        int index = originalItemsList.size() - 1;

        if (index < 0)
            return null;

        return originalItemsList.get(index);
    }

    private void synchronizeOriginalItemsList() {
        originalItemsList.clear();
        originalItemsList.addAll(itemsList);
    }

    private void updateList(List<iGridItem> newItemsList) {

        int oldItemsCount = getItemCount() - 1;
        int newItemsCount = newItemsList.size();
        int listsSizeDifference = oldItemsCount - newItemsCount;

        // после фильтрации элементов стало меньше
        if (listsSizeDifference >= 0) {
            // обновляю начальные
            for (int i = 0; i < newItemsCount; i++) {
                iGridItem newItem = newItemsList.get(i);
                itemsList.set(i, newItem);
                notifyItemChanged(i, newItem);
            }

            // удаляю лишние
            for (int i = oldItemsCount - 1; i >= newItemsCount; i--) {
                iGridItem oldItem = itemsList.get(i);
                itemsList.remove(i);
                notifyItemRemoved(i);
            }
        }
        // после фильтрации элементов стало больше
        else {
            // обновляю начальные
            for (int i=0; i < oldItemsCount; i++) {
                iGridItem newItem = newItemsList.get(i);
                itemsList.set(i, newItem);
                notifyItemChanged(i, newItem);
            }

            // добавляю недостающие
            for (int i=itemsList.size()-1; i < newItemsCount; i++) {
                iGridItem newItem = newItemsList.get(i);
                itemsList.add(i, newItem);
//                notifyItemChanged(i, newItem);
                notifyItemInserted(i);
            }
        }
    }

    private void append_LoadMore_Item() {
        GridItem_LoadMore itemLoadMore = new GridItem_LoadMore(R.string.CARDS_GRID_load_old, true);
        insertItem(itemsList.size(), itemLoadMore);
    }
}
