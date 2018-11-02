package ru.aakumykov.me.mvp.tags.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.utils.MyUtils;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.cards_list.CardsList_View;
import ru.aakumykov.me.mvp.models.Tag;
import ru.aakumykov.me.mvp.tags.Tags_Presenter;
import ru.aakumykov.me.mvp.tags.iTags;


// TODO: попробовать extends iBaseView

public class TagShow_View extends BaseView implements
        iTags.ShowView,
        View.OnClickListener
{
    @BindView(R.id.nameView) TextView nameView;
    @BindView(R.id.linkToCardsView) TextView linkToCardsView;

    private final static String TAG = "TagShow_View";
    private iTags.Presenter presenter;
    private Tag currentTag;

    // Системные методы
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_show_activity);
        ButterKnife.bind(this);

        presenter = new Tags_Presenter();

        processIntent();
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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.linkToCardsView:
                goCardsListPage(currentTag.getKey());
                break;
            default:
                break;
        }
    }

    // Главные методы
    @Override
    public void displayTag(final Tag tag) {
        Log.d(TAG, "displayTag(), "+tag);

        currentTag = tag;

        hideProgressBar();

        String pageTitle = getString(R.string.TAG_SHOW_page_title, tag.getName());
        setPageTitle(pageTitle);

        String tagName = getResources()
                .getString(R.string.braces, tag.getName());
        nameView.setText(tagName);

        int cardsCount =  tag.getCards().size();
        String linkText = getResources()
                .getQuantityString(
                        R.plurals.cards_count,
                        cardsCount,
                        cardsCount
                );
        linkToCardsView.setText(linkText);
        MyUtils.show(linkToCardsView);

        if (cardsCount > 0) {
            linkToCardsView.setOnClickListener(this);
        }
    }

    @Override
    public void goCardsListPage(@Nullable String tagFilter) {
        Log.d(TAG, "goCardsListPage('"+tagFilter+"'");
        Intent intent = new Intent(this, CardsList_View.class);
        intent.putExtra(Constants.TAG_FILTER, tagFilter);
        startActivity(intent);
    }

    // Внутренние методы
    private void processIntent() {
        Intent intent = getIntent();
        String tagKey = intent.getStringExtra(Constants.TAG_KEY);
        presenter.onShowPageReady(tagKey);
    }
}
