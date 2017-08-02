package com.jupiter.facedetection.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.jupiter.facedetection.ObjectDetector;
import com.jupiter.facedetection.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * Created by lchad on 2017/7/18.
 */
public class OpenCVActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "Jupiter-OpenCV";
    //openCV 里 Scalar 三彩色通道的顺序是BGR.
    private static final Scalar FACE_RECT_COLOR = new Scalar(255, 0, 0);
    private static final Scalar EYE_RECT_COLOR = new Scalar(0, 255, 0);

    static {
        System.loadLibrary("opencv_java");
        System.loadLibrary("opencv_java3");
        System.loadLibrary("jupiter_opencv_320");
    }

    private CameraBridgeViewBase mOpenCvCameraView;

    private Mat mRgba;
    private Mat mGray;

    //脸部识别
    private ObjectDetector mFaceDetector;

    //眼部识别
    private ObjectDetector mEyeDetector;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    Log.i(TAG, "OpenCV loaded successfully");
                    //R.raw.haarcascade_profileface           //头发挡住额头效果一般
                    //R.raw.haarcascade_frontalface_alt       //头发挡住额头貌似可以
                    //R.raw.haarcascade_frontalface_alt2      //头发挡住额头貌似可以,比haarcascade_frontalface_default要好
                    //R.raw.haarcascade_frontalface_default   //头发挡住额头貌似可以,也有误识别现象

                    //R.raw.lbpcascade_frontalface            //头发挡住额头效果最差
                    mFaceDetector = new ObjectDetector(getApplicationContext(), R.raw.haarcascade_frontalface_alt, 4, 0.1F, 0.1F, FACE_RECT_COLOR);
                    mEyeDetector = new ObjectDetector(getApplicationContext(), R.raw.haarcascade_eye, 6, 0.05F, 0.05F, EYE_RECT_COLOR);
                    mOpenCvCameraView.enableView();
                    break;
                case LoaderCallbackInterface.INCOMPATIBLE_MANAGER_VERSION:
                    Log.i(TAG, "OpenCV INCOMPATIBLE_MANAGER_VERSION");
                    break;
                case LoaderCallbackInterface.INIT_FAILED:
                    Log.i(TAG, "OpenCV INIT_FAILED");
                    break;
                case LoaderCallbackInterface.INSTALL_CANCELED:
                    Log.i(TAG, "OpenCV INSTALL_CANCELED");
                    break;
                case LoaderCallbackInterface.MARKET_ERROR:
                    Log.i(TAG, "OpenCV MARKET_ERROR");
                    break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        findViewById(R.id.switch_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOpenCvCameraView.switchCamera();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        MatOfRect faces = new MatOfRect();

        Rect[] facesArray = mFaceDetector.detectObject(mGray, faces);
        for (int i = 0; i < facesArray.length; i++) {
            Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
        }

//        MatOfRect eyes = new MatOfRect();
//
//        Rect[] eyesArray = mEyeDetector.detectObject(mGray, eyes);
//        for (Rect rect : eyesArray) {
//            Imgproc.rectangle(mRgba, rect.tl(), rect.br(), mEyeDetector.getRectColor(), 3);
//        }

        return mRgba;
    }
}
