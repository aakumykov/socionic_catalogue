package ru.aakumykov.me.mvp.dynamic_link_processor;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;


public class DLP_Presenter implements iDLP.Presenter {

    private iDLP.View view;
    private FirebaseDynamicLinks firebaseDynamicLinks = FirebaseDynamicLinks.getInstance();

    @Override
    public void processDynamicLink(Activity activity, @Nullable final Intent intent) {

        if (null == intent) {
            view.showErrorMsg(R.string.DLP_error_processing_link, "Intent is NULL");
            return;
        }

        view.showProgressBar();

        firebaseDynamicLinks
                .getDynamicLink(intent)
                .addOnSuccessListener(activity, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        Uri deepLink = null;
                        if (null != pendingDynamicLinkData) {
                            deepLink = pendingDynamicLinkData.getLink();
                            processDeepLink(deepLink, intent);
                        } else {
                            view.showErrorMsg(R.string.DLP_error_processing_link, "Deep link not found");
                            view.showHomeButton();
                        }
                    }
                })
                .addOnFailureListener(activity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        view.showErrorMsg(R.string.DLP_error_processing_link, e.getMessage());
                        view.showHomeButton();
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void linkView(iDLP.View view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = null;
    }

    // Внутренние методы
    private void processDeepLink(Uri deepLink, @NonNull Intent intent) {
        if (null == deepLink) {
            view.showErrorMsg(R.string.DLP_error_processing_link, "Deep link is NULL");
            view.showHomeButton();
            return;
        }

        String path = deepLink.getPath() + "";
        if (!TextUtils.isEmpty(path)) {
            view.showInfoMsg(path);
        } else {
            view.showErrorMsg(R.string.DLP_error_processing_link, "Deep link path is empty");
        }

        view.showHomeButton();
    }
}
