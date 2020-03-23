package ru.aakumykov.me.sociocat.template_of_list.view_holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.template_of_list.list_items.DataItem;

public class DataItem_ViewHolder extends BasicViewHolder {

    @BindView(R.id.cardView) View cardView;
    @BindView(R.id.nameView) TextView nameView;
    @Nullable @BindView(R.id.countView) TextView countView;
    @BindView(R.id.selectedOverlay) View selectedOverlay;

    private DataItem dataItem;

    // Конструктор
    public DataItem_ViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
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
    @OnClick(R.id.cardView)
    void onItemClicked() {
        presenter.onDataItemClicked(this.dataItem);
    }

    @OnLongClick(R.id.cardView)
    void onItemLongClicked() {
        presenter.onDataItemLongClicked(this.dataItem);
    }


    public void setSelected(boolean isSelected) {
        selectedOverlay.setVisibility((isSelected) ? View.VISIBLE : View.INVISIBLE);
    }
}
