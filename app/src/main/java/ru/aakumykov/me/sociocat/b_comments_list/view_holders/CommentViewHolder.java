package ru.aakumykov.me.sociocat.b_comments_list.view_holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_DataItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.utils.ViewUtils;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_holders.BasicMVPList_DataViewHolder;
import ru.aakumykov.me.sociocat.models.Comment;

public class CommentViewHolder extends BasicMVPList_DataViewHolder {

    @BindView(R.id.elementView) View elementView;
    @BindView(R.id.titleView) TextView titleView;
    @Nullable @BindView(R.id.selectingOverlay) View selectingOverlay;
    @Nullable @BindView(R.id.highlightingOverlay) View highlightingOverlay;


    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void displayIsChecked(boolean selected) {
        ViewUtils.setVisibility(selectingOverlay, selected);
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

        Comment comment = (Comment) dataItem.getPayload();

        displayText(comment);
        displayIsChecked(dataItem.isSelected());
        displayIsHighlighted(dataItem.isHighLighted());
    }



    @OnClick(R.id.elementView)
    void onItemClicked() {
        mItemClickListener.onItemClicked(this);
    }

    @OnLongClick(R.id.listItem)
    void onItemLongClicked() {
        mItemClickListener.onItemLongClicked(this);
    }


    private void displayText(@NonNull Comment comment) {
        titleView.setText(comment.getName());
    }
}
