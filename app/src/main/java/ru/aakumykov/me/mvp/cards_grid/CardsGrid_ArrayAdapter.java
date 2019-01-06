package ru.aakumykov.me.mvp.cards_grid;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

public class CardsGrid_ArrayAdapter extends ArrayAdapter<Card> {

    private LayoutInflater inflater;
    private int layout;
    private List<Card> list;

    CardsGrid_ArrayAdapter(Context context, int resource, List<Card> list) {
        super(context, resource, list);
        this.list = list;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;
        if(convertView==null){
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Card card = list.get(position);

        viewHolder.titleView.setText(card.getTitle());

        if (card.isTextCard()) {
            String quote = MyUtils.cutToLength(card.getQuote(), Constants.CARDS_GRID_QUOTE_MAX_LENGTH);
            viewHolder.quoteView.setText(quote);
            MyUtils.show(viewHolder.quoteView);
        }

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


        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.titleView) TextView titleView;
        @BindView(R.id.quoteView) TextView quoteView;
        @BindView(R.id.imageView) ImageView imageView;

        ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}