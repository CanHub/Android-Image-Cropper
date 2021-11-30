package com.canhub.cropper.sample.crop_image_java.presenter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageView;
import com.canhub.cropper.sample.crop_image_java.domain.SCropImageContractJava;

import java.util.Objects;

public class SCropImagePresenterJava implements SCropImageContractJava.Presenter {
    private SCropImageContractJava.View view = null;

    @Override
    public void bind(SCropImageContractJava.View view) {
        this.view = view;
    }

    @Override
    public void unbind() {
        view = null;
    }

    @Override
    public void onCreate(FragmentActivity activity, Context context) {
        assert view != null;
        if (activity == null || context == null) {
            view.showErrorMessage("onCreate activity and/or context are null");
        }
    }

    @Override
    public void onCropImageResult(@NonNull CropImageView.CropResult result) {
        if (result.isSuccessful()) {
            view.handleCropImageResult(Objects.requireNonNull(result.getUriContent())
                    .toString()
                    .replace("file:", ""));
        } else if (result.equals(CropImage.CancelledResult.INSTANCE)) {
            view.showErrorMessage("cropping image was cancelled by the user");
        } else {
            view.showErrorMessage("cropping image failed");
        }
    }

    @Override
    public void onTakePictureResult(boolean success) {
        if (success) {
            view.startCameraWithUri();
        } else {
            view.showErrorMessage("taking picture failed");
        }
    }
}
