package ru.aakumykov.me.mvp.user_page;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.MyUtils;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.models.User;
import ru.aakumykov.me.mvp.services.UsersSingleton;

public class UserPage_View extends BaseView implements
        iUserPage.View
{
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.nameRow) LinearLayout nameRow;
    @BindView(R.id.emailRow) LinearLayout emailRow;
    @BindView(R.id.nameView) TextView nameView;
    @BindView(R.id.emailView) TextView emailView;

    private final static String TAG = "UserPage_View";
    private UsersSingleton usersSingleton = UsersSingleton.getInstance();
    private iUserPage.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_page_activity);
        ButterKnife.bind(this);

        presenter = new UserPage_Presenter();

        Intent intent = getIntent();
        String userId = intent.getStringExtra(Constants.USER_ID);
        if (null == userId) {
            hideProgressBar();
            showErrorMsg(R.string.error_displaying_user, "userId == null");
        } else {
            presenter.userIdRecieved(userId);
        }
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
    public void displayUser(User user) {
        Log.d(TAG, "displayUser(), "+user);

        hideProgressBar();

        nameView.setText(user.getName());
        emailView.setText(user.getEmail());

        MyUtils.show(nameRow);
        MyUtils.show(emailRow);
    }


    //TODO: перенести в общий интерфейс
    void hideProgressBar() {
        MyUtils.hide(progressBar);
    }
}
