package ru.aakumykov.me.sociocat.tags_list2;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.Tag;

public class TagsList2_ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tagItem) View tagItem;
    @BindView(R.id.nameView) TextView nameView;
    @BindView(R.id.counterView) TextView counterView;

    TagsList2_ViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void initialize(Tag tag, iTagsList2.TagItemClickListener tagItemClickListener) {
        nameView.setText(tag.getName());

        int cardsCount = tag.getCards().keySet().size();
        counterView.setText(String.valueOf(cardsCount));

        tagItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tagItemClickListener.onTagClicked(tag);
            }
        });
    }
}
