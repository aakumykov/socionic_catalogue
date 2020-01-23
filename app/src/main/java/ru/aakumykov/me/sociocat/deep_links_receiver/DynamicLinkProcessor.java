package ru.aakumykov.me.sociocat.deep_links_receiver;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import ru.aakumykov.me.sociocat.utils.MyUtils;

class DynamicLinkProcessor {

    private static final String TAG = "DynamicLinkProcessor";
    private static FirebaseDynamicLinks firebaseDynamicLinks = FirebaseDynamicLinks.getInstance();

    public static void process(@NonNull Context context, @NonNull Intent intent) {

        firebaseDynamicLinks
                .getDynamicLink(intent)
                .addOnSuccessListener(new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        processPendingDynamicLink( pendingDynamicLinkData.getLink() );
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        MyUtils.printError(TAG, e);
                    }
                });

    }

    private static void processPendingDynamicLink(@Nullable Uri pendingDynamicLink) {

        if (null == pendingDynamicLink) {
            Log.e(TAG, "pendingDynamicLink is null");
            return;
        }

        Log.d(TAG, "2, pendingDynamicLink: "+pendingDynamicLink);

        String continueUrl = pendingDynamicLink.getQueryParameter("continueUrl");
        Log.d(TAG, "3, continueUrl: "+continueUrl);
    }

}
