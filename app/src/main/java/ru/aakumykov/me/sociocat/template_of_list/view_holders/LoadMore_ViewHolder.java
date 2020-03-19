package ru.aakumykov.me.sociocat.template_of_list.view_holders;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.template_of_list.iItemsList;

public class LoadMore_ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.cardView) View cardView;

    private iItemsList.iPresenter presenter;

    // Конструктор
    public LoadMore_ViewHolder(View itemView, iItemsList.iPresenter presenter) {
        super(itemView);
        this.presenter = presenter;
        ButterKnife.bind(this, itemView);
    }

    // Заполнение данными
    public void initialize() {

    }

    // Нажатия
    @OnClick(R.id.cardView)
    void onItemClicked() {
        presenter.onLoadMoreClicked();
    }

}
