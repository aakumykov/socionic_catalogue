package ru.aakumykov.me.mvp.start_page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.mvp.R;


public class StartPage extends AppCompatActivity {

    @BindView(R.id.viewPager) ViewPager viewPager;
    @BindView(R.id.tabLayout) TabLayout tabLayout;

    private FragmentManager fragmentManager;
    private PagerAdapter pagerAdapter;

//    private CardsFragment cardsFragment;
//    private TagsFragment tagsFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_page_activity);
        ButterKnife.bind(this);


    }


}
