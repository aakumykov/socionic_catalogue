package ru.aakumykov.me.sociocat.cards_list.view_holders;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_list.iCardsList;
import ru.aakumykov.me.sociocat.cards_list.list_items.DataItem;

public class DataItem_ViewHolder extends BasicViewHolder {

    @BindView(R.id.elementView) ViewGroup elementView;
    @BindView(R.id.nameView) TextView nameView;
    @Nullable @BindView(R.id.countView) TextView countView;

    private static final String TAG = DataItem_ViewHolder.class.getSimpleName();
    private DataItem dataItem;
    private iCardsList.LayoutMode currentLayoutMode;
    private int neutralStateColor = -1;


    // Конструктор
    public DataItem_ViewHolder(View itemView, iCardsList.LayoutMode layoutMode) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        currentLayoutMode = layoutMode;
    }


    // Заполнение данными
    public void initialize(Object payload) {
        this.dataItem = (DataItem) payload;

        if (nameView != null)
            nameView.setText(dataItem.getName());

        if (countView != null)
            countView.setText(String.valueOf(dataItem.getCount()));
    }

    @Override
    public void setViewState(iCardsList.ItemState itemState) {
        switch (itemState) {
            case NEUTRAL:
                applyNeutralState();
                break;
            case SELECTED:
                applySelectedState();
                break;
            case DELETING:
                applyDeletingState();
                break;
            default:
                Log.e(TAG, "Unknown eViewHolderState: "+ itemState);
        }
    }


    // Нажатия
    @OnClick(R.id.elementView)
    void onItemClicked() {
        presenter.onDataItemClicked(this.dataItem);
    }

    @OnLongClick(R.id.elementView)
    void onItemLongClicked() {
        presenter.onDataItemLongClicked(this.dataItem);
    }


    // Внутренние
    private void applySelectedState() {
        int selectedColor = elementView.getResources().getColor(R.color.element_is_selected);

        switch (currentLayoutMode) {
            case LIST:
                elementView.setBackgroundColor(selectedColor);
                break;

            default:
                ((CardView) elementView).setCardBackgroundColor(selectedColor);
                break;
        }
    }

    private void applyDeletingState() {
        int deletingStateColor = elementView.getResources().getColor(R.color.element_is_now_deleting);

        switch (currentLayoutMode) {
            case LIST:
                elementView.setBackgroundColor(deletingStateColor);
                break;

            default:
                ((CardView)elementView).setCardBackgroundColor(deletingStateColor);
                break;
        }
    }

    private void applyNeutralState() {
        switch (currentLayoutMode) {
            case LIST:
                elementView.setBackgroundResource(R.drawable.shape_bottom_line);
                break;

            default:
                ((CardView)elementView).setCardBackgroundColor(neutralStateColor);
                break;
        }
    }

}
