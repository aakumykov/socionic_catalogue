package ru.aakumykov.me.sociocat.cards_grid.view_holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_grid.iCardsGrid;
import ru.aakumykov.me.sociocat.cards_grid.items.GridItem_LoadOld;
import ru.aakumykov.me.sociocat.cards_grid.items.iGridItem;

public class LoadOld_ViewHolder extends BaseViewHolder {

    @BindView(R.id.cardView) CardView cardView;
    @BindView(R.id.titleView) TextView titleView;
    private final static String TAG = "LoadOld_ViewHolder";
    private iCardsGrid.iPresenter presenter;


    public LoadOld_ViewHolder(@NonNull View itemView, iCardsGrid.iPresenter presenter) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.presenter = presenter;
    }

    public void bindClickListener(iCardsGrid.iLoadMoreClickListener loadMoreClickListener) {
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMoreClickListener.onLoadMoreClicked(v);
            }
        });
    }

    public void initialize(int position, iGridItem gridItem) {

        GridItem_LoadOld gridItemLoadMore = (GridItem_LoadOld) gridItem;

        String textLabel = cardView.getResources().getString(gridItemLoadMore.getMessageId());
        titleView.setText(textLabel);

        if (gridItemLoadMore.isClickListenerEnabled()) {
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.onLoadOldClicked(position);
                }
            });
        }
    }
}
