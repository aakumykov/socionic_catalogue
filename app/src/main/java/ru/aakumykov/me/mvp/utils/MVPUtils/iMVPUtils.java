package ru.aakumykov.me.mvp.utils.MVPUtils;

public interface iMVPUtils {

    interface ImageLoadWithResizeCallbacks {
        void onImageLoadWithResizeSuccess(FileInfo fileInfo);
        void onImageLoadWithResizeFail(String errorMsg);
    }
}
