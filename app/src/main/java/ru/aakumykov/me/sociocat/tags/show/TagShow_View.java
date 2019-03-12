package ru.aakumykov.me.sociocat.tags.show;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.utils.MyUtils;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_list.CardsList_View;
import ru.aakumykov.me.sociocat.models.Tag;
import ru.aakumykov.me.sociocat.tags.Tags_Presenter;
import ru.aakumykov.me.sociocat.tags.iTags;


// TODO: попробовать extends iBaseView

public class TagShow_View extends BaseView implements
        iTags.ShowView,
        View.OnClickListener
{
    @BindView(R.id.nameView) TextView nameView;
    @BindView(R.id.viewCardsButton) Button viewCardsButton;

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
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.viewCardsButton:
                goCardsListPage(currentTag.getKey());
                break;

            default:
                break;
        }
    }


    // Обязательные методы
    @Override
    public void onUserLogin() {

    }
    @Override
    public void onUserLogout() {

    }


    // Интерфейсныне методы
    @Override
    public void displayTag(final Tag tag) {
        Log.d(TAG, "displayTag(), "+tag);

        currentTag = tag;

        hideProgressBar();

        String pageTitle = getString(R.string.TAG_SHOW_page_title, tag.getName());
        setPageTitle(pageTitle);

        String tagName = getResources()
                .getString(R.string.aquotes, tag.getName());
        nameView.setText(tagName);

        int cardsCount =  tag.getCards().size();
        String linkText = getResources()
                .getQuantityString(
                        R.plurals.cards_count,
                        cardsCount,
                        cardsCount
                );

        viewCardsButton.setText(linkText);
        MyUtils.show(viewCardsButton);

        if (cardsCount > 0) {
            viewCardsButton.setOnClickListener(this);
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
