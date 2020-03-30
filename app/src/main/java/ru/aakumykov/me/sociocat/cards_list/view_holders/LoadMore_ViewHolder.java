package ru.aakumykov.me.sociocat.cards_list.view_holders;

import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_list.iCardsList;
import ru.aakumykov.me.sociocat.cards_list.list_items.ListItem;

public class LoadMore_ViewHolder extends BasicViewHolder {

    @BindView(R.id.elementView) View cardView;

    // Конструктор
    public LoadMore_ViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    // Заполнение данными
    public void initialize(ListItem listItem) {

    }

    @Override
    public void setViewState(iCardsList.ItemState eItemState) {

    }

    // Нажатия
    @OnClick(R.id.elementView)
    void onItemClicked() {
        presenter.onLoadMoreClicked();
    }

}
