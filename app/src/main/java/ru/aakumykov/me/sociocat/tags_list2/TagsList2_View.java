package ru.aakumykov.me.sociocat.tags_list2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_grid.CardsGrid_View;
import ru.aakumykov.me.sociocat.models.Tag;

public class TagsList2_View extends BaseView implements
        iTagsList2.iPageView,
        iTagsList2.TagItemClickListener
{
    private TagsList2_DataAdapter dataAdapter;
    private LinearLayoutManager linearLayoutManager;
    private iTagsList2.iPresenter presenter;
    private boolean dryRun = true;

    @BindView(R.id.recyclerView) RecyclerView recyclerView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tags_list2_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.TAGS_LIST_page_title);
        activateUpButton();

        dataAdapter = new TagsList2_DataAdapter(this);
        linearLayoutManager = new LinearLayoutManager(this);
        presenter = new TagsList2_Presenter();

        recyclerView.setAdapter(dataAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.sort_by_name, menu);
        menuInflater.inflate(R.menu.sort_by_count, menu);

        MenuItem menuItem = menu.findItem(R.id.actionTags);
        menuItem.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.actionSortByName:
                presenter.onSortByNameClicked(true);
                break;

            case R.id.actionSortByNameReverse:
                presenter.onSortByNameClicked(false);
                break;

            case R.id.actionSortByCount:
                presenter.onSortByCardsClicked(true);
                break;

            case R.id.actionSortByCountReverse:
                presenter.onSortByCardsClicked(false);
                break;

            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    // iTagsList2.iPageView
    @Override
    public void goShowTag(Tag tag) {
        Intent intent = new Intent(this, CardsGrid_View.class);
        intent.putExtra(Constants.TAG_NAME, tag.getName());
        startActivity(intent);
    }


    // iTagsList2.TagItemClickListener
    @Override
    public void onTagClicked(Tag tag) {
        presenter.onTagClicked(tag);
    }
}
