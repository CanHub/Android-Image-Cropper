package com.canhub.cropper.sample;

import static android.graphics.Color.RED;
import static android.graphics.Color.WHITE;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.example.croppersample.R;
import com.example.croppersample.databinding.FragmentCameraBinding;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class SampleCropJava extends Fragment {

    static final String DATE_FORMAT = "yyyyMMdd_HHmmss";
    static final String FILE_NAMING_PREFIX = "JPEG_";
    static final String FILE_NAMING_SUFFIX = "_";
    static final String FILE_FORMAT = ".jpg";
    static final String AUTHORITY_SUFFIX = ".cropper.fileprovider";

    private FragmentCameraBinding binding;
    private Uri outputUri;

    private final ActivityResultLauncher<Uri> takePicture =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), this::onTakePictureResult);

    private final ActivityResultLauncher<CropImageContractOptions> cropImage =
            registerForActivityResult(new CropImageContract(), this::onCropImageResult);


    public static SampleCropJava newInstance() {
        return new SampleCropJava();
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

        binding.takePictureBeforeCallLibraryWithUri.setOnClickListener(v -> startTakePicture());
        binding.callLibraryWithoutUri.setOnClickListener(v -> startCameraWithoutUri());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void startCameraWithoutUri() {
        CropImageContractOptions options = new CropImageContractOptions(null, new CropImageOptions())
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
                .setFixAspectRatio(true);

        cropImage.launch(options);
    }

    public void startCameraWithUri() {
        CropImageContractOptions options = new CropImageContractOptions(outputUri, new CropImageOptions())
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
                .setFixAspectRatio(false);
        cropImage.launch(options);
    }

    public void showErrorMessage(@NotNull String message) {
        Log.e("Camera Error:", message);
        Toast.makeText(getActivity(), "Crop failed: " + message, Toast.LENGTH_SHORT).show();
    }

    private void startTakePicture() {
        try {
            Context ctx = requireContext();
            String authorities = ctx.getPackageName() + AUTHORITY_SUFFIX;
            outputUri = FileProvider.getUriForFile(ctx, authorities, createImageFile());
            takePicture.launch(outputUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleCropImageResult(@NotNull String uri) {
        SampleResultScreen.Companion.start(this, null, Uri.parse(uri), null);
    }

    private File createImageFile() throws IOException {
        SimpleDateFormat timeStamp = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                FILE_NAMING_PREFIX + timeStamp + FILE_NAMING_SUFFIX,
                FILE_FORMAT,
                storageDir
        );
    }

    public void onCropImageResult(@NonNull CropImageView.CropResult result) {
        if (result.isSuccessful()) {
            handleCropImageResult(Objects.requireNonNull(result.getUriContent())
                    .toString()
                    .replace("file:", ""));
        } else if (result.equals(CropImage.CancelledResult.INSTANCE)) {
            showErrorMessage("cropping image was cancelled by the user");
        } else {
            showErrorMessage("cropping image failed");
        }
    }

    public void onTakePictureResult(boolean success) {
        if (success) { startCameraWithUri(); }
        else { showErrorMessage("taking picture failed"); }
    }
}
