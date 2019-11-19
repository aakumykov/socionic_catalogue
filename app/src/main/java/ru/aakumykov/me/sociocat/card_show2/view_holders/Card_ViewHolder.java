package ru.aakumykov.me.sociocat.card_show2.view_holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show2.list_items.iList_Item;
import ru.aakumykov.me.sociocat.models.Card;

public class Card_ViewHolder extends Base_ViewHolder {

    @BindView(R.id.titleView) TextView titleView;
    @BindView(R.id.descriptionView) TextView descriptionView;


    public Card_ViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void initialize(iList_Item listItem) {
        Card card = (Card) listItem;
        displayCard(card);
    }

    private void displayCard(Card card) {
        titleView.setText(card.getTitle());
        descriptionView.setText(card.getDescription());
    }
}
