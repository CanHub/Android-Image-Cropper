package com.canhub.cropper.sample.camera_java.presenter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.canhub.cropper.CropImage;
import com.canhub.cropper.common.CommonVersionCheck;
import com.canhub.cropper.sample.camera_java.app.SCameraFragmentJava;
import com.canhub.cropper.sample.camera_java.domain.CameraEnumDomainJava;
import com.canhub.cropper.sample.camera_java.domain.SCameraContractJava;

import static android.app.Activity.RESULT_OK;
import static com.canhub.cropper.sample.camera_java.app.SCameraFragmentJava.CODE_PHOTO_CAMERA;

public class SCameraPresenterJava implements SCameraContractJava.Presenter {
    private SCameraContractJava.View view = null;
    private boolean minVersion = CommonVersionCheck.INSTANCE.isAtLeastM23();
    private boolean request = false;
    private boolean hasSystemFeature = false;
    private boolean selfPermission = false;
    private Context context = null;

    @Override
    public void bind(SCameraContractJava.View view) {
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
            view.dispatchTakePictureIntent();
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
        this.context = context;

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
            view.dispatchTakePictureIntent();
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
        view.startCropImage(CameraEnumDomainJava.START_WITHOUT_URI);
    }

    @Override
    public void startPickImageActivityClicked() {
        assert view != null;
        view.startCropImage(CameraEnumDomainJava.START_PICK_IMG);
    }

    @Override
    public void startActivityForResultClicked() {
        assert view != null;
        view.startCropImage(CameraEnumDomainJava.START_FOR_RESULT);
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
    public void onActivityResult(int resultCode, int requestCode, Intent data) {
        assert view != null;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE: {
                    assert (context != null);
                    Bitmap bitmap = CropImage.getActivityResult(data).getBitmap(context);
                    Log.v(
                            "File Path",
                            CropImage.getActivityResult(data).getUriFilePath(context, false)
                    );

                    Uri uriContent = CropImage.getActivityResult(data).getUriContent();
                    if (uriContent != null && !CropImage.isReadExternalStoragePermissionsRequired(context, uriContent)) {
                        view.handleCropImageResult(uriContent.toString().replace("file:", ""));
                    } else {
                        view.showErrorMessage("CropImage getActivityResult return null");
                    }
                    break;
                }
                case SCameraFragmentJava.CUSTOM_REQUEST_CODE: {
                    assert context != null;
                    Log.v("File Path", CropImage.getPickImageResultUriFilePath(context, data, false));
                    CropImage.getPickImageResultUriFilePath(context, data, false);
                    Uri uri = CropImage.getPickImageResultUriContent(context, data);
                    if (view != null) {
                        view.handleCropImageResult(uri.toString().replace("file:", ""));
                    }

                    break;
                }
                case CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE: {
                    assert context != null;
                    Log.v("File Path", CropImage.getPickImageResultUriFilePath(context, data, false));
                    Uri uri = CropImage.getPickImageResultUriContent(context, data);
                    if (view != null) {
                        view.handleCropImageResult(uri.toString());
                    }
                    break;
                }
                case CODE_PHOTO_CAMERA: {
                    view.startCropImage(CameraEnumDomainJava.START_WITH_URI);
                    break;
                }
                default: {
                    view.showErrorMessage("requestCode = " + requestCode);
                    break;
                }
            }
        } else {
            view.showErrorMessage("resultCode = " + resultCode);
        }
    }
}