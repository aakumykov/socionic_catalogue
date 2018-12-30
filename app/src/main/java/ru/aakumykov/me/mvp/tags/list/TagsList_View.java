package ru.aakumykov.me.mvp.tags.list;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.models.Tag;
import ru.aakumykov.me.mvp.tags.Tags_Presenter;
import ru.aakumykov.me.mvp.tags.iTags;
import ru.aakumykov.me.mvp.tags.show.TagShow_View;

public class TagsList_View extends BaseView implements
        iTags.ListView,
        AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener
{
    @BindView(R.id.swiperefresh) SwipeRefreshLayout swiperefreshLayout;
    @BindView(R.id.listView) ListView listView;

    private final static String TAG = "TagsList_View";
    private iTags.Presenter presenter;
    private List<Tag> tagsList = new ArrayList<>();
    private TagsListAdapter tagsListAdapter;


    // Системные методы
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tags_list);
        ButterKnife.bind(this);

        setPageTitle(R.string.TAGS_LIST_page_title);

        swiperefreshLayout.setOnRefreshListener(this);
        swiperefreshLayout.setColorSchemeResources(R.color.blue_swipe, R.color.green_swipe, R.color.orange_swipe, R.color.red_swipe);

        tagsListAdapter = new TagsListAdapter(this, R.layout.tags_list_item, tagsList);
        listView.setAdapter(tagsListAdapter);

        listView.setOnItemClickListener(this);

        presenter = new Tags_Presenter();
        presenter.loadList();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

//        menuInflater.inflate(R.menu.create_card, menu);
        menuInflater.inflate(R.menu.cards, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Tag tag = tagsList.get(position);
        presenter.onTagClicked(tag);
    }

    @Override
    public void onRefresh() {
        swiperefreshLayout.setRefreshing(true);
        presenter.loadList();
    }


    // Интерфейсные методы
    @Override
    public void displayTags(List<Tag> list) {
        tagsList.clear();
        tagsList.addAll(list);
        tagsListAdapter.notifyDataSetChanged();
    }

    @Override
    public void goShowPage(String tagId) {
        Intent intent = new Intent(this, TagShow_View.class);
        intent.putExtra(Constants.TAG_KEY, tagId);
        startActivity(intent);
    }

    @Override
    public void hideSwipeRefresh() {
        swiperefreshLayout.setRefreshing(false);
    }


    // Обязательные методы
    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }

}
