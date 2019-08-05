package ru.aakumykov.me.sociocat.users.show;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.CardShow_View;
import ru.aakumykov.me.sociocat.cards_list.CardsListAdapter;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.users.Users_Presenter;
import ru.aakumykov.me.sociocat.users.edit.UserEdit_View;
import ru.aakumykov.me.sociocat.users.iUsers;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class UserShow_View extends BaseView implements
        iUsers.ShowView,
        iUsersSingleton.ReadCallbacks,
        AdapterView.OnItemClickListener
{
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.nameView) TextView nameView;
    @BindView(R.id.emailView) TextView emailView;
    @BindView(R.id.aboutView) TextView aboutView;
    @BindView(R.id.avatarView) ImageView avatarView;
    @BindView(R.id.avatarThrobber) ProgressBar avatarThrobber;

    private final static String TAG = "UserShow_View";
    boolean dryRun = true;
    private iUsers.Presenter presenter;

    // Где должен базироваться currentUser: во вьюхе или презентере?
    private User currentUser;

    private CardsListAdapter cardsListAdapter;
    private List<Card> cardsList;
    @BindView(R.id.cardsListView) ListView cardsListView;


    // Системные методы
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_show_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.USER_page_title);

        activateUpButton();

        presenter = new Users_Presenter();

        cardsList = new ArrayList<>();
        cardsListAdapter = new CardsListAdapter(this, R.layout.cards_list_item, cardsList);
        cardsListView.setAdapter(cardsListAdapter);

        cardsListView.setOnItemClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);

        if (dryRun) {
            dryRun = false;

            try {
                presenter.processInputIntent(getIntent());
            } catch (Exception e) {
                // TODO: всунуть сокрытие крутилки внутрь show*Message()
                hideProgressMessage();
                showErrorMsg(R.string.USER_SHOW_error_displaying_user, e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unlinkView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        presenter.linkView(this);

        switch (resultCode) {
            case RESULT_OK:
                break;
            case RESULT_CANCELED:
                return;
            default:
                showErrorMsg(R.string.USER_EDIT_error_saving_user, "Unknown resultCode: "+resultCode);
                return;
        }

        switch (requestCode) {
            case Constants.CODE_USER_SHOW:
                Intent intent = new Intent(this, UserShow_View.class);
                // Не делаю "forResult", чтобы не зацикливать...
                startActivity(intent);
                break;

            case Constants.CODE_USER_EDIT:
                displayEditedUser(data);
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (AuthSingleton.isLoggedIn()) {
            getMenuInflater().inflate(R.menu.edit, menu);
        }

        super.onCreateOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected()");

        switch (item.getItemId()) {
            case android.R.id.home:
                closePage();
                break;
            case R.id.actionEdit:
                goUserEdit();
                break;
            case R.id.actionDelete:
//                presenter.userDeleteClicked(currentUser.getKey());
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


    // Интерфейсные методы
    @Override
    public void displayUser(final User user) {
        Log.d(TAG, "displayUser(), "+user);

        currentUser = user;

        hideProgressMessage();

        nameView.setText(user.getName());
        emailView.setText(user.getEmail());
        aboutView.setText(user.getAbout());

//        MyUtils.show(nameLabel);
        MyUtils.show(nameView);
//        MyUtils.show(aboutLabel);
        MyUtils.show(aboutView);

        if (user.hasAvatar())
            displayAvatar(user.getAvatarURL());
    }

    @Override
    public void displayCardsList(List<Card> list) {
        if (null != list) {
            cardsList.addAll(list);
            cardsListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void goUserEdit() {
        if (null != currentUser) {
            Intent intent = new Intent(this, UserEdit_View.class);
            intent.putExtra(Constants.USER_ID, currentUser.getKey());
            startActivityForResult(intent, Constants.CODE_USER_EDIT);
        }
    }


    // Коллбеки
    @Override
    public void onUserReadSuccess(User user) {
        currentUser = user;
        displayUser(user);
        presenter.loadCardsOfUser(user.getKey());
    }

    @Override
    public void onUserReadFail(String errorMsg) {
        currentUser = null;
        hideProgressMessage();
        showErrorMsg(R.string.USER_SHOW_error_displaying_user, errorMsg);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Card card = cardsList.get(position);
        Intent intent = new Intent(this, CardShow_View.class);
        intent.putExtra(Constants.CARD_KEY, card.getKey());
        startActivity(intent);
    }


    // Внутренние методы
    private void showAvatarThrobber() {
        MyUtils.show(avatarThrobber);
    }

    private void hideAvatarThrobber() {
        MyUtils.hide(avatarThrobber);
    }

    private void displayEditedUser(Intent data) {
        if (null == data) {
            showErrorMsg(R.string.USER_EDIT_data_error, "Intent data is null");
            return;
        }

        User user = data.getParcelableExtra(Constants.USER);
        if (null == user) {
            showErrorMsg(R.string.USER_EDIT_data_error, "User from intent is null.");
        }

        displayUser(user);
    }

    private void displayAvatar(String imageURI) {
        try {
            Uri uri = Uri.parse(imageURI);
            showAvatarThrobber();
            Picasso.get().load(uri).into(avatarView, new Callback() {
                @Override
                public void onSuccess() {
                    hideAvatarThrobber();
                }

                @Override
                public void onError(Exception e) {
                    hideAvatarThrobber();
                    showImageIsBroken(avatarView);
                }
            });

        } catch (Exception e) {
            hideAvatarThrobber();
            showImageIsBroken(avatarView);
            e.printStackTrace();
        }
    }

    private void showImageIsBroken(ImageView imageView) {
        Drawable brokenImage = imageView.getContext().getResources().getDrawable(R.drawable.ic_image_broken);
        imageView.setImageDrawable(brokenImage);
    }
}
