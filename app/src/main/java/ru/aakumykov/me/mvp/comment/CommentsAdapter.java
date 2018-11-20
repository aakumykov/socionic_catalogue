package ru.aakumykov.me.mvp.comment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.models.Comment;
import ru.aakumykov.me.mvp.utils.MyUtils;

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

        final TextView commentTextView = view.findViewById(R.id.commentText);
        commentTextView.setText(comment.getText());

        // Если это ответ...
        if (null != comment.getParentId()) {
            ImageView commentReplyMark = view.findViewById(R.id.commentReplyMark);
            TextView commentParentQuote = view.findViewById(R.id.parentComment);
            MyUtils.show(commentReplyMark);
            MyUtils.show(commentParentQuote);
        }


        TextView commentAuthorView = view.findViewById(R.id.commentAuthor);
        commentAuthorView.setText(comment.getUserName());

        TextView parentCommentView = view.findViewById(R.id.parentComment);
        parentCommentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String initialText = v.getResources().getString(R.string.COMMENT_show_quote);
                TextView textView = (TextView)v;
                String text = textView.getText().toString();

                if (text.equals(initialText)) textView.setText(comment.getParentText());
                else textView.setText(initialText);
            }
        });

        TextView commentRatingView = view.findViewById(R.id.commentRating);
        commentRatingView.setText( ""+comment.getRating() );

        ImageView commentMenu = view.findViewById(R.id.commentMenu);
        commentMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentClickListener.onCommentMenuClicked(v, comment);
            }
        });

        TextView commentReply = view.findViewById(R.id.commentReply);
        commentReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentClickListener.onCommentReplyClicked(v, comment);
            }
        });

        return view;
    }
    
}
