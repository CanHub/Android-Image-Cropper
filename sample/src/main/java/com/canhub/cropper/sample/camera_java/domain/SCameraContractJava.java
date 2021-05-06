package com.canhub.cropper.sample.camera_java.domain;

import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.FragmentActivity;

public interface SCameraContractJava {

    interface View {
        public void startCropImage(CameraEnumDomainJava option);
        public void showErrorMessage(String message);
        public void dispatchTakePictureIntent();
        public void cameraPermissionLaunch();
        public void showDialog();
        public void handleCropImageResult(String uri);
    }

    interface Presenter {
        public void bind(View view);
        public void unbind();
        public void onPermissionResult(boolean granted);
        public void onCreate(FragmentActivity activity, Context context);
        public void onOk();
        public void onCancel();
        public void onActivityResult(int resultCode, int requestCode, Intent data);
        public void startWithUriClicked();
        public void startWithoutUriClicked();
        public void startPickImageActivityClicked();
        public void startActivityForResultClicked();
    }
}
