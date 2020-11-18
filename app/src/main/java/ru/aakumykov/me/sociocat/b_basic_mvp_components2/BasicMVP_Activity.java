package ru.aakumykov.me.sociocat.b_basic_mvp_components2;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class BasicMVP_Activity
        extends AppCompatActivity
{
    public void setPageTitle(int titleId) {
        String title = getString(titleId);
        setPageTitle(title);
    }

    public void setPageTitle(int titleId, Object... formatArgs) {
        String title = getString(titleId, formatArgs);
        setPageTitle(title);
    }

    public void setPageTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar)
            actionBar.setTitle(title);
    }

    public void refreshMenu() {
        invalidateOptionsMenu();
    }

    public void activateUpButton() {
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
