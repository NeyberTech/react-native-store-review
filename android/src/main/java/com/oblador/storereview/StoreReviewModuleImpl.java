package com.oblador.storereview;

import android.app.Activity;
import android.util.Log;
import androidx.annotation.NonNull;
import android.content.Intent;
import android.net.Uri;
import java.util.Map;
import java.util.HashMap;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.review.ReviewInfo;
import com.facebook.react.bridge.ReactApplicationContext;
import com.google.android.gms.tasks.Task;

public class StoreReviewModuleImpl {

    public static final String NAME = "RNStoreReview";

    public static void requestReview(ReactApplicationContext context) {
        ReviewManager manager = ReviewManagerFactory.create(context);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ReviewInfo reviewInfo = task.getResult();
                Activity currentActivity = context.getCurrentActivity();
                if (currentActivity != null) {
                  manager.launchReviewFlow(currentActivity, reviewInfo);
                } else {
                  Log.w(NAME, "Current activity is null. Unable to launch review flow.");
                  openPlayStorePage(context);
                }
            } else {
                Log.w(NAME, "Requesting review failed", task.getException());
                openPlayStorePage(context);
            }
        });
    }

    private static void openPlayStorePage(ReactApplicationContext context) {
        Activity currentActivity = context.getCurrentActivity();
        if (currentActivity != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://play.google.com/store/apps/details?id=" + context.getApplicationContext().getPackageName()));
            if (intent.resolveActivity(currentActivity.getPackageManager()) != null) {
                currentActivity.startActivity(intent);
            } else {
                Log.w(NAME, "No activity found to handle the intent.");
            }
        } else {
            Log.w(NAME, "Current activity is null. Unable to open Play Store.");
        }
    }

}
