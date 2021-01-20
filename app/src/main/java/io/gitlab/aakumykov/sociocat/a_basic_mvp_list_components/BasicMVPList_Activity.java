package io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class BasicMVPList_Activity
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
