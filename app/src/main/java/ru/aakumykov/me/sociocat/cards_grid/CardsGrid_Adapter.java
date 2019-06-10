package ru.aakumykov.me.sociocat.cards_grid;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.singletons.iAuthSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class CardsGrid_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements Filterable
{
    public interface iAdapterUser {
        void onGridItemClick(int position);
        void onPopupMenuClick(MenuItem menuItem, int listPosition);
        void onDataFiltered(List<Card> filteredCardsList);
    }

    private static final String TAG = "CardsGrid_Adapter";

    private static final int VIEW_TYPE_TEXT_CARD = 10;
    private static final int VIEW_TYPE_IMAGE_CARD = 20;
    private static final int VIEW_TYPE_AUDIO_CARD = 30;
    private static final int VIEW_TYPE_VIDEO_CARD = 40;

    private List<Card> cardsList;
    private List<Card> originalCardsList;
    private List<Card> cardsListFiltered;
    private iAdapterUser adapterUser;

    private Card currentCard; // Используется для отладки ошибки.

    private iAuthSingleton authSingleton = AuthSingleton.getInstance();
    private iUsersSingleton usersSingleton = UsersSingleton.getInstance();

    // Конструктор
    CardsGrid_Adapter(List<Card> cardsList) {
        this.cardsList = cardsList;
        this.originalCardsList = this.cardsList;
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
        currentCard = card;
        //Log.e(TAG, "Card: "+card);
        //Log.e(TAG, card.getKey()+" | "+card.getTitle()+" | "+card.getType());

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
                Log.e(TAG, "UNKNOWN CARD TYPE: "+card);
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

                if (null != adapterUser) { // Иначе падает при входе со страницы плиток
                    adapterUser.onDataFiltered(cardsList);
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
                // TODO: регистрировать это событие
                Log.e(TAG, "Unknown view type of card: "+currentCard);
                view = layoutInflater.inflate(R.layout.cards_grid_item_unknown, parent, false);
                return new ViewHolderUnknown(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int listPosition) {

        ViewHolderCommon viewHolderCommon = (ViewHolderCommon) holder;

        viewHolderCommon.cardView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                adapterUser.onGridItemClick(listPosition);
            }
        });

        viewHolderCommon.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override public boolean onLongClick(View view) {
                showPopupMenu(view, listPosition, viewHolderCommon);
                return true;
            }
        });

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
    void bindView(iAdapterUser consumer) {
        this.adapterUser = consumer;
    }

    void unbindView() {
        this.adapterUser = null;
    }

    void restoreInitialList() {
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
                MyUtils.hide(viewHolder.imageThrobber);
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


    private void showPopupMenu(View view, int listPosition, ViewHolderCommon viewHolderCommon) {

        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);

        User currentUser = usersSingleton.getCurrentUser();
        Card currentCard = cardsList.get(listPosition);

        if (null != currentUser) {
            if (usersSingleton.currentUserIsAdmin() || usersSingleton.isCardOwner(currentCard)) {
                popupMenu.inflate(R.menu.edit);
                popupMenu.inflate(R.menu.delete);
            }
        }
        else {
            popupMenu.inflate(R.menu.share);
        }

        viewHolderCommon.saveOriginalBackground();
        viewHolderCommon.setPressedBackground();

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                viewHolderCommon.restoreBackground();
            }
        });

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                adapterUser.onPopupMenuClick(item, listPosition);
                return true;
            }
        });

        popupMenu.show();
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

    static class ViewHolderUnknown extends ViewHolderCommon {
        public ViewHolderUnknown(@NonNull View itemView) {
            super(itemView);
        }
    }

    static class ViewHolderCommon extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView titleView;
        int oldBackgroundColor;

        ViewHolderCommon(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            titleView = itemView.findViewById(R.id.titleView);
        }

        void saveOriginalBackground() {
            oldBackgroundColor = cardView.getCardBackgroundColor().getDefaultColor();
        }

        void setPressedBackground() {
            int color = cardView.getContext().getResources().getColor(R.color.selected_list_item_bg);
            cardView.setCardBackgroundColor(color);
        }

        void restoreBackground() {
            cardView.setCardBackgroundColor(oldBackgroundColor);
        }
    }

}
