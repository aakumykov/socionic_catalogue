package ru.aakumykov.me.sociocat.cards_grid_3.view_holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_grid_3.iCG3;
import ru.aakumykov.me.sociocat.cards_grid_3.items.LoadMore_Item;
import ru.aakumykov.me.sociocat.cards_grid_3.items.iGridItem;

public class LoadMore_ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.cardView) CardView cardView;
    @BindView(R.id.titleView) TextView titleView;
    private final static String TAG = "LoadMore_ViewHolder";
    private iCG3.iPresenter presenter;
    private int position;


    public LoadMore_ViewHolder(@NonNull View itemView, iCG3.iPresenter presenter) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.presenter = presenter;
    }

    public void initialize(iGridItem gridItem, int position) {
        this.position = position;

        titleView.setText(R.string.CARDS_GRID_load_more);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                LoadMore_Item loadMoreItem = (LoadMore_Item) gridItem;
                presenter.onLoadMoreClicked(position, loadMoreItem.getStartKey());
            }
        });
    }
}
