package com.jupiter.facedetection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

import com.jupiter.facedetection.util.CamParaUtil;
import com.jupiter.facedetection.util.DisplayUtil;
import com.jupiter.facedetection.util.FileUtil;
import com.jupiter.facedetection.util.ImageUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lchad on 2017/7/20.
 */
public class CameraInterface {
    private static final String TAG = "jupiter";

    private static CameraInterface mCameraInterface;

    private Camera mCamera;
    private Camera.Parameters mParams;
    private boolean isPreviewing = false;
    private float mPreviwRate = -1f;
    private int mCameraId = -1;

    private List<Rect> mFaceRect = new ArrayList<>();

    /**
     * 快门音效
     */
    ShutterCallback mShutterCallback = new ShutterCallback() {
        public void onShutter() {
            Toast.makeText(FaceApplication.getInstance(), "shutter", Toast.LENGTH_SHORT).show();
        }
    };

    PictureCallback mJpegPictureCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap b = null;
            if (null != data) {
                b = BitmapFactory.decodeByteArray(data, 0, data.length);
                mCamera.stopPreview();
                isPreviewing = false;
            }

            if (null != b) {
                Bitmap rotatedBitmap = null;
                if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    rotatedBitmap = ImageUtil.getRotateBitmap(b, 90.0f);
                } else if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    rotatedBitmap = ImageUtil.getRotateBitmap(b, -90.0f);
                }

                FileUtil.saveBitmap(rotatedBitmap);

                if (mFaceRect.size() > 0) {
                    for (Rect rect : mFaceRect) {
                        Log.e("123456789", " face clips: " + rect.flattenToString());
//                        Bitmap faceClipsBitmap = Bitmap.createBitmap(rotatedBitmap, rect.left, rect.top, rect.width(), rect.height());
//                        FileUtil.saveBitmap(faceClipsBitmap);
                    }
                }
            }
            mCamera.startPreview();
            isPreviewing = true;
            mFaceRect.clear();
        }
    };


    private CameraInterface() {

    }

    public static synchronized CameraInterface getInstance() {
        if (mCameraInterface == null) {
            mCameraInterface = new CameraInterface();
        }
        return mCameraInterface;
    }

    public void doOpenCamera(CamOpenOverCallback callback, int cameraId) {
        Log.i(TAG, "Camera open....");
        mCamera = Camera.open(cameraId);
        mCameraId = cameraId;
        if (callback != null) {
            callback.cameraHasOpened();
        }
    }

    public void doStartPreview(SurfaceHolder holder, float previewRate) {
        Log.i(TAG, "doStartPreview...");
        if (isPreviewing) {
            mCamera.stopPreview();
            mCamera.stopFaceDetection();
            return;
        }
        if (mCamera != null) {

            mParams = mCamera.getParameters();
            mParams.setPictureFormat(ImageFormat.JPEG);
            CamParaUtil.getInstance().printSupportPictureSize(mParams);
            CamParaUtil.getInstance().printSupportPreviewSize(mParams);

            Size pictureSize = CamParaUtil.getInstance().getPropPictureSize(
                    mParams.getSupportedPictureSizes(), previewRate, 800);
            mParams.setPictureSize(pictureSize.width, pictureSize.height);
            Size previewSize = CamParaUtil.getInstance().getPropPreviewSize(
                    mParams.getSupportedPreviewSizes(), previewRate, 800);
//            mParams.setPreviewSize(previewSize.width, previewSize.height);
            mParams.setPreviewSize(1920, 1080 - 48);

//            mCamera.setDisplayOrientation(90);

            CamParaUtil.getInstance().printSupportFocusMode(mParams);
            List<String> focusModes = mParams.getSupportedFocusModes();
            if (focusModes.contains("continuous-video")) {
                mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            mCamera.setParameters(mParams);

            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }

            isPreviewing = true;
            mPreviwRate = previewRate;

            mParams = mCamera.getParameters();
            Log.i(TAG, "PreviewSize--With = " + mParams.getPreviewSize().width
                    + "Height = " + mParams.getPreviewSize().height);
            Log.i(TAG, "PictureSize--With = " + mParams.getPictureSize().width
                    + "Height = " + mParams.getPictureSize().height);
        }
    }

    public void doStopCamera() {
        if (null != mCamera) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.stopFaceDetection();
            isPreviewing = false;
            mPreviwRate = -1f;
            mCamera.release();
            mCamera = null;
        }
    }

    public void doTakePicture(List<Rect> rects) {
        if (isPreviewing && (mCamera != null)) {
            mFaceRect.addAll(rects);
            mCamera.stopFaceDetection();
            mCamera.takePicture(mShutterCallback, null, mJpegPictureCallback);
        }
    }

    public Camera.Parameters getCameraParams() {
        if (mCamera != null) {
            mParams = mCamera.getParameters();
            return mParams;
        }
        return null;
    }

    public Camera getCameraDevice() {
        return mCamera;
    }

    public int getCameraId() {
        return mCameraId;
    }

    public interface CamOpenOverCallback {
        void cameraHasOpened();
    }
}
