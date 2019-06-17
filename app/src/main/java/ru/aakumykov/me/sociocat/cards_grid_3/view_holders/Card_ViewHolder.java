package ru.aakumykov.me.sociocat.cards_grid_3.view_holders;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.myimageloader.MyImageLoader;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_grid_3.iCG3;
import ru.aakumykov.me.sociocat.models.Card;

public class Card_ViewHolder extends RecyclerView.ViewHolder implements
    View.OnClickListener
{
    private final static String TAG = "Card_ViewHolder";

    @BindView(R.id.cardView) CardView cardView;
    @BindView(R.id.titleView) TextView titleView;
    @Nullable @BindView(R.id.imageContainer) ViewGroup imageContainer;

    private iCG3.iPresenter presenter;
    private int position;


    public Card_ViewHolder(@NonNull View itemView, iCG3.iPresenter presenter) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.presenter = presenter;
    }

    public void initialize(Card card, int position) {
        this.position = position;

        cardView.setOnClickListener(this);

        commonCardInit(card);

        if (card.isTextCard()) {
            initTextCard(card);
        }
        else if (card.isImageCard()) {
            initImageCard(card);
        }
        else if (card.isAudioCard()) {
            initAudioCard(card);
        }
        else if (card.isVideoCard()) {
            initVideoCard(card);
        }
        else {
            Log.e(TAG, "Unknown card type: "+card);
        }
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


    // Внутренние методы
    private void commonCardInit(Card card) {
        titleView.setText(card.getTitle());
    }

    private void initTextCard(Card card) {

    }

    private void initImageCard(Card card) {
        MyImageLoader.loadImageToContainer(
                imageContainer.getContext(),
                imageContainer,
                card.getImageURL()
            );
    }

    private void initAudioCard(Card card) {

    }

    private void initVideoCard(Card card) {

    }

    private void initUnknownCard(Card card) {

    }
}
