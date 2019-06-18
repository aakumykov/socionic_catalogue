package ru.aakumykov.me.sociocat.cards_grid_3;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
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
import ru.aakumykov.me.sociocat.cards_grid_3.items.LoadMore_Item;
import ru.aakumykov.me.sociocat.cards_grid_3.items.Throbber_Item;
import ru.aakumykov.me.sociocat.cards_grid_3.items.iGridItem;
import ru.aakumykov.me.sociocat.cards_grid_3.view_holders.Card_ViewHolder;
import ru.aakumykov.me.sociocat.cards_grid_3.view_holders.LoadMore_ViewHolder;
import ru.aakumykov.me.sociocat.cards_grid_3.view_holders.Throbber_ViewHolder;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.User;
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

        iGridItem item = itemsList.get(position);

        if (item instanceof Card) {
            Card card = (Card) item;
            Card_ViewHolder gridItemViewHolder = (Card_ViewHolder) viewHolder;
            gridItemViewHolder.initialize(card, position);
        }
        else if (item instanceof LoadMore_Item) {
            LoadMore_ViewHolder loadMoreViewHolder = (LoadMore_ViewHolder) viewHolder;
            loadMoreViewHolder.initialize(item, position);
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
        iGridItem item = itemsList.get(position);

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
            lastCard = (Card) list.get(maxIndex);
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
        if (gridItem instanceof LoadMore_Item) {
            itemsList.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public void showThrobber() {
        itemsList.add(new Throbber_Item());
        int index = itemsList.size() - 1;
        notifyItemChanged(index);
    }

    @Override
    public void showThrobber(int position) {
        itemsList.remove(position);
        itemsList.add(new Throbber_Item());
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
    public void showPopupMenu(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);

        User currentUser = usersSingleton.getCurrentUser();
        Card card = (Card) itemsList.get(position);

        if (null != currentUser) {
            if (usersSingleton.currentUserIsAdmin() || usersSingleton.isCardOwner(card)) {
                popupMenu.inflate(R.menu.edit);
                popupMenu.inflate(R.menu.delete);
            }
        }

        popupMenu.inflate(R.menu.share);

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                unfadeItem(view, position);
            }
        });

        //Card_ViewHolder cardViewHolder =
        popupMenu.show();
    }

    @Override
    public void fadeItem(View view, int position) {
        this.originalBackground = view.getBackground();

        int color = view.getResources().getColor(R.color.cards_grid_pressed_background_color);
        view.setBackgroundColor(color);
    }

    @Override
    public void unfadeItem(View view, int position) {
        view.setBackground(this.originalBackground);
    }


    /*
    private void showPopupMenu(View view, int listPosition, ViewHolderCommon viewHolderCommon) {

        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);

        User currentUser = usersSingleton.getCurrentUser();
        Card currentCard = cardsList.get(listPosition);

        if (null != currentUser) {
            if (usersSingleton.currentUserIsAdmin() || usersSingleton.isCardOwner(currentCard)) {
                popupMenu.inflate(R.menu.edit);
                popupMenu.inflate(R.menu.delete);
            }
        }
        else {
            popupMenu.inflate(R.menu.share);
        }

        viewHolderCommon.saveOriginalBackground();
        viewHolderCommon.setPressedBackground();

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                viewHolderCommon.restoreBackground();
            }
        });

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                adapterUser.onPopupMenuClick(item, listPosition);
                return true;
            }
        });

        popupMenu.show();
    }
    */

    // Внутренние методы
    private void showLoadMoreItem(@Nullable Card card) {
        if (null != card) {
            itemsList.add(new LoadMore_Item(card));
            notifyItemChanged(itemsList.size());
        }
    }
}
