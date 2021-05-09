package com.canhub.cropper.sample.camera_java.domain;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.FragmentActivity;

public interface SCameraContractJava {

    interface View {
        void startCropImage(CameraEnumDomainJava option);

        void showErrorMessage(String message);

        void dispatchTakePictureIntent();

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

        void onActivityResult(int resultCode, int requestCode, Intent data);

        void startWithUriClicked();

        void startWithoutUriClicked();

        void startPickImageActivityClicked();

        void startActivityForResultClicked();
    }
}
