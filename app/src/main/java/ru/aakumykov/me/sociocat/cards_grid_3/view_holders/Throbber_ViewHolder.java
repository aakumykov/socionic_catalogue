package ru.aakumykov.me.sociocat.cards_grid_3.view_holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.R;

public class Throbber_ViewHolder extends RecyclerView.ViewHolder {

    public Throbber_ViewHolder(@NonNull View itemView) {
        super(itemView);
        //ButterKnife.bind(this, itemView);
    }

    public void initialize() {
        //mTitleView.setText(R.string.CARDS_GRID_load_more);
    }


}
