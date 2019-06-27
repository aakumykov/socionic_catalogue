package ru.aakumykov.me.sociocat.cards_grid.view_holders;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_grid.iCardsGrid;

public abstract class BaseViewHolder extends RecyclerView.ViewHolder implements iGridViewHolder
{
    public CardView mCardView;
    private int mOriginalBackgroundColor = -1;

    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
        mCardView = itemView.findViewById(R.id.cardView);
    }

    @Override
    public void fade() {
        mOriginalBackgroundColor = mCardView.getCardBackgroundColor().getDefaultColor();
        int newColor = itemView.getResources().getColor(R.color.cards_grid_pressed_background_color);
        mCardView.setCardBackgroundColor(newColor);
    }

    @Override
    public void unfade() {
        mCardView.setCardBackgroundColor(mOriginalBackgroundColor);
    }

    public void bindClickListener(iCardsGrid.iGridItemClickListener gridItemClickListener) {

        if (this instanceof Card_ViewHolder) {
            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gridItemClickListener.onGridItemClicked(v);
                }
            });
        }
    }
}
