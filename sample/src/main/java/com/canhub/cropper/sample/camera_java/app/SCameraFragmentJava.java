package com.canhub.cropper.sample.camera_java.app;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageView;
import com.canhub.cropper.sample.SCropResultActivity;
import com.canhub.cropper.sample.camera_java.domain.CameraEnumDomainJava;
import com.canhub.cropper.sample.camera_java.domain.SCameraContractJava;
import com.canhub.cropper.sample.camera_java.presenter.SCameraPresenterJava;
import com.example.croppersample.R;
import com.example.croppersample.databinding.FragmentCameraBinding;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.graphics.Color.RED;
import static android.graphics.Color.WHITE;

public class SCameraFragmentJava extends Fragment implements SCameraContractJava.View {

    public static final int CODE_PHOTO_CAMERA = 811917;
    static final String DATE_FORMAT = "yyyyMMdd_HHmmss";
    static final String FILE_NAMING_PREFIX = "JPEG_";
    static final String FILE_NAMING_SUFFIX = "_";
    static final String FILE_FORMAT = ".jpg";
    static final String AUTHORITY_SUFFIX = ".fileprovider";
    public static final int CUSTOM_REQUEST_CODE = 8119153;

    private FragmentCameraBinding binding;
    private SCameraContractJava.Presenter presenter = new SCameraPresenterJava();
    private Uri photoUri;
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                presenter.onPermissionResult(isGranted);
            });

    public static SCameraFragmentJava newInstance(){
        return new SCameraFragmentJava();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCameraBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.bind(this);

        binding.startWithUri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.startWithUriClicked();
            }
        });

        binding.startWithoutUri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.startWithoutUriClicked();
            }
        });

        binding.startPickImageActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.startPickImageActivityClicked();
            }
        });

        binding.startActivityForResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.startActivityForResultClicked();
            }
        });

        presenter.onCreate(getActivity(), getContext());
    }

    @Override
    public void startCropImage(@NotNull CameraEnumDomainJava option) {
        switch (option) {
            case START_WITH_URI : startCameraWithUri(); break;
            case START_WITHOUT_URI : startCameraWithoutUri(); break;
            case START_PICK_IMG : startPickImage(); break;
            case START_FOR_RESULT : startForResult(); break;
            default: break;
        }
    }

    private void startForResult(){
        assert(getContext()!=null);
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), CUSTOM_REQUEST_CODE);

    }

    private void startPickImage(){
        assert(getContext()!=null);
        CropImage.activity()
                .start(getContext(), this);
    }

    private void startCameraWithoutUri(){
        assert(getContext()!=null);
        Context ctx = getContext();
        CropImage.activity()
                .setScaleType(CropImageView.ScaleType.CENTER)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(4, 16)
                .setMaxZoom(8)
                .setAutoZoomEnabled(false)
                .setMultiTouchEnabled(false)
                .setCenterMoveEnabled(true)
                .setShowCropOverlay(false)
                .setAllowFlipping(false)
                .setSnapRadius(10f)
                .setTouchRadius(30f)
                .setInitialCropWindowPaddingRatio(0.3f)
                .setBorderLineThickness(5f)
                .setBorderLineColor(R.color.black)
                .setBorderCornerThickness(6f)
                .setBorderCornerOffset(2f)
                .setBorderCornerLength(20f)
                .setBorderCornerColor(RED)
                .setGuidelinesThickness(5f)
                .setGuidelinesColor(RED)
                .setBackgroundColor(Color.argb(119, 30, 60, 90))
                .setMinCropWindowSize(20, 20)
                .setMinCropResultSize(16, 16)
                .setMaxCropResultSize(999, 999)
                .setActivityTitle("CUSTOM title")
                .setActivityMenuIconColor(RED)
                .setOutputUri(null)
                .setOutputCompressFormat(Bitmap.CompressFormat.PNG)
                .setOutputCompressQuality(50)
                .setRequestedSize(100, 100)
                .setRequestedSize(100, 100, CropImageView.RequestSizeOptions.RESIZE_FIT)
                .setInitialCropWindowRectangle(null)
                .setInitialRotation(180)
                .setAllowCounterRotation(true)
                .setFlipHorizontally(true)
                .setFlipVertically(true)
                .setCropMenuCropButtonTitle("Custom name")
                .setCropMenuCropButtonIcon(R.drawable.ic_gear_24)
                .setAllowRotation(false)
                .setNoOutputImage(false)
                .setFixAspectRatio(true)
                .start(ctx, this);
    }

    private void startCameraWithUri(){
        assert(getContext()!=null);
        Context ctx = getContext();
        CropImage.activity(photoUri)
                .setScaleType(CropImageView.ScaleType.FIT_CENTER)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                .setAspectRatio(1, 1)
                .setMaxZoom(4)
                .setAutoZoomEnabled(true)
                .setMultiTouchEnabled(true)
                .setCenterMoveEnabled(true)
                .setShowCropOverlay(true)
                .setAllowFlipping(true)
                .setSnapRadius(3f)
                .setTouchRadius(48f)
                .setInitialCropWindowPaddingRatio(0.1f)
                .setBorderLineThickness(3f)
                .setBorderLineColor(Color.argb(170, 255, 255, 255))
                .setBorderCornerThickness(2f)
                .setBorderCornerOffset(5f)
                .setBorderCornerLength(14f)
                .setBorderCornerColor(WHITE)
                .setGuidelinesThickness(1f)
                .setGuidelinesColor(R.color.white)
                .setBackgroundColor(Color.argb(119, 0, 0, 0))
                .setMinCropWindowSize(24, 24)
                .setMinCropResultSize(20, 20)
                .setMaxCropResultSize(99999, 99999)
                .setActivityTitle("")
                .setActivityMenuIconColor(0)
                .setOutputUri(null)
                .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                .setOutputCompressQuality(90)
                .setRequestedSize(0, 0)
                .setRequestedSize(0, 0, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                .setInitialCropWindowRectangle(null)
                .setInitialRotation(90)
                .setAllowCounterRotation(false)
                .setFlipHorizontally(false)
                .setFlipVertically(false)
                .setCropMenuCropButtonTitle(null)
                .setCropMenuCropButtonIcon(0)
                .setAllowRotation(true)
                .setNoOutputImage(false)
                .setFixAspectRatio(false)
                .start(ctx, this);
    }

    @Override
    public void showErrorMessage(@NotNull String message) {
        Log.e("Camera Error:", message);
        Toast.makeText(getActivity(), "Crop failed: "+message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void dispatchTakePictureIntent() {
        assert(getContext()!=null);
        Context ctx = getContext();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            if(takePictureIntent.resolveActivity(ctx.getPackageManager()) != null){
                String authorities = getContext().getPackageName() + AUTHORITY_SUFFIX;
                photoUri = FileProvider.getUriForFile(ctx, authorities, createImageFile());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, CODE_PHOTO_CAMERA);
            }
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cameraPermissionLaunch() {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA);
    }

    @Override
    public void showDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle(R.string.missing_camera_permission_title);
        alertDialogBuilder.setMessage(R.string.missing_camera_permission_body);
                alertDialogBuilder.setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                presenter.onOk();
                            }
                        });
        alertDialogBuilder.setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                presenter.onCancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    @Override
    public void handleCropImageResult(@NotNull String uri) {
        SCropResultActivity.Companion.start(this, null, Uri.parse(uri), null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onActivityResult(resultCode, requestCode, data);
    }

    private File createImageFile() throws IOException {
        assert getActivity()!=null;
        SimpleDateFormat timeStamp = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                "$FILE_NAMING_PREFIX$$FILE_NAMING_SUFFIX",
                FILE_FORMAT,
                storageDir
        );
    }
}
