package ru.aakumykov.me.mvp.cards_grid;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
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


public class CardsGrid_Adapter extends RecyclerView.Adapter<CardsGrid_Adapter.ViewHolder> {

    public interface iOnItemClickListener {
        void onItemClick(int position);
    }

    private LayoutInflater inflater;
    private List<Card> cardList;
    private iOnItemClickListener onItemClickListener;
    private boolean gridMode = true;

    CardsGrid_Adapter(Context context, List<Card> cardList) {
        this.cardList = cardList;
        this.inflater = LayoutInflater.from(context);
    }

    // Системные методы
    @NonNull @Override
    public CardsGrid_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = (gridMode) ?
                inflater.inflate(R.layout.cards_grid_item, parent, false)
                :
                inflater.inflate(R.layout.cards_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CardsGrid_Adapter.ViewHolder viewHolder, final int position) {
        Card card = cardList.get(position);

        // Слушатель нажатий
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position);
            }
        });

        // Название
        viewHolder.titleView.setText(card.getTitle());

        // Цитата
        if (card.isTextCard()) {
            String quote = MyUtils.cutToLength(card.getQuote(), Constants.CARDS_GRID_QUOTE_MAX_LENGTH);
            viewHolder.quoteView.setText(quote);
            MyUtils.show(viewHolder.quoteView);
            MyUtils.hide(viewHolder.imageView);
        }

        // Картинка
        if (card.isImageCard()) {

            MyUtils.hide(viewHolder.quoteView);
            MyUtils.show(viewHolder.imageThrobber);

            Picasso.get()
                    .load(card.getImageURL())
                    .into(viewHolder.imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            MyUtils.hide(viewHolder.imageThrobber);
                            MyUtils.show(viewHolder.imageView);
                        }

                        @Override
                        public void onError(Exception e) {
                            MyUtils.hide(viewHolder.imageThrobber);
                            MyUtils.show(viewHolder.imageErrorView);
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    // Какой-то класс
    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cardView) CardView cardView;
        @BindView(R.id.titleView) TextView titleView;
        @BindView(R.id.quoteView) TextView quoteView;
        @BindView(R.id.imageThrobber) ProgressBar imageThrobber;
        @BindView(R.id.imageView) ImageView imageView;
        @BindView(R.id.imageErrorView) ImageView imageErrorView;

        ViewHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    // Другие методы
    void bindClickListener(iOnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    void unbindClickListener() {
        this.onItemClickListener = null;
    }

    public void activateListLayout() {
        gridMode = false;
    }

    public void activateGridLayout() {
        gridMode = true;
    }
}