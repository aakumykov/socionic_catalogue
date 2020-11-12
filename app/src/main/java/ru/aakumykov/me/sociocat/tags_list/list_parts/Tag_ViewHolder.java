package ru.aakumykov.me.sociocat.tags_list.list_parts;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.BasicMVP_DataItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.BasicMVP_ListItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.view_holders.BasicMVP_DataViewHolder;
import ru.aakumykov.me.sociocat.models.Tag;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.tags_list.interfaces.iTagsList_ClickListener;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class Tag_ViewHolder extends BasicMVP_DataViewHolder {

    @BindView(R.id.listItem) View listItem;
    @BindView(R.id.titleView) TextView titleView;
    @BindView(R.id.commentsCountView) TextView commentsCountView;
    @BindView(R.id.tagEditButton) View tagEditButton;

    public Tag_ViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void displayIsChecked(boolean selected) {
        if (selected) {
            int color = listItem.getResources().getColor(R.color.tags_list_selection_background_color);
            listItem.setBackgroundColor(color);
        }
        else
            listItem.setBackground(null);
    }

    @Override
    public void fillWithData(BasicMVP_ListItem basicListItem) {
        BasicMVP_DataItem basicMVPDataItem = (BasicMVP_DataItem) basicListItem;

        Tag tag = (Tag) basicMVPDataItem.getPayload();

        titleView.setText(tag.getName());
        commentsCountView.setText(String.valueOf(tag.getCardsCount()));

        displayIsChecked(basicMVPDataItem.isSelected());

        MyUtils.setVisibility(tagEditButton, UsersSingleton.getInstance().currentUserIsAdmin());
    }


    @OnClick(R.id.listItem)
    void onItemClicked() {
        mItemClickListener.onItemClicked(this);
    }

    @OnLongClick(R.id.listItem)
    void onItemLongClicked() {
        mItemClickListener.onItemLongClicked(this);
    }

    @OnClick(R.id.tagEditButton)
    void onEditTagClicked() {
        ((iTagsList_ClickListener) mItemClickListener).onEditTagClicked(this);
    }
}
