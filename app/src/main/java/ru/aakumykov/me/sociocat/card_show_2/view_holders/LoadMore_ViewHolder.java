package ru.aakumykov.me.sociocat.card_show_2.view_holders;

import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show_2.iCommentsController;
import ru.aakumykov.me.sociocat.models.Comment;

public class LoadMore_ViewHolder extends Base_ViewHolder {

    private final static String TAG = "LoadMore_ViewHolder";
    @BindView(R.id.textView) TextView textView;


    public LoadMore_ViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void initialize(Comment item, iCommentsController commentsController) {

//        commentsController.loadComments(item.getKey()+1, 10);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //commentsController.loadComments(item.getKey(), 10);
            }
        });
    }


}

