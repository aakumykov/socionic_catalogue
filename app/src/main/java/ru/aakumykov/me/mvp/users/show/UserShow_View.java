package ru.aakumykov.me.mvp.users.show;

import android.content.Intent;
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
import ru.aakumykov.me.mvp.users.Users_Presenter;
import ru.aakumykov.me.mvp.users.iUsers;

public class UserShow_View extends BaseView implements
        iUsers.ShowView
{
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.nameRow) LinearLayout nameRow;
    @BindView(R.id.emailRow) LinearLayout emailRow;
    @BindView(R.id.nameView) TextView nameView;
    @BindView(R.id.emailView) TextView emailView;

    private final static String TAG = "UserShow_View";
    private UsersSingleton usersSingleton = UsersSingleton.getInstance();
    private iUsers.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_page_activity);
        ButterKnife.bind(this);

        presenter = new Users_Presenter();

        Intent intent = getIntent();
        String userId = intent.getStringExtra(Constants.USER_ID);
        Log.d(TAG, "userId: "+userId);

        try {
            presenter.loadUser(userId);
        } catch (Exception e) {
            // TODO: всунуть сокрытие крутилки внутрь show*Message()
            hideProgressBar();
            showErrorMsg(R.string.error_displaying_user, e.getMessage());
            e.printStackTrace();
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
