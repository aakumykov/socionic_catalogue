package ru.aakumykov.me.sociocat.singletons;


import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.models.Tag;

public interface iTagsSingleton {

    void createTag(Tag tag, TagCallbacks callbacks);
    void readTag(String key, TagCallbacks callbacks);
    void saveTag(Tag tag, SaveCallbacks callbacks);
    void deleteTag(Tag tag, DeleteCallbacks callbacks);
    void listTags(ListCallbacks callbacks);

    void processTags(String cardKey,
                     @Nullable List<Tag> oldTags,
                     @Nullable List<Tag> newTags,
                     @Nullable UpdateCallbacks callbacks
    );

    interface TagCallbacks {
        void onTagSuccess(Tag tag);
        void onTagFail(String errorMsg);
    }

    interface SaveCallbacks {
        void onSaveSuccess(Tag tag);
        void onSaveFail(String errorMsg);
    }

    interface UpdateCallbacks {
        void onUpdateSuccess();
        void onUpdateFail(String errorMsg);
    }

    interface DeleteCallbacks {
        void onDeleteSuccess(Tag tag);
        void onDeleteFail(String errorMsg);
    }

    interface ListCallbacks {
        void onTagsListSuccess(List<Tag> tagsList);
        void onTagsListFail(String errorMsg);
    }
}
