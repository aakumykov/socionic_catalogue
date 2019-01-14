package ru.aakumykov.me.mvp.register2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.cards_grid.CardsGrid_View;

public class Register2_View extends BaseView implements
    iRegister2.View
{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register2_activity);
    }

    @Override
    public void onUserLogin() {
        showToast(R.string.REGISTER2_you_are_already_registered);
        Intent intent = new Intent(this, CardsGrid_View.class);
        startActivity(intent);
    }

    @Override
    public void onUserLogout() {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getEmail() {
        return null;
    }

    @Override
    public String getPassword1() {
        return null;
    }

    @Override
    public String getPassword2() {
        return null;
    }

    @Override
    public void showNameError() {

    }

    @Override
    public void showEmailError() {

    }

    @Override
    public void showPasswordError() {

    }

    @Override
    public void finishAndGoToApp() {
        Intent intent = new Intent(this, CardsGrid_View.class);
        startActivity(intent);
    }
}
