package ru.aakumykov.me.mvp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import ru.aakumykov.me.mvp.start_page.iPageConfigurator;

public class TagsList_Fragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tags_list, container, false);
        ButterKnife.bind(this, rootView);

//        iPageConfigurator pageConfigurator = (iPageConfigurator) getActivity();
//        if (null != pageConfigurator)
//            pageConfigurator.setPageTitle(R.string.TAGS_LIST_page_title);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }
}
