package ru.aakumykov.me.mvp.tags;

import ru.aakumykov.me.mvp.interfaces.iTagsSingleton;
import ru.aakumykov.me.mvp.services.TagsSingleton;

public class Tags_Presenter implements iTags.Presenter {

    private final static String TAG = "Tags_Presenter";
    private iTags.ListView listView;
    private iTags.ShowView showView;
    private iTags.EditView editView;
    private iTagsSingleton tagsSingleton = TagsSingleton.getInstance();


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


    @Override
    public void listPageCreated(iTagsSingleton.ListCallbacks callbacks) {
        tagsSingleton.listTags(callbacks);
    }
}
