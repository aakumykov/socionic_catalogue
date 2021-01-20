package io.gitlab.aakumykov.sociocat.singletons;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;

import java.util.HashMap;
import java.util.List;

import io.gitlab.aakumykov.sociocat.models.Tag;

public interface iTagsSingleton {

    void createTag(Tag tag, TagCallbacks callbacks);
    void getTag(String key, TagCallbacks callbacks);
    void saveTag(Tag tag, SaveCallbacks callbacks);

    void listTags(ListCallbacks callbacks);

    CollectionReference getTagsCollection();

    void processTags(String cardKey,
                     @Nullable List<String> oldTagsNames,
                     @Nullable List<String> newTagsNames,
                     @Nullable UpdateCallbacks callbacks
    );

    void processTags(
            String cardKey,
            @Nullable HashMap<String, Boolean> oldTags,
            @Nullable HashMap<String, Boolean> newTags,
            @Nullable UpdateCallbacks callbacks
    );

    void checkTagExists(@Nullable String tagName, ExistanceCallbacks callbacks);



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

    interface ExistanceCallbacks {
        void onTagExists(@NonNull String tagName);
        void onTagNotExists(@Nullable String tagName);
        void onTagExistsCheckFailed(@NonNull String errorMsg);
    }
}
