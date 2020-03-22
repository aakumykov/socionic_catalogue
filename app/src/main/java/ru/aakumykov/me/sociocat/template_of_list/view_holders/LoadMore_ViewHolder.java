package ru.aakumykov.me.sociocat.template_of_list.view_holders;

import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.R;

public class LoadMore_ViewHolder extends BasicViewHolder {

    @BindView(R.id.elementView) View cardView;

    // Конструктор
    public LoadMore_ViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    // Заполнение данными
    public void initialize(Object payload) {

    }

    @Override
    public void setSelected(boolean isSelected) {

    }

    // Нажатия
    @OnClick(R.id.elementView)
    void onItemClicked() {
        presenter.onLoadMoreClicked();
    }

}
