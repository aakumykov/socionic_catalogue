package ru.aakumykov.me.sociocat.card_show.view_holders;

import android.view.View;

import butterknife.ButterKnife;

public class CardThrobber_ViewHolder extends Base_ViewHolder
{
    private final static String TAG = "CardThrobber_ViewHolder";

    public CardThrobber_ViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

