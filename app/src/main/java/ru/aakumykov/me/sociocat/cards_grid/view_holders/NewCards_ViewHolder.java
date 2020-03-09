package ru.aakumykov.me.sociocat.cards_grid.view_holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_grid.iCardsGrid;

public class NewCards_ViewHolder extends BaseViewHolder {

    private final static String TAG = "NewCards_ViewHolder";
    @BindView(R.id.mainView) CardView mainView;
    private final iCardsGrid.iPresenter presenter;

    public NewCards_ViewHolder(@NonNull View itemView, iCardsGrid.iPresenter presenter) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.presenter = presenter;
    }


}
