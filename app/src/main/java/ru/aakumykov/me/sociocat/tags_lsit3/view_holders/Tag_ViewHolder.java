package ru.aakumykov.me.sociocat.tags_lsit3.view_holders;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.Tag;
import ru.aakumykov.me.sociocat.tags_lsit3.iTagsList3;

public class Tag_ViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener
{
    private View tagItem;
    private TextView nameView;
    private TextView countView;

    private iTagsList3.iPresenter presenter;
    private Tag tag;

    public Tag_ViewHolder(View itemView, iTagsList3.iPresenter presenter) {
        super(itemView);

        tagItem = itemView.findViewById(R.id.tagItem);
        nameView = itemView.findViewById(R.id.nameView);
        countView = itemView.findViewById(R.id.countView);

        tagItem.setOnClickListener(this);

        this.presenter = presenter;
    }

    public void initialize(Object payload) {
        this.tag = (Tag) payload;

        if (null != nameView)
            nameView.setText(tag.getName());

        if (null != countView)
            countView.setText(String.valueOf(tag.getCards().size()));
    }

    // View.OnClickListener
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tagItem:
                presenter.onTagClicked(tag);
                break;
            default:
                break;
        }
    }
}
