package ru.aakumykov.me.mvp.start_page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.mvp.cards_list.CardsList_Fragment;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.TagsListFragment;


public class StartPage extends AppCompatActivity implements
    ViewPager.OnPageChangeListener,
    TabLayout.OnTabSelectedListener
{
    @BindView(R.id.viewPager) ViewPager viewPager;
    @BindView(R.id.tabLayout) TabLayout tabLayout;

    private FragmentManager fragmentManager;
    private StartPage_PagerAdapter startPagePagerAdapter;

//    private CardsList_Fragment cardsListFragment;
//    private TagsListFragment tagsListFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_page_activity);
        ButterKnife.bind(this);

        Fragment[] fragments = { new CardsList_Fragment(), new TagsListFragment() };

        fragmentManager = getSupportFragmentManager();
        startPagePagerAdapter = new StartPage_PagerAdapter(fragmentManager, fragments);
        viewPager.setAdapter(startPagePagerAdapter);

        viewPager.addOnPageChangeListener(this);
        tabLayout.addOnTabSelectedListener(this);
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
}
