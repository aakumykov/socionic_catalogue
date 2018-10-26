package ru.aakumykov.me.mvp.tags.list;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.lujun.androidtagview.TagContainerLayout;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iTagsSingleton;
import ru.aakumykov.me.mvp.models.Tag;
import ru.aakumykov.me.mvp.tags.Tags_Presenter;
import ru.aakumykov.me.mvp.tags.iTags;

public class TagsList_View extends BaseView implements
        iTags.ListView,
        iTagsSingleton.ListCallbacks
{
    @BindView(R.id.tagsContainer) TagContainerLayout tagsContainer;

    private final static String TAG = "TagsList_View";
    private iTags.Presenter presenter;
    private List<String> tagsList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tags_list_activity);
        ButterKnife.bind(this);

        presenter = new Tags_Presenter();

        presenter.listPageCreated(this);
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


    // Коллбеки
    @Override
    public void onTagsListSuccess(List<Tag> list) {
        Log.d(TAG, "onTagsListSuccess(), "+list);

        hideProgressBar();

        for (Tag tag : list) {
            tagsList.add(tag.getName());
        }

        tagsContainer.setTags(tagsList);
    }

    @Override
    public void onTagsListFail(String errorMsg) {
        showErrorMsg(R.string.error_loading_tags);
    }

}
