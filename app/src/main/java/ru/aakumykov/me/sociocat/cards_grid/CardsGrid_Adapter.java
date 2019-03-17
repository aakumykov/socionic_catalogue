package ru.aakumykov.me.sociocat.cards_grid;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.utils.MyUtils;


public class CardsGrid_Adapter extends RecyclerView.Adapter<CardsGrid_Adapter.ViewHolder>
    implements Filterable
{
    public interface iAdapterConsumer {
        void onDataFiltered(List<Card> filteredCardsList);
        void onItemClick(int position);
    }

    private LayoutInflater inflater;
    private iAdapterConsumer adapterConsumer;
    private boolean gridMode = true;
    private List<Card> cardsList;
    private List<Card> originalCardsList;
    private List<Card> cardsListFiltered;

    private static HashMap<String, Integer> cardTypes = new HashMap<>();
    static {
        cardTypes.put(Constants.TEXT_CARD, 10);
        cardTypes.put(Constants.IMAGE_CARD, 20);
        cardTypes.put(Constants.AUDIO_CARD, 30);
        cardTypes.put(Constants.VIDEO_CARD, 40);
    }

    CardsGrid_Adapter(Context context, List<Card> cardsList) {
        this.cardsList = cardsList;
        this.originalCardsList=  cardsList;
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

        Card card = cardsList.get(position);

        switch (viewHolder.getItemViewType()) {
            case 10:
                initTextCard((CommonViewHolder) viewHolder, card);
                break;
            case 20:
                initImageCard((ImageViewHolder) viewHolder, card);
                break;
            case 30:
                initAudioCard((AudioViewHolder) viewHolder, card);
                break;
            case 40:
                initVideoCard((VideoViewHolder) viewHolder, card);
                break;
            default:
                break;
        }

        // Слушатель нажатий
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterConsumer.onItemClick(position);
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
        } else {
            viewHolder.quoteView.setText(null);
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

        // Видео
        if (card.isVideoCard()) {
            MyUtils.hide(viewHolder.imageView);
            MyUtils.hide(viewHolder.quoteView);
        }
    }

    private void initTextCard(TextViewHolder textViewHolder, Card card) {
        textViewHolder.titleView.setText(card.getTitle());
    }

    private void initImageCard(ImageViewHolder imageViewHolder, Card card) {

    }

    private void initAudioCard(AudioViewHolder audioViewHolder, Card card) {

    }

    private void initVideoCard(VideoViewHolder videoViewHolder, Card card) {

    }


    @Override
    public int getItemCount() {
        return cardsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Card card = cardsList.get(position);
        String cardTypeString = card.getType();

        if (cardTypes.containsKey(cardTypeString)) {
            return cardTypes.get(cardTypeString);
        } else {
            return -1;
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    cardsListFiltered = originalCardsList;
                } else {
                    List<Card> filteredList = new ArrayList<>();
                    for (Card row : originalCardsList) {

                        if (row.getTitle().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    cardsListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = cardsListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                cardsList = (ArrayList<Card>) results.values;

                if (null != adapterConsumer) { // Иначе падает при входе со страницы плиток
                    adapterConsumer.onDataFiltered(cardsList);
                }

                notifyDataSetChanged();
            }
        };
    }


    // Другие методы
    void bindView(iAdapterConsumer consumer) {
        this.adapterConsumer = consumer;
    }

    void unbindView() {
        this.adapterConsumer = null;
    }

    public void activateListLayout() {
        gridMode = false;
    }

    public void activateGridLayout() {
        gridMode = true;
    }

    public void restoreInitialList() {
        this.cardsList = this.originalCardsList;
        notifyDataSetChanged();
    }


    // Внутренние классы
    static class ViewHolder extends RecyclerView.ViewHolder {
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

    static class TextViewHolder extends RecyclerView.ViewHolder {
        public TextViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ProgressBar imageThrobber;
        ImageView imageView;
        ImageView imageErrorView;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageThrobber = itemView.findViewById(R.id.imageThrobber);
            imageView = itemView.findViewById(R.id.imageView);
            imageErrorView = itemView.findViewById(R.id.imageErrorView);
        }
    }

    static class AudioViewHolder extends RecyclerView.ViewHolder {
        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    static class CommonViewHolder extends RecyclerView.ViewHolder {
        TextView titleView;
        public CommonViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.titleView);
        }
    }
}