package ru.aakumykov.me.mvp.interfaces;


import java.util.HashMap;
import java.util.List;

import ru.aakumykov.me.mvp.models.Tag;

public interface iTagsSingleton {

    void createTag(Tag tag, TagCallbacks callbacks);
    void readTag(String key, TagCallbacks callbacks);
    void saveTag(Tag tag, SaveCallbacks callbacks);
    void deleteTag(Tag tag, DeleteCallbacks callbacks);
    void listTags(ListCallbacks callbacks);

    void updateCardTags(String cardKey, HashMap<String,Boolean> oldTags, HashMap<String,Boolean> newTags);


    interface TagCallbacks {
        void onTagSuccess(Tag tag);
        void onTagFail(String errorMsg);
    }

    interface SaveCallbacks {
        void onSaveSuccess(Tag tag);
        void onSaveFail(String errorMsg);
    }

    interface DeleteCallbacks {
        void onDeleteSuccess(Tag tag);
        void onDeleteFail(String errorMsg);
    }

    interface ListCallbacks {
        void onTagsListSuccess(List<Tag> list);
        void onTagsListFail(String errorMsg);
    }
}
