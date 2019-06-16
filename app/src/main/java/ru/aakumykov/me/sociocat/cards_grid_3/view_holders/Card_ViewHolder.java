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
import ru.aakumykov.me.sociocat.models.Card;

public class Card_ViewHolder extends RecyclerView.ViewHolder implements
    View.OnClickListener
{
    @BindView(R.id.cardView) CardView cardView;
    @BindView(R.id.titleView) TextView titleView;
    private iCG3.iPresenter presenter;
    private int position;


    public Card_ViewHolder(@NonNull View itemView, iCG3.iPresenter presenter) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.presenter = presenter;
    }

    public void initialize(Card card, int position) {
        this.position = position;
        titleView.setText(card.getTitle());
        cardView.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cardView:
                presenter.onCardClicked(position);
                break;
            default:
                break;
        }
    }
}
