package ru.aakumykov.me.sociocat.cards_grid;

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
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class CardsGrid_Adapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements Filterable
{
    public interface iAdapterConsumer {
        void onDataFiltered(List<Card> filteredCardsList);
        void onItemClick(int position);
    }

    private static final int VIEW_TYPE_TEXT_CARD = 10;
    private static final int VIEW_TYPE_IMAGE_CARD = 20;
    private static final int VIEW_TYPE_AUDIO_CARD = 30;
    private static final int VIEW_TYPE_VIDEO_CARD = 40;

    private List<Card> cardsList;
    private List<Card> originalCardsList;
    private List<Card> cardsListFiltered;
    private CardsGrid_Adapter2.iAdapterConsumer adapterConsumer;


    // Конструктор
    CardsGrid_Adapter2(List<Card> cardsList) {
        this.cardsList = cardsList;
    }


    // Системные методы
    @Override
    public int getItemCount() {
        return null == cardsList ? 0 : cardsList.size();
    }

    @Override
    public int getItemViewType(int position) {

        // TODO: по идее, это неустойчиво к отсутствию карточки
        Card card = cardsList.get(position);

        switch (card.getType()) {
            case Constants.TEXT_CARD:
                return VIEW_TYPE_TEXT_CARD;
            case Constants.IMAGE_CARD:
                return VIEW_TYPE_IMAGE_CARD;
            case Constants.AUDIO_CARD:
                return VIEW_TYPE_AUDIO_CARD;
            case Constants.VIDEO_CARD:
                return VIEW_TYPE_VIDEO_CARD;
            default:
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

    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;

        switch (viewType) {
            case VIEW_TYPE_TEXT_CARD:
                view = layoutInflater.inflate(R.layout.cards_grid_item_text, parent, false);
                return new ViewHolderText(view);

            case VIEW_TYPE_IMAGE_CARD:
                view = layoutInflater.inflate(R.layout.cards_grid_item_image, parent, false);
                return new ViewHolderImage(view);

            case VIEW_TYPE_AUDIO_CARD:
                view = layoutInflater.inflate(R.layout.cards_grid_item_audio, parent, false);
                return new ViewHolderAudio(view);

            case VIEW_TYPE_VIDEO_CARD:
                view = layoutInflater.inflate(R.layout.cards_grid_item_video, parent, false);
                return new ViewHolderVideo(view);

            default:
                throw new RuntimeException("Unknown view type: "+viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int listPosition) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_TEXT_CARD:
                initTextLayout((ViewHolderText)holder, listPosition);
                break;
            case VIEW_TYPE_IMAGE_CARD:
                initImageLayout((ViewHolderImage)holder, listPosition);
                break;
            case VIEW_TYPE_AUDIO_CARD:
                initAudioLayout((ViewHolderAudio)holder, listPosition);
                break;
            case VIEW_TYPE_VIDEO_CARD:
                initVideoLayout((ViewHolderVideo)holder, listPosition);
            default:
                break;
        }
    }


    // Публичные методы
    public void bindView(CardsGrid_Adapter2.iAdapterConsumer consumer) {
        this.adapterConsumer = consumer;
    }

    public void unbindView() {
        this.adapterConsumer = null;
    }

    public void restoreInitialList() {
        this.cardsList = this.originalCardsList;
        notifyDataSetChanged();
    }


    // Внутренние методы
    private void initTextLayout(ViewHolderText viewHolder, int listPosition) {
        Card card = cardsList.get(listPosition);
        viewHolder.titleView.setText(card.getTitle());
    }

    private void initImageLayout(ViewHolderImage viewHolder, int listPosition) {
        Card card = cardsList.get(listPosition);
        viewHolder.titleView.setText(card.getTitle());

        MyUtils.show(viewHolder.imageThrobber);

        Picasso.get().load(card.getImageURL()).into(viewHolder.imageView, new Callback() {
            @Override public void onSuccess() {
                MyUtils.hide(viewHolder.imageThrobber);
                MyUtils.show(viewHolder.imageView);
            }

            @Override public void onError(Exception e) {
                MyUtils.show(viewHolder.imageErrorView);
            }
        });
    }

    private void initAudioLayout(ViewHolderAudio viewHolder, int listPosition) {
        Card card = cardsList.get(listPosition);
        viewHolder.titleView.setText(card.getTitle());
    }

    private void initVideoLayout(ViewHolderVideo viewHolder, int listPosition) {
        Card card = cardsList.get(listPosition);
        viewHolder.titleView.setText(card.getTitle());
    }


    // Внутренние классы
    static class ViewHolderText extends ViewHolderCommon {
        TextView titleView;
        public ViewHolderText(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.titleView);
        }
    }

    static class ViewHolderImage extends ViewHolderCommon {
        ProgressBar imageThrobber;
        ImageView imageView;
        ImageView imageErrorView;
        public ViewHolderImage(@NonNull View itemView) {
            super(itemView);
            imageThrobber = itemView.findViewById(R.id.imageThrobber);
            imageView = itemView.findViewById(R.id.imageView);
            imageErrorView = itemView.findViewById(R.id.imageErrorView);
        }
    }

    static class ViewHolderAudio extends ViewHolderCommon {
        public ViewHolderAudio(@NonNull View itemView) {
            super(itemView);
        }
    }

    static class ViewHolderVideo extends ViewHolderCommon {
        public ViewHolderVideo(@NonNull View itemView) {
            super(itemView);
        }
    }

    static class ViewHolderCommon extends RecyclerView.ViewHolder {
        TextView titleView;
        public ViewHolderCommon(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.titleView);
        }
    }
}
