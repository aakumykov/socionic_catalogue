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

public class Row_ViewHolder extends RecyclerView.ViewHolder implements iViewHolder {

    @BindView(R.id.tagItem) View tagItem;
    @BindView(R.id.nameView) TextView nameView;

    private iItemsList.iPresenter presenter;
    private Item item;

    public Row_ViewHolder(View itemView, iItemsList.iPresenter presenter) {
        super(itemView);
        this.presenter = presenter;
        ButterKnife.bind(this, itemView);
    }

    public void initialize(Object payload) {
        this.item = (Item) payload;
        nameView.setText(item.getName());
    }

    @OnClick(R.id.tagItem)
    void onItemClicked() {
        presenter.onItemClicked(this.item);
    }


    // iViewHolder
    // ...
}
