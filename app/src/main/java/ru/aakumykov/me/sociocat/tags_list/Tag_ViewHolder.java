package ru.aakumykov.me.sociocat.tags_list;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.BasicMVP_DataItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.BasicMVP_ListItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.view_holders.BasicMVP_DataViewHolder;
import ru.aakumykov.me.sociocat.models.Tag;

public class Tag_ViewHolder extends BasicMVP_DataViewHolder {

    @BindView(R.id.titleView) TextView titleView;
    @BindView(R.id.commentsCountView) TextView commentsCountView;

    public Tag_ViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void displayIsChecked(boolean selected) {

    }

    @Override
    public void fillWithData(BasicMVP_ListItem basicListItem) {
        Tag tag = (Tag) ((BasicMVP_DataItem) basicListItem).getPayload();
        titleView.setText(tag.getName());
        commentsCountView.setText(String.valueOf(tag.getCardsCount()));
    }
}
