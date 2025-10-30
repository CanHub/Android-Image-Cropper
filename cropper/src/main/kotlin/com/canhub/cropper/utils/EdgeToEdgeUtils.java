package com.canhub.cropper.utils;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.canhub.cropper.R;

public class EdgeToEdgeUtils {

    public static void enableEdgeToEdge(Activity activity, int rootViewId) {
        Window window = activity.getWindow();

        // ১. কন্টেন্টকে সিস্টেম বারের নিচে প্রসারিত করা
        WindowCompat.setDecorFitsSystemWindows(window, false);

        // ২. স্ট্যাটাস বার এবং নেভিগেশন বার স্বচ্ছ করা
        // এটি নিশ্চিত করবে যে আপনার টুলবারের রঙ স্ট্যাটাস বারে দেখা যায়।
        window.setNavigationBarColor(Color.TRANSPARENT);
        window.setStatusBarColor(Color.TRANSPARENT);

        // ৩. লাইট স্ট্যাটাস বার সেটিং
        WindowInsetsControllerCompat controller =
                new WindowInsetsControllerCompat(window, window.getDecorView());
        controller.setAppearanceLightStatusBars(false); // যদি আপনার টুলবার গাঢ় রঙের হয়
        controller.setAppearanceLightNavigationBars(false);

        // ৪. ডিসপ্লে কাটআউট মোড সেটিং
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.getAttributes().layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }

        // ম্যানুয়াল প্যাডিং লজিক সম্পূর্ণভাবে বাদ দেওয়া হলো
    }

    public static void disableEdgeToEdge(Activity activity) {
        Window window = activity.getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, true);
    }
}
