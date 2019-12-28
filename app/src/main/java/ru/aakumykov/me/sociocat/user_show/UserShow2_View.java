package ru.aakumykov.me.sociocat.user_show;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.user_show.models.Item;
import ru.aakumykov.me.sociocat.user_show.view_model.UserShow_ViewModel;
import ru.aakumykov.me.sociocat.user_show.view_model.UserShow_ViewModelFactory;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class UserShow2_View extends BaseView implements iUserShow.iView {

    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.nameView) TextView nameView;
    @BindView(R.id.emailView) TextView emailView;
    @BindView(R.id.aboutView) TextView aboutView;
    @BindView(R.id.avatarView) ImageView avatarView;
    @BindView(R.id.avatarThrobber) ProgressBar avatarThrobber;

    private iUserShow.iPresenter presenter;

    // Activity
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_show_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.PAGE_TEMPLATE_page_title);
        activateUpButton();

        UserShow_ViewModel viewModel = new ViewModelProvider(this, new UserShow_ViewModelFactory())
                .get(UserShow_ViewModel.class);

        if (viewModel.hasPresenter()) {
            this.presenter = viewModel.getPresenter();
        } else {
            this.presenter = new UserShow_Presenter();
            viewModel.storePresenter(this.presenter);
        }

        configureSwipeRefresh();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);

        if (presenter.hasUser())
            presenter.onConfigChanged();
        else
            presenter.onFirstOpen(getIntent());
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unlinkView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (presenter.canEditUser())
            getMenuInflater().inflate(R.menu.edit, menu);

        return super.onCreateOptionsMenu(menu);
    }


    // BaseView
    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }


    // iUserShow.iView
    @Override
    public void displayUser(User user) {
        loadAndShowAvatar(user);

        nameView.setText(user.getName());
        emailView.setText(user.getEmail());
        aboutView.setText(user.getAbout());
    }

    @Override
    public void hideRefreshThrobber() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showAvatarTrobber() {
        MyUtils.show(avatarThrobber);
    }

    @Override
    public void hideAvatarThrobber() {
        MyUtils.hide(avatarThrobber, true);
    }


    // Внутренние методы
    private void configureSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                presenter.onRefreshRequested();
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.blue_swipe, R.color.green_swipe, R.color.orange_swipe, R.color.red_swipe);
    }

    private void loadAndShowAvatar(User user) {
        MyUtils.show(avatarThrobber);

        Glide.with(this).load(user.getAvatarURL()).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                MyUtils.hide(avatarThrobber);
                avatarView.setImageDrawable(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                avatarView.setImageResource(R.drawable.ic_avatar_placeholder);
            }
        });
    }
}
