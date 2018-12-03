package ru.aakumykov.me.mvp.tags.list;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.mvp.BaseFragment;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iTagsSingleton;
import ru.aakumykov.me.mvp.models.Tag;
import ru.aakumykov.me.mvp.services.TagsSingleton;
import ru.aakumykov.me.mvp.tags.Tags_Presenter;
import ru.aakumykov.me.mvp.tags.iTags;
import ru.aakumykov.me.mvp.tags.view.TagShow_View;

public class TagsList_Fragment extends BaseFragment implements
    iTags.ListView,
    AdapterView.OnItemClickListener
{
    private final static String TAG = "TagsList_Fragment";

    private iTagsSingleton tagsService = TagsSingleton.getInstance();
    private List<Tag> tagsList = new ArrayList<>();
    private TagsListAdapter tagsListAdapter;
    @BindView(R.id.listView) ListView listView;

    private iTags.Presenter presenter;
    private boolean firstRun = true;


    // Системные методы
    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = getLayoutInflater().inflate(R.layout.tags_list, container, false);
        ButterKnife.bind(this, rootView);

        tagsListAdapter = new TagsListAdapter(getContext(), R.layout.tags_list_item, tagsList);
        listView.setAdapter(tagsListAdapter);

        presenter = new Tags_Presenter();

        setRootView(rootView);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        presenter.linkView(this);

        if (firstRun) {
            presenter.loadList();
            firstRun = false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.unlinkView();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Tag tag = tagsList.get(position);
        presenter.onTagClicked(tag);
    }


    // Интерфейсные методы
    @Override
    public void displayTags(List<Tag> list) {
//        Log.d(TAG, "displayTags(), "+list);
        tagsList.addAll(list);
        tagsListAdapter.notifyDataSetChanged();
    }

    @Override
    public void goShowPage(String tagId) {
        Intent intent = new Intent(getContext(), TagShow_View.class);
        intent.putExtra(Constants.TAG_KEY, tagId);
        startActivity(intent);
    }


    // Обязательные методы
    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }

}
