package ru.aakumykov.me.mvp.tags.list;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.lujun.androidtagview.TagContainerLayout;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.models.Tag;
import ru.aakumykov.me.mvp.tags.Tags_Presenter;
import ru.aakumykov.me.mvp.tags.iTags;

public class TagsList_View extends BaseView implements
        iTags.ListView
{
    @BindView(R.id.listView) ListView listView;

    private final static String TAG = "TagsList_View";
    private iTags.Presenter presenter;
    private List<Tag> tagsList = new ArrayList<>();
    private TagsListAdapter tagsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tags_list_activity);
        ButterKnife.bind(this);

        tagsListAdapter = new TagsListAdapter(this, R.layout.tags_list_item, tagsList);
        listView.setAdapter(tagsListAdapter);

        presenter = new Tags_Presenter();
        presenter.onPageCreated();
    }


    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);
    }
    @Override
    protected void onStop() {
        super.onStop();
        presenter.unlinkView();
    }
    @Override
    public void onServiceBounded() {

    }
    @Override
    public void onServiceUnbounded() {

    }


    // Внешние методы
    @Override
    public void displayTags(List<Tag> list) {
        Log.d(TAG, "displayTags(), "+list);
        tagsList.addAll(list);
        tagsListAdapter.notifyDataSetChanged();
    }



}
