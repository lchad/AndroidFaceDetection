package com.jupiter.facedetection.ui;

import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.jupiter.facedetection.CameraInterface;
import com.jupiter.facedetection.FaceApplication;
import com.jupiter.facedetection.GoogleFaceDetect;
import com.jupiter.facedetection.R;
import com.jupiter.facedetection.util.DisplayUtil;
import com.jupiter.facedetection.util.EventUtil;
import com.jupiter.facedetection.widget.CameraSurfaceView;
import com.jupiter.facedetection.widget.FaceView;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by lchad on 2017/7/20.
 */
public class CameraAPIActivity extends AppCompatActivity {
    CameraSurfaceView mCameraSurfaceView = null;
    Button switchBtn;
    FaceView faceView;

    //屏幕高度与屏幕宽度的比例.
    float previewRate = -1f;
    GoogleFaceDetect googleFaceDetect = null;
    private MainHandler mMainHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_camera_api);
        initView();
        initViewParams();
        googleFaceDetect = new GoogleFaceDetect(CameraAPIActivity.this);
        mMainHandler = new MainHandler(faceView, googleFaceDetect);

        mMainHandler.sendEmptyMessageDelayed(EventUtil.CAMERA_HAS_STARTED_PREVIEW, 1500);
    }

    private void initView() {
        mCameraSurfaceView = (CameraSurfaceView) findViewById(R.id.camera_surfaceview);
        faceView = (FaceView) findViewById(R.id.face_view);
        faceView.setSingleTapListener(new FaceView.SingleTapListener() {
            @Override
            public void onSingleTap(float x, float y, List<Rect> rects) {
                //通过PictureCallback的回调拿到原始 bitmap, 根据 rects进行裁剪,然后展示,上传
                Log.e("123456789", " x:" + x + " y:" + y + " rect size: " + rects.size());
                if (rects.size() > 0) {
                    for (Rect rect : rects) {
                        Log.e("123456789", " rect : " + rect.flattenToString());
                    }
                    takePicture(rects);
                }
            }
        });
        switchBtn = (Button) findViewById(R.id.btn_switch);
        switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCamera();
            }
        });
    }

    private void initViewParams() {
        ViewGroup.LayoutParams params = mCameraSurfaceView.getLayoutParams();
        Point p = DisplayUtil.getScreenMetrics(this);
        params.width = p.x;
        params.height = p.y;
        previewRate = DisplayUtil.getScreenRate(this);
        mCameraSurfaceView.setLayoutParams(params);
    }

    private void takePicture(List<Rect> rects) {
        CameraInterface.getInstance().doTakePicture(rects);
        mMainHandler.sendEmptyMessageDelayed(EventUtil.CAMERA_HAS_STARTED_PREVIEW, 1500);
    }

    private void switchCamera() {
        stopGoogleFaceDetect();
        //0 后置; 1 前置
        int newId = (CameraInterface.getInstance().getCameraId() + 1) % 2;
        CameraInterface.getInstance().doStopCamera();
        CameraInterface.getInstance().doOpenCamera(null, newId);
        CameraInterface.getInstance().doStartPreview(mCameraSurfaceView.getSurfaceHolder(), previewRate);
        mMainHandler.sendEmptyMessageDelayed(EventUtil.CAMERA_HAS_STARTED_PREVIEW, 1500);
    }

    private void stopGoogleFaceDetect() {
        Camera.Parameters params = CameraInterface.getInstance().getCameraParams();
        if (params.getMaxNumDetectedFaces() > 0) {
            CameraInterface.getInstance().getCameraDevice().setFaceDetectionListener(null);
            CameraInterface.getInstance().getCameraDevice().stopFaceDetection();
            faceView.clearFaces();
        }
    }

    private static class MainHandler extends Handler {
        private final WeakReference<FaceView> mFaceViewWeakReference;
        private final WeakReference<GoogleFaceDetect> mGoogleFaceDetectWeakReference;

        public MainHandler(FaceView faceView, GoogleFaceDetect googleFaceDetect) {
            mFaceViewWeakReference = new WeakReference<>(faceView);
            mGoogleFaceDetectWeakReference = new WeakReference<>(googleFaceDetect);
            mGoogleFaceDetectWeakReference.get().setHandler(MainHandler.this);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EventUtil.UPDATE_FACE_RECT:
                    Camera.Face[] faces = (Camera.Face[]) msg.obj;
                    mFaceViewWeakReference.get().setFaces(faces);
                    break;
                case EventUtil.CAMERA_HAS_STARTED_PREVIEW:
                    Camera.Parameters params = CameraInterface.getInstance().getCameraParams();
                    if (params != null && params.getMaxNumDetectedFaces() > 0) {
                        if (mFaceViewWeakReference.get() != null) {
                            mFaceViewWeakReference.get().clearFaces();
                        }
                        CameraInterface.getInstance().getCameraDevice().setFaceDetectionListener(mGoogleFaceDetectWeakReference.get());
                        CameraInterface.getInstance().getCameraDevice().stopFaceDetection();
                        CameraInterface.getInstance().getCameraDevice().startFaceDetection();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (faceView != null) {
            faceView.clearFaces();
        }
    }
}
