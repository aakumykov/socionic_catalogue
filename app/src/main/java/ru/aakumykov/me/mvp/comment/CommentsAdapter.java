package ru.aakumykov.me.mvp.comment;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.models.Comment;
import ru.aakumykov.me.mvp.users.show.UserShow_View;
import ru.aakumykov.me.mvp.utils.MyUtils;

public class CommentsAdapter extends ArrayAdapter<Comment> {

    // TODO: реализовать ViewHolder

    private final static String TAG = "CommentsAdapter";
    private Context context;
    private LayoutInflater inflater;
    private int layout;
    private List<Comment> list;
    private iComments.commentClickListener commentClickListener; // ОПАСНО, если адаптер живуч

    public CommentsAdapter(Context context, int resource, List<Comment> commentsList,
                           iComments.commentClickListener commentClickListener) {
        super(context, resource, commentsList);

        this.context = context;
        this.list = commentsList;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
        this.commentClickListener = commentClickListener;
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        final Comment comment = list.get(position);

        final View commentItemView = inflater.inflate(this.layout, parent, false);

        final TextView commentTextView = commentItemView.findViewById(R.id.commentText);
        commentTextView.setText(comment.getText());

        // Если это ответ...
        if (null != comment.getParentId()) {
            ImageView commentReplyMark = commentItemView.findViewById(R.id.commentReplyMark);
            TextView commentParentQuote = commentItemView.findViewById(R.id.parentComment);
            MyUtils.show(commentReplyMark);
            MyUtils.show(commentParentQuote);
        }


        ImageView commentAvatar = commentItemView.findViewById(R.id.commentAvatar);
        commentAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seeCommentAuthorProfile(comment);
            }
        });

        TextView commentAuthorView = commentItemView.findViewById(R.id.commentAuthor);
        commentAuthorView.setText(comment.getUserName());
        commentAuthorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seeCommentAuthorProfile(comment);
            }
        });

        TextView parentCommentView = commentItemView.findViewById(R.id.parentComment);
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

        TextView commentRatingView = commentItemView.findViewById(R.id.commentRatingView);
        commentRatingView.setText( ""+comment.getRating() );

        ImageView commentMenu = commentItemView.findViewById(R.id.commentMenu);
        commentMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentClickListener.onCommentMenuClicked(v, comment);
            }
        });

        TextView commentReply = commentItemView.findViewById(R.id.commentReply);
        commentReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentClickListener.onCommentReplyClicked(v, comment);
            }
        });


        ImageView commentRateUpButton = commentItemView.findViewById(R.id.commentRateUpButton);
        commentRateUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentClickListener.onCommentRateUpClicked(comment);
            }
        });

        ImageView commentRateDownButton = commentItemView.findViewById(R.id.commentRateDownButton);
        commentRateDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentClickListener.onCommentRateDownClicked(comment);
            }
        });

        return commentItemView;
    }

    public void commentRatingChanged(Comment comment, Comment oldComment) {
        int index = list.indexOf(oldComment);
        list.set(index, comment);
    }

    // TODO: после рагистрации текущий пользователь не имеет имени (в комментах не появляется иени).
    private void seeCommentAuthorProfile(Comment comment) {
        Intent intent = new Intent(context, UserShow_View.class);
        intent.putExtra(Constants.USER_ID, comment.getUserId());
        context.startActivity(intent);
    }
}
