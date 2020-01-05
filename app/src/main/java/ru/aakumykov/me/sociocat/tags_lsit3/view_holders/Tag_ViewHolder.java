package ru.aakumykov.me.sociocat.tags_lsit3.view_holders;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.Tag;
import ru.aakumykov.me.sociocat.tags_lsit3.iTagsList3;
import ru.aakumykov.me.sociocat.tags_lsit3.model.Item;

public class Tag_ViewHolder extends RecyclerView.ViewHolder implements iTag_ViewHolder {

    @BindView(R.id.nameView) TextView nameView;
    @BindView(R.id.countView) TextView countView;

    private iTagsList3.iPresenter presenter;
    private Tag tag;

    public Tag_ViewHolder(View itemView, iTagsList3.iPresenter presenter) {
        super(itemView);
        this.presenter = presenter;
        ButterKnife.bind(this, itemView);
    }

    public void initialize(Object payload) {
        this.tag = (Tag) payload;

        if (null != nameView)
            nameView.setText(tag.getName());

        if (null != countView)
            countView.setText(String.valueOf(tag.getCards().size()));
    }

    @OnClick(R.id.tagItem)
    void onItemClicked() {
        presenter.onTagClicked(this.tag);
    }


    // iTag_ViewHolder
    // ...
}
