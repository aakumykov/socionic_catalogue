package ru.aakumykov.me.sociocat.tags_lsit3.view_holders;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.tags_lsit3.iTagsList3;
import ru.aakumykov.me.sociocat.tags_lsit3.model.Item;

public class Tag_ViewHolder extends RecyclerView.ViewHolder implements iTag_ViewHolder {

    @BindView(R.id.tagItem) View tagItem;
    @BindView(R.id.nameView) TextView nameView;

    private iTagsList3.iPresenter presenter;
    private Item item;

    public Tag_ViewHolder(View itemView, iTagsList3.iPresenter presenter) {
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
        presenter.onTagClicked(this.item);
    }


    // iTag_ViewHolder
    // ...
}
