package ru.aakumykov.me.sociocat.cards_list2.view_holders;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import butterknife.BindView;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.list_items.BasicMVP_ListItem;

public class CardViewHolder_Feed extends CardViewHolder {

    @BindView(R.id.imageView) ImageView mImageView;

    public CardViewHolder_Feed(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void fillWithData(BasicMVP_ListItem basicListItem) {
        super.fillWithData(basicListItem);
    }
}
