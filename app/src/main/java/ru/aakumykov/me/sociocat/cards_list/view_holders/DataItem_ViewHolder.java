package ru.aakumykov.me.sociocat.cards_list.view_holders;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import ru.aakumykov.me.sociocat.MyApp;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_list.iCardsList;
import ru.aakumykov.me.sociocat.cards_list.list_items.DataItem;
import ru.aakumykov.me.sociocat.cards_list.list_items.ListItem;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class DataItem_ViewHolder extends BasicViewHolder {

    @BindView(R.id.elementView) ViewGroup elementView;
    @BindView(R.id.titleView) TextView titleView;
    @Nullable @BindView(R.id.countView) TextView countView;
    @Nullable @BindView(R.id.imageView) ImageView imageView;
    @Nullable @BindView(R.id.quoteView) TextView quoteView;

    private static final String TAG = DataItem_ViewHolder.class.getSimpleName();
    private DataItem dataItem;
    private iCardsList.ViewMode currentViewMode;
    private int neutralStateColor = -1;


    // Конструктор
    public DataItem_ViewHolder(View itemView, iCardsList.ViewMode viewMode) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        currentViewMode = viewMode;
    }


    // Заполнение данными
    public void initialize(ListItem listItem) {
        this.dataItem = (DataItem) listItem;

        Card card = (Card) dataItem.getPayload();

        titleView.setText(card.getTitle());

        if (null != imageView) {
            if (card.isImageCard()) {
                MyUtils.show(imageView);

                Glide.with(imageView).load(card.getImageURL())
                        .error(R.drawable.ic_image_error)
                        .into(imageView);
            }
            else {
                MyUtils.hide(imageView);
            }
        }

        if (null != quoteView) {
            if (card.isTextCard()) {
                quoteView.setText(card.getQuote());
            }
            else {
                MyUtils.hide(quoteView);
            }
        }
    }

    @Override
    public void setViewState(iCardsList.ItemState itemState) {
        switch (itemState) {
            case NEUTRAL:
                applyNeutralState();
                break;
            case SELECTED:
                applySelectedState();
                break;
            case DELETING:
                applyDeletingState();
                break;
            default:
                Log.e(TAG, "Unknown eViewHolderState: "+ itemState);
        }
    }


    // Нажатия
    @OnClick(R.id.elementView)
    void onItemClicked() {
        presenter.onDataItemClicked(this.dataItem);
    }

    @OnLongClick(R.id.elementView)
    void onItemLongClicked() {
        presenter.onDataItemLongClicked(this.dataItem);
    }


    // Внутренние
    private void applySelectedState() {
        int selectedColor = elementView.getResources().getColor(R.color.element_is_selected);

        switch (currentViewMode) {
            case LIST:
                elementView.setBackgroundResource(R.drawable.list_item_selected);
                break;

            default:
                ((CardView) elementView).setCardBackgroundColor(selectedColor);
                break;
        }
    }

    private void applyDeletingState() {
        int deletingStateColor = elementView.getResources().getColor(R.color.element_is_now_deleting);

        switch (currentViewMode) {
            case LIST:
                elementView.setBackgroundResource(R.drawable.list_item_deleting);
                break;

            default:
                ((CardView)elementView).setCardBackgroundColor(deletingStateColor);
                break;
        }
    }

    private void applyNeutralState() {
        switch (currentViewMode) {
            case LIST:
                elementView.setBackgroundResource(R.drawable.list_item_neutral);
                break;

            default:
                ((CardView)elementView).setCardBackgroundColor(neutralStateColor);
                break;
        }
    }

}
