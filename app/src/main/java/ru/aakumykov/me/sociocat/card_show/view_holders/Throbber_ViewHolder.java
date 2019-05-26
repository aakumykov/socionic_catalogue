package ru.aakumykov.me.sociocat.card_show.view_holders;

import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.list_items.Throbber_Item;

public class Throbber_ViewHolder extends Base_ViewHolder
{
    @BindView(R.id.throbberMessageView) TextView throbberMessageView;

    public Throbber_ViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void initialize(Throbber_Item throbberItem) {
        throbberMessageView.setText(throbberItem.getMessage());
    }
}

