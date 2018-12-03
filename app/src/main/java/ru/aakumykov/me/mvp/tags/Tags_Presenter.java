package ru.aakumykov.me.mvp.tags;

import android.util.Log;

import java.util.List;

import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iTagsSingleton;
import ru.aakumykov.me.mvp.models.Tag;
import ru.aakumykov.me.mvp.services.TagsSingleton;

public class Tags_Presenter implements
        iTags.Presenter,
        iTagsSingleton.ListCallbacks,
        iTagsSingleton.TagCallbacks
{

    private final static String TAG = "Tags_Presenter";
    private iTags.ListView listView;
    private iTags.ShowView showView;
    private iTags.EditView editView;
    private iTagsSingleton tagsSingleton = TagsSingleton.getInstance();


    // Служебные методы
    @Override
    public void linkView(iTags.View view) throws IllegalArgumentException {

        if (view instanceof iTags.ListView) {
            listView = (iTags.ListView) view;
        }
        else if (view instanceof iTags.ShowView) {
            showView = (iTags.ShowView) view;
        }
        else {
            throw new IllegalArgumentException("Unknown type of View '"+view.getClass()+"'");
        }
    }
    @Override
    public void unlinkView() {
        this.listView = null;
        this.showView = null;
        this.editView = null;
    }


    // Основные методы
    @Override
    public void onTagClicked(Tag tag) {
        // TODO: проверить с null, чтобы понять, где обрабатывать ошибку
        listView.goShowPage(tag.getKey());
    }

    @Override
    public void loadList() {
        Log.d(TAG, "loadList()");
        tagsSingleton.listTags(this);
    }

    @Override
    public void onShowPageReady(String tagKey) {
        Log.d(TAG, "onShowPageReady('"+tagKey+"')");
        TagsSingleton.getInstance().readTag(tagKey, this);
    }

    @Override
    public void onEditPageReady(String tagKey) {
        Log.d(TAG, "onEditPageReady("+tagKey+"')");
    }


    // Коллбеки
    @Override
    public void onTagsListSuccess(List<Tag> list) {
//        Log.d(TAG, "onTagsListSuccess(), "+list);
        listView.hideProgressBar();
        listView.displayTags(list);
    }

    @Override
    public void onTagsListFail(String errorMsg) {
        listView.showErrorMsg(R.string.error_loading_tags);
    }

    @Override
    public void onTagSuccess(Tag tag) {
        showView.displayTag(tag);
    }

    @Override
    public void onTagFail(String errorMsg) {
        showView.showErrorMsg(R.string.error_loading_tag);
        Log.e(TAG, errorMsg);
    }
}
