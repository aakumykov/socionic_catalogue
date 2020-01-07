package ru.aakumykov.me.sociocat.template_of_list.view_holders;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.template_of_list.iItemsList;
import ru.aakumykov.me.sociocat.template_of_list.model.Item;

public class Item_ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tagItem) View tagItem;
    @BindView(R.id.nameView) TextView nameView;
    @BindView(R.id.countView) TextView countView;

    private iItemsList.iPresenter presenter;
    private Item item;

    // Конструктор
    public Item_ViewHolder(View itemView, iItemsList.iPresenter presenter) {
        super(itemView);
        this.presenter = presenter;
        ButterKnife.bind(this, itemView);
    }

    // Заполнение данными
    public void initialize(Object payload) {
        this.item = (Item) payload;
        nameView.setText(item.getName());
        countView.setText(String.valueOf(item.getCount()));
    }

    // Нажатия
    @OnClick(R.id.tagItem)
    void onItemClicked() {
        presenter.onItemClicked(this.item);
    }
}
