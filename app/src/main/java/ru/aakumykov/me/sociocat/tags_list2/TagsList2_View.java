package ru.aakumykov.me.sociocat.tags_list2;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.Tag;

public class TagsList2_View extends BaseView implements iTagsList2.iPageView {

    private TagsList2_DataAdapter dataAdapter;
    private iTagsList2.iPresenter presenter;
    private boolean dryRun = true;

    @BindView(R.id.recyclerView) RecyclerView recyclerView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tags_list2_activity);
        ButterKnife.bind(this);

        dataAdapter = new TagsList2_DataAdapter();
        presenter = new TagsList2_Presenter();

        recyclerView.setAdapter(dataAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        presenter.bindViews(this, dataAdapter);

        if (dryRun) {
            dryRun = false;
            presenter.startWork();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unbindViews();
    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }

    @Override
    public void goShowTag(Tag tag) {

    }
}
