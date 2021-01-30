package com.canhub.cropper.sample

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.canhub.cropper.CropImageView.CropResult
import com.canhub.cropper.CropImageView.OnCropImageCompleteListener
import com.canhub.cropper.CropImageView.OnSetImageUriCompleteListener
import com.example.croppersample.R

/** The fragment that will show the Image Cropping UI by requested preset.  */
class MainFragment : Fragment(), OnSetImageUriCompleteListener, OnCropImageCompleteListener {

    private var mDemoPreset: CropDemoPreset? = null
    private var mCropImageView: CropImageView? = null

    /** Set the image to show for cropping.  */
    fun setImageUri(imageUri: Uri?) {
        mCropImageView!!.setImageUriAsync(imageUri)
        //        CropImage.activity(imageUri)
        //                .start(getContext(), this);
    }

    /** Set the options of the crop image view to the given values.  */
    fun setCropImageViewOptions(options: CropImageViewOptions) {
        mCropImageView!!.scaleType = options.scaleType
        mCropImageView!!.cropShape = options.cropShape
        mCropImageView!!.guidelines = options.guidelines
        mCropImageView!!.setAspectRatio(options.aspectRatio.first, options.aspectRatio.second)
        mCropImageView!!.setFixedAspectRatio(options.fixAspectRatio)
        mCropImageView!!.setMultiTouchEnabled(options.multitouch)
        mCropImageView!!.isShowCropOverlay = options.showCropOverlay
        mCropImageView!!.isShowProgressBar = options.showProgressBar
        mCropImageView!!.isAutoZoomEnabled = options.autoZoomEnabled
        mCropImageView!!.maxZoom = options.maxZoomLevel
        mCropImageView!!.isFlippedHorizontally = options.flipHorizontally
        mCropImageView!!.isFlippedVertically = options.flipVertically
    }

    /** Set the initial rectangle to use.  */
    fun setInitialCropRect() {
        mCropImageView!!.cropRect = Rect(100, 300, 500, 1200)
    }

    /** Reset crop window to initial rectangle.  */
    fun resetCropRect() {
        mCropImageView!!.resetCropRect()
    }

    fun updateCurrentCropViewOptions() {
        val options = CropImageViewOptions()
        options.scaleType = mCropImageView!!.scaleType
        options.cropShape = mCropImageView!!.cropShape
        options.guidelines = mCropImageView!!.guidelines
        options.aspectRatio = mCropImageView!!.aspectRatio
        options.fixAspectRatio = mCropImageView!!.isFixAspectRatio
        options.showCropOverlay = mCropImageView!!.isShowCropOverlay
        options.showProgressBar = mCropImageView!!.isShowProgressBar
        options.autoZoomEnabled = mCropImageView!!.isAutoZoomEnabled
        options.maxZoomLevel = mCropImageView!!.maxZoom
        options.flipHorizontally = mCropImageView!!.isFlippedHorizontally
        options.flipVertically = mCropImageView!!.isFlippedVertically
        (activity as MainActivity?)!!.setCurrentOptions(options)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return when (mDemoPreset) {
            CropDemoPreset.RECT -> inflater.inflate(R.layout.fragment_main_rect, container, false)
            CropDemoPreset.CIRCULAR -> inflater.inflate(
                R.layout.fragment_main_oval,
                container,
                false
            )
            CropDemoPreset.CUSTOMIZED_OVERLAY -> inflater.inflate(
                R.layout.fragment_main_customized,
                container,
                false
            )
            CropDemoPreset.MIN_MAX_OVERRIDE -> inflater.inflate(
                R.layout.fragment_main_min_max,
                container,
                false
            )
            CropDemoPreset.SCALE_CENTER_INSIDE -> inflater.inflate(
                R.layout.fragment_main_scale_center,
                container,
                false
            )
            CropDemoPreset.CUSTOM -> inflater.inflate(R.layout.fragment_main_rect, container, false)
            else -> throw IllegalStateException("Unknown preset: $mDemoPreset")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mCropImageView = view.findViewById(R.id.cropImageView)
        mCropImageView?.let {
            it.setOnSetImageUriCompleteListener(this)
            it.setOnCropImageCompleteListener(this)
            updateCurrentCropViewOptions()
            if (savedInstanceState == null) {
                if (mDemoPreset === CropDemoPreset.SCALE_CENTER_INSIDE)
                    it.imageResource = R.drawable.cat_small
                else it.imageResource = R.drawable.cat
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_action_crop -> {
                mCropImageView!!.getCroppedImageAsync()
                return true
            }
            R.id.main_action_rotate -> {
                mCropImageView!!.rotateImage(90)
                return true
            }
            R.id.main_action_flip_horizontally -> {
                mCropImageView!!.flipImageHorizontally()
                return true
            }
            R.id.main_action_flip_vertically -> {
                mCropImageView!!.flipImageVertically()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mDemoPreset = CropDemoPreset.valueOf(arguments!!.getString("DEMO_PRESET")!!)
        (activity as MainActivity).setCurrentFragment(this)
    }

    override fun onDetach() {
        super.onDetach()
        if (mCropImageView != null) {
            mCropImageView!!.setOnSetImageUriCompleteListener(null)
            mCropImageView!!.setOnCropImageCompleteListener(null)
        }
    }

    override fun onSetImageUriComplete(view: CropImageView, uri: Uri, error: Exception) {
        Log.e("AIC", "Failed to load image by URI", error)
        Toast.makeText(activity, "Image load failed: " + error.message, Toast.LENGTH_LONG).show()
    }

    override fun onCropImageComplete(view: CropImageView, result: CropResult) {
        handleCropResult(result)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            handleCropResult(result)
        }
    }

    private fun handleCropResult(result: CropResult?) {
        if (result!!.error == null) {
            val intent = Intent(activity, CropResultActivity::class.java)
            intent.putExtra("SAMPLE_SIZE", result.sampleSize)
            if (result.uri != null) {
                intent.putExtra("URI", result.uri)
            } else {
                CropResultActivity.mImage =
                    if (mCropImageView!!.cropShape == CropImageView.CropShape.OVAL) CropImage.toOvalBitmap(
                        result.bitmap
                    ) else result.bitmap
            }
            startActivity(intent)
        } else {
            Log.e("AIC", "Failed to crop image", result.error)
            Toast.makeText(
                activity,
                "Image crop failed: " + result.error.message,
                Toast.LENGTH_LONG
            )
                .show()
        }
    }

    companion object {

        /** Returns a new instance of this fragment for the given section number.  */
        fun newInstance(demoPreset: CropDemoPreset): MainFragment {
            val fragment = MainFragment()
            val args = Bundle()
            args.putString("DEMO_PRESET", demoPreset.name)
            fragment.arguments = args
            return fragment
        }
    }
}