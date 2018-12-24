package ru.aakumykov.me.mvp.start_page;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.cards_list.CardsList_Fragment;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.tags.list.TagsList_Fragment;


public class StartPage extends BaseView implements
        ViewPager.OnPageChangeListener,
        TabLayout.OnTabSelectedListener
{
    @BindView(R.id.viewPager) ViewPager viewPager;
    @BindView(R.id.tabLayout) TabLayout tabLayout;

    private CardsList_Fragment cardsListFragment;
    private TagsList_Fragment tagsListFragment;
    private FragmentManager fragmentManager;
    private StartPage_PagerAdapter startPagePagerAdapter;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode) {

            case Constants.CODE_CREATE_CARD:
                if (RESULT_OK==resultCode) cardsListFragment.processCardCreationResult(data);
                break;

            case Constants.CODE_EDIT_CARD:
                if (RESULT_OK==resultCode) cardsListFragment.processCardEditionResult(data);
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_page_activity);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar)
            actionBar.setDisplayShowTitleEnabled(false);

        cardsListFragment = new CardsList_Fragment();
        tagsListFragment = new TagsList_Fragment();

        // Заготовка для установки заголовка страницы. Но как её использовать?
        HashMap<Integer,Fragment> fragmentsMap = new HashMap<>();
        fragmentsMap.put(R.string.CARDS_LIST_page_title, cardsListFragment);
        fragmentsMap.put(R.string.TAGS_LIST_page_title, tagsListFragment);

        fragmentManager = getSupportFragmentManager();
        startPagePagerAdapter = new StartPage_PagerAdapter(fragmentManager, fragmentsMap);
        viewPager.setAdapter(startPagePagerAdapter);

        viewPager.addOnPageChangeListener(this);
        tabLayout.addOnTabSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // эта  строчка обязана предшествовать методу setupSpinner()
        getMenuInflater().inflate(R.menu.spinner_menu, menu);
        setupSpinner(menu);

        if (auth().isUserLoggedIn()) {
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.create_card, menu);
            menuInflater.inflate(R.menu.refresh, menu);
        }

        super.onCreateOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.actionRefresh:
//                loadList(true);
                break;

            default:
                super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void onUserLogin() {

    }
    @Override
    public void onUserLogout() {

    }


    // Методы обратнаго вызова Листателя
    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        TabLayout.Tab tab = tabLayout.getTabAt(i);
        if (null != tab) tab.select();
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }


    // Методы обратного вызова Вкладок
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int position = tab.getPosition();
        viewPager.setCurrentItem(position);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    // Внутренние методы
    private void setupSpinner(Menu menu) {
        List<String> list = new ArrayList<>();
        list.add("Карточки");
        list.add("Метки");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        MenuItem menuItem = menu.findItem(R.id.spinner);
        View view = menuItem.getActionView();
        if (view instanceof Spinner) {
            Spinner spinner = (Spinner) menuItem.getActionView();
            spinner.setAdapter(adapter);
        }
    }
}
