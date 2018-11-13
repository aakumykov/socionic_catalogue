package ru.aakumykov.me.mvp.comment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.models.Comment;
import ru.aakumykov.me.mvp.models.Comment;

public class CommentsListAdapter extends ArrayAdapter<Comment> {

    // TODO: реализовать ViewHolder

    private final static String TAG = "CommentsListAdapter";
    private LayoutInflater inflater;
    private int layout;
    private List<Comment> cards;

    public CommentsListAdapter(Context context, int resource, List<Comment> cards) {
        super(context, resource, cards);
        this.cards = cards;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View view = inflater.inflate(this.layout, parent, false);
        TextView titleView = view.findViewById(R.id.titleView);

        Comment comment = cards.get(position);
        titleView.setText(comment.getText());

        return view;
    }
    
}
