package ru.aakumykov.me.mvp.cards_grid;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.utils.MyUtils;


public class CardsGrid_RecyclerAdapter extends RecyclerView.Adapter<CardsGrid_RecyclerAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<Card> cardList;

    CardsGrid_RecyclerAdapter(Context context, List<Card> cardList) {
        this.cardList = cardList;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull @Override
    public CardsGrid_RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.cards_grid_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardsGrid_RecyclerAdapter.ViewHolder viewHolder, int position) {
        Card card = cardList.get(position);

        // Название
        viewHolder.titleView.setText(card.getTitle());

        // Цитата
        if (card.isTextCard()) {
            String quote = MyUtils.cutToLength(card.getQuote(), Constants.CARDS_GRID_QUOTE_MAX_LENGTH);
            viewHolder.quoteView.setText(quote);
            MyUtils.show(viewHolder.quoteView);
        }

        // Картинка
        if (card.isImageCard()) {

            MyUtils.show(viewHolder.imageView);

            Picasso.get()
                    .load(card.getImageURL())
                    .into(viewHolder.imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.titleView) TextView titleView;
        @BindView(R.id.quoteView) TextView quoteView;
        @BindView(R.id.imageView) ImageView imageView;

        ViewHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}