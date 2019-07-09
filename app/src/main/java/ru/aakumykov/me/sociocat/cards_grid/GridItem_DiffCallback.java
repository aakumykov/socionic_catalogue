package ru.aakumykov.me.sociocat.cards_grid;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import ru.aakumykov.me.sociocat.cards_grid.items.iGridItem;
import ru.aakumykov.me.sociocat.models.Card;

public class GridItem_DiffCallback extends DiffUtil.Callback {

    private final static String TAG = "GridItem_DiffCallback";
    private final List<iGridItem> mOldGridItems;
    private final List<iGridItem> mNewGridItems;

    public GridItem_DiffCallback(List<iGridItem> oldList, List<iGridItem> newList) {
        mOldGridItems = oldList;
        mNewGridItems = newList;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }

    @Override
    public int getOldListSize() {
        return mOldGridItems.size();
    }

    @Override
    public int getNewListSize() {
        return mNewGridItems.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        try {
            Card oldCard = (Card) mOldGridItems.get(oldItemPosition).getPayload();
            Card newCard = (Card) mNewGridItems.get(newItemPosition).getPayload();
            return oldCard.getKey().equals(newCard.getKey());
        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        try {
            Card oldCard = (Card) mOldGridItems.get(oldItemPosition).getPayload();
            Card newCard = (Card) mNewGridItems.get(newItemPosition).getPayload();
            String oldTitle = oldCard.getTitle();
            String newTitle = newCard.getTitle();
            String oldDescription = oldCard.getDescription();
            String newDescription = newCard.getDescription();
            return oldTitle.equals(newTitle) && oldDescription.equals(newDescription);
        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
