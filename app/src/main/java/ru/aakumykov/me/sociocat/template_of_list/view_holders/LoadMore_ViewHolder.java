package ru.aakumykov.me.sociocat.template_of_list.view_holders;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.template_of_list.iItemsList;

public class LoadMore_ViewHolder extends BasicViewHolder {

    @BindView(R.id.cardView) View cardView;

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
    @OnClick(R.id.cardView)
    void onItemClicked() {
        presenter.onLoadMoreClicked();
    }

}
