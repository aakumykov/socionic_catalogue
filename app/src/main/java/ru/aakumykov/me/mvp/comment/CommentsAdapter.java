package ru.aakumykov.me.mvp.comment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.models.Comment;

public class CommentsAdapter extends ArrayAdapter<Comment> {

    // TODO: реализовать ViewHolder

    private final static String TAG = "CommentsAdapter";
    private LayoutInflater inflater;
    private int layout;
    private List<Comment> list;
    private iComments.commentClickListener commentClickListener; // ОПАСНО, если адаптер живуч

    public CommentsAdapter(Context context, int resource, List<Comment> commentsList,
                           iComments.commentClickListener commentClickListener) {
        super(context, resource, commentsList);
        this.list = commentsList;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
        this.commentClickListener = commentClickListener;
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        final Comment comment = list.get(position);

        final View view = inflater.inflate(this.layout, parent, false);

        TextView commentTextView = view.findViewById(R.id.commentText);
        commentTextView.setText(comment.getText());

        TextView commentAuthorView = view.findViewById(R.id.commentAuthor);
        commentAuthorView.setText(comment.getUserName());

        ImageView commentMenu = view.findViewById(R.id.commentMenu);
        commentMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentClickListener.onCommentMenuClicked(view, comment);
            }
        });

        return view;
    }
    
}
