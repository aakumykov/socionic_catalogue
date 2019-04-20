package ru.aakumykov.me.sociocat.cards_list;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.Card;

public class CardsListAdapter extends ArrayAdapter<Card> {

    // TODO: реализовать ViewHolder

    private final static String TAG = "CardsListAdapter";
    private LayoutInflater inflater;
    private int layout;
    private List<Card> cards;

    public CardsListAdapter(Context context, int resource, List<Card> cards) {
        super(context, resource, cards);
//        Log.d(TAG, "== new CardsListAdapter(), cards: "+cards);
        this.cards = cards;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull  ViewGroup parent) {

        View view = inflater.inflate(this.layout, parent, false);

        Card card = cards.get(position);

        // Title
        TextView titleView = view.findViewById(R.id.titleView);
        titleView.setText(card.getTitle());

        TextView authorView  = view.findViewById(R.id.authorView);
        authorView.setText(view.getResources().getString(R.string.CARDS_LIST_author, card.getUserName()));

        // Comments count
        int cc = card.getCommentsCount();
        if (cc > 0) {
            TextView commentsCountView = view.findViewById(R.id.commentsCountView);
            String ccText = view.getResources().getString(R.string.CARDS_LIST_comments_count, cc);
            commentsCountView.setText(ccText);
        }

        return view;
    }
}