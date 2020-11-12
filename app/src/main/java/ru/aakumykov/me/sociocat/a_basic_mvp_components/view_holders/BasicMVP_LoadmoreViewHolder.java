package ru.aakumykov.me.sociocat.a_basic_mvp_components.view_holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.BasicMVP_ListItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.BasicMVP_LoadmoreItem;

public class BasicMVP_LoadmoreViewHolder extends BasicMVP_ViewHolder {

    @BindView(R.id.titleView) TextView titleView;

    public BasicMVP_LoadmoreViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void fillWithData(BasicMVP_ListItem basicListItem) {
        BasicMVP_LoadmoreItem loadmoreItem = (BasicMVP_LoadmoreItem) basicListItem;
        if (loadmoreItem.hasTitleId())
            titleView.setText(loadmoreItem.getTitleId());
    }

    @OnClick(R.id.itemView)
    public void onLoadMoreClicked() {
        getItemClickListener().onLoadMoreClicked(this);
    }
}