package ru.aakumykov.me.sociocat.cards_grid_3;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.Card;

public class GridItem_ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.titleView) TextView titleView;

    public GridItem_ViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void initialize(Card card) {
        titleView.setText(card.getTitle());
    }
}
