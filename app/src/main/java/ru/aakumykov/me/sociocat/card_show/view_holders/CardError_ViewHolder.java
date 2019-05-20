package ru.aakumykov.me.sociocat.card_show.view_holders;

import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.ListItem_CardError;

public class CardError_ViewHolder extends Base_ViewHolder
{
    @BindView(R.id.cardErrorView) TextView cardErrorView;

    public CardError_ViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void initialize(ListItem_CardError cardErrorItem) {
        cardErrorView.setText( cardErrorItem.getErrorMsg() );
    }
}

