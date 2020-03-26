package ru.aakumykov.me.sociocat.cards_list.view_holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_list.list_items.DataItem;

public class DataItem_ViewHolder extends BasicViewHolder {

    @BindView(R.id.elementView) CardView elementView;
    @BindView(R.id.nameView) TextView nameView;
    @Nullable @BindView(R.id.countView) TextView countView;
    @BindView(R.id.selectedOverlay) View selectedOverlay;

    private DataItem dataItem;
    private static int initialBackgroundColor = -1;

    // Конструктор
    public DataItem_ViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        if (-1 == initialBackgroundColor)
            initialBackgroundColor = elementView.getCardBackgroundColor().getDefaultColor();
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
        presenter.onItemClicked(this.dataItem);
    }

    @OnLongClick(R.id.elementView)
    void onItemLongClicked() {
        presenter.onItemLongClicked(this.dataItem);
    }


    public void setSelected(boolean isSelected) {
        selectedOverlay.setVisibility((isSelected) ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void setIsNowDeleting(boolean isNowDeleting) {
        if (isNowDeleting)
            elementView.setCardBackgroundColor(elementView.getResources().getColor(R.color.card_is_now_deleting));
        else
            elementView.setCardBackgroundColor(initialBackgroundColor);
    }
}
