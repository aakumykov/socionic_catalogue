package ru.aakumykov.me.sociocat.cards_list.view_holders;

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
    @Nullable @BindView(R.id.selectedOverlay) View selectedOverlay;

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

    // Нажатия
    @OnClick(R.id.elementView)
    void onItemClicked() {
        presenter.onDataItemClicked(this.dataItem);
    }

    @OnLongClick(R.id.elementView)
    void onItemLongClicked() {
        presenter.onDataItemLongClicked(this.dataItem);
    }

    @Override
    public void setSelected(boolean isSelected) {
        int selectedStateColor = elementView.getResources().getColor(R.color.element_is_selected);

        if (isListMode()) {
            // Режим списка
            if (isSelected)
                elementView.setBackgroundColor(selectedStateColor);
            else
                elementView.setBackgroundResource(R.drawable.shape_bottom_line);
        }
        else {
            // Режим сетки
            if (null != selectedOverlay)
                selectedOverlay.setVisibility((isSelected) ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @Override
    public void setIsNowDeleting(boolean isNowDeleting) {
        int deletingStateColor = elementView.getResources().getColor(R.color.element_is_now_deleting);

        if (isListMode()) {
            // Режим списка
            if (isNowDeleting)
                elementView.setBackgroundColor(deletingStateColor);
            else
                elementView.setBackgroundResource(R.drawable.shape_bottom_line);
        }
        else {
            // Режим сетки
            if (isNowDeleting)
                ((CardView)elementView).setCardBackgroundColor(deletingStateColor);
            else
                ((CardView)elementView).setCardBackgroundColor(neutralStateColor);
        }
    }


    // Внутренние методы
    private boolean isListMode() {
        return iCardsList.LayoutMode.LIST.equals(currentLayoutMode);
    }
}
