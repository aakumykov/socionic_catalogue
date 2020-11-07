package ru.aakumykov.me.sociocat.a_basic_mvp_components.view_holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.Basic_ListItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.Basic_LoadmoreItem;

public class Basic_LoadmoreViewHolder extends Basic_ViewHolder {

    @BindView(R.id.titleView) TextView titleView;

    public Basic_LoadmoreViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void fillWithData(Basic_ListItem basicListItem) {
        Basic_LoadmoreItem loadmoreItem = (Basic_LoadmoreItem) basicListItem;
        if (loadmoreItem.hasTitleId())
            titleView.setText(loadmoreItem.getTitleId());
    }

    @OnClick(R.id.itemView)
    public void onLoadMoreClicked() {
        getItemClickListener().onLoadMoreClicked(this);
    }
}
