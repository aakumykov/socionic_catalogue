package ru.aakumykov.me.sociocat.card_show.view_holders;

import android.view.View;

import butterknife.ButterKnife;

public class CommentsThrobber_ViewHolder extends Base_ViewHolder
{
    private final static String TAG = "CommentsThrobber_ViewHolder";

    public CommentsThrobber_ViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

