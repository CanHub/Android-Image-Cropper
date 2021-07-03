package com.canhub.cropper.sample.crop_image_java.presenter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageView;
import com.canhub.cropper.common.CommonVersionCheck;
import com.canhub.cropper.sample.crop_image_java.domain.SCropImageEnumDomainJava;
import com.canhub.cropper.sample.crop_image_java.domain.SCropImageContractJava;

public class SCropImagePresenterJava implements SCropImageContractJava.Presenter {
    private SCropImageContractJava.View view = null;
    private boolean minVersion = CommonVersionCheck.INSTANCE.isAtLeastM23();
    private boolean request = false;
    private boolean hasSystemFeature = false;
    private boolean selfPermission = false;

    @Override
    public void bind(SCropImageContractJava.View view) {
        this.view = view;
    }

    @Override
    public void unbind() {
        view = null;
    }

    @Override
    public void onPermissionResult(boolean granted) {
        assert view != null;
        if (granted) {
            view.startTakePicture();
        } else if (minVersion && request) {
            view.showDialog();
        } else {
            view.cameraPermissionLaunch();
        }
    }

    @Override
    public void onCreate(FragmentActivity activity, Context context) {
        assert view != null;
        if (activity == null || context == null) {
            view.showErrorMessage("onCreate activity and/or context are null");
            return;
        }

        request = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA);
        if (context.getPackageManager() != null) {
            hasSystemFeature = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
        } else {
            hasSystemFeature = false;
        }
        selfPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void startWithUriClicked() {
        assert view != null;
        if (hasSystemFeature && selfPermission) {
            view.startTakePicture();
        } else if (hasSystemFeature && minVersion && request) {
            view.showDialog();
        } else if (hasSystemFeature) {
            view.cameraPermissionLaunch();
        } else {
            view.showErrorMessage("onCreate no case apply");
        }
    }

    @Override
    public void startWithoutUriClicked() {
        assert view != null;
        view.startCropImage(SCropImageEnumDomainJava.START_WITHOUT_URI);
    }

    @Override
    public void startPickImageActivityClicked() {
        assert view != null;
        view.startCropImage(SCropImageEnumDomainJava.START_PICK_IMG);
    }

    @Override
    public void startPickImageActivityCustomClicked() {
        assert view != null;
        view.startCropImage(SCropImageEnumDomainJava.START_PICK_IMG_CUSTOM);
    }

    @Override
    public void onOk() {
        assert view != null;
        view.cameraPermissionLaunch();
    }

    @Override
    public void onCancel() {
        assert view != null;
        view.showErrorMessage("onCancel");
    }

    @Override
    public void onCropImageResult(@NonNull CropImageView.CropResult result) {
        if (result.isSuccessful()) {
            view.handleCropImageResult(result.getUriContent().toString().replace("file:", ""));
        } else if (result.equals(CropImage.CancelledResult.INSTANCE)) {
            view.showErrorMessage("cropping image was cancelled by the user");
        } else {
            view.showErrorMessage("cropping image failed");
        }
    }

    @Override
    public void onPickImageResult(@Nullable Uri resultUri) {
        if (resultUri != null) {
            Log.v("Uri", resultUri.toString());
            view.handleCropImageResult(resultUri.toString());
        } else {
            view.showErrorMessage("picking image failed");
        }
    }

    @Override
    public void onPickImageResultCustom(@Nullable Uri resultUri) {
        if (resultUri != null) {
            Log.v("File Path", resultUri.toString());
            view.handleCropImageResult(resultUri.toString());
        } else {
            view.showErrorMessage("picking image failed");
        }
    }

    @Override
    public void onTakePictureResult(boolean success) {
        if (success) {
            view.startCropImage(SCropImageEnumDomainJava.START_WITH_URI);
        } else {
            view.showErrorMessage("taking picture failed");
        }
    }
}