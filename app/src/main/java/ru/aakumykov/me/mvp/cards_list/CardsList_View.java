package ru.aakumykov.me.mvp.cards_list;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import butterknife.BindView;
import ru.aakumykov.me.mvp.R;

public class CardsList_View extends AppCompatActivity {

    private final static String TAG = "cards_list";

    @BindView(R.id.listView) ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
