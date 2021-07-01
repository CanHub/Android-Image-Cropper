package com.canhub.cropper.sample.camera_java.domain;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.canhub.cropper.CropImageView;

public interface SCameraContractJava {

    interface View {
        void startCropImage(CameraEnumDomainJava option);

        void showErrorMessage(String message);

        void startTakePicture();

        void cameraPermissionLaunch();

        void showDialog();

        void handleCropImageResult(String uri);
    }

    interface Presenter {
        void bind(View view);

        void unbind();

        void onPermissionResult(boolean granted);

        void onCreate(FragmentActivity activity, Context context);

        void onOk();

        void onCancel();

        void onCropImageResult(@NonNull CropImageView.CropResult result);

        void onPickImageResult(@Nullable Uri resultUri);

        void onPickImageResultCustom(@Nullable Uri resultUri);

        void onTakePictureResult(boolean success);

        void startWithUriClicked();

        void startWithoutUriClicked();

        void startPickImageActivityClicked();

        void startPickImageActivityCustomClicked();
    }
}
