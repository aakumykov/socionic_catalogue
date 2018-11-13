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

public class CommentsAdapter extends ArrayAdapter<Comment> {

    // TODO: реализовать ViewHolder

    private final static String TAG = "CommentsAdapter";
    private LayoutInflater inflater;
    private int layout;
    private List<Comment> list;

    public CommentsAdapter(Context context, int resource, List<Comment> commentsList) {
        super(context, resource, commentsList);
        this.list = commentsList;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View view = inflater.inflate(this.layout, parent, false);
        TextView commentTextView = view.findViewById(R.id.commentTextView);

        Comment comment = list.get(position);
        commentTextView.setText(comment.getText());

        return view;
    }
    
}
