package ru.aakumykov.me.mvp.cards_list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.models.Card;

public class CardsListAdapter extends ArrayAdapter<Card> {

    private LayoutInflater inflater;
    private int layout;
    private List<Card> cards;

    CardsListAdapter(Context context, int resource, List<Card> cards) {
        super(context, resource, cards);
        this.cards = cards;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull  ViewGroup parent) {

        View view = inflater.inflate(this.layout, parent, false);
        TextView titleView = view.findViewById(R.id.titleView);

        Card card = cards.get(position);
        titleView.setText(card.getTitle());

        return view;
    }
}