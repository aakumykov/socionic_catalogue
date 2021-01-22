package io.gitlab.aakumykov.sociocat.b_tags_list.view_holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import io.gitlab.aakumykov.sociocat.R;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_DataItem;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.utils.ViewUtils;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_holders.BasicMVPList_DataViewHolder;
import io.gitlab.aakumykov.sociocat.b_tags_list.interfaces.iTagsList_ItemClickListener;
import io.gitlab.aakumykov.sociocat.models.Tag;
import io.gitlab.aakumykov.sociocat.singletons.UsersSingleton;
import io.gitlab.aakumykov.sociocat.utils.MyUtils;

public class TagViewHolder extends BasicMVPList_DataViewHolder {

    @BindView(R.id.listItem) View listItem;
    @BindView(R.id.checkMark) ImageView checkMark;
    @BindView(R.id.titleView) TextView titleView;
    @BindView(R.id.commentsCountView) TextView commentsCountView;
    @BindView(R.id.tagEditButton) View tagEditButton;
    @Nullable @BindView(R.id.highlightingOverlay) View highlightingOverlay;


    public TagViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void displayIsChecked(boolean selected) {
        ViewUtils.setVisibility(checkMark, selected);
    }

    @Override
    public void displayIsHighlighted(boolean isHighLighted) {
        if (null != highlightingOverlay) {
            if (isHighLighted) {
                highlightingOverlay.setBackgroundResource(R.drawable.simple_list_item_background_highlighted);
                ViewUtils.show(highlightingOverlay);
            }
            else {
                highlightingOverlay.setBackground(null);
                ViewUtils.hide(highlightingOverlay);
            }
        }
    }

    @Override
    public void initialize(BasicMVPList_ListItem basicListItem) {
        BasicMVPList_DataItem dataItem = (BasicMVPList_DataItem) basicListItem;

        Tag tag = (Tag) dataItem.getPayload();

        titleView.setText(tag.getName());

        commentsCountView.setText(String.valueOf(tag.getCardsCount()));

        MyUtils.setVisibility(tagEditButton,
                UsersSingleton.getInstance().currentUserIsAdmin());

        displayIsChecked(dataItem.isSelected());
        displayIsHighlighted(dataItem.isHighLighted());
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
        ((iTagsList_ItemClickListener) mItemClickListener).onEditTagClicked(this);
    }
}
