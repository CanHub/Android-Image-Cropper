package com.canhub.cropper.sample.crop_image_java.domain;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.canhub.cropper.CropImageView;

public interface SCropImageContractJava {

    interface View {
        void showErrorMessage(String message);
        void handleCropImageResult(String uri);
        void startCameraWithUri();
    }

    interface Presenter {
        void bind(View view);
        void unbind();
        void onCreate(FragmentActivity activity, Context context);
        void onCropImageResult(@NonNull CropImageView.CropResult result);
        void onTakePictureResult(boolean success);
    }
}
