package com.jupiter.facedetection;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;

/**
 * Created by lchad on 2017/7/18.
 */

public class DetectionTracker {
    private long mNativeObj = 0;

    public DetectionTracker(String cascadeName, int minFaceSize) {
        mNativeObj = nativeCreateObject(cascadeName, minFaceSize);
    }

    private static native long nativeCreateObject(String cascadeName, int minFaceSize);

    private static native void nativeDestroyObject(long thiz);

    private static native void nativeStart(long thiz);

    private static native void nativeStop(long thiz);

    private static native void nativeSetFaceSize(long thiz, int size);

    private static native void nativeDetect(long thiz, long inputImage, long faces);

    public void start() {
        nativeStart(mNativeObj);
    }

    public void stop() {
        nativeStop(mNativeObj);
    }

    public void setMinFaceSize(int size) {
        nativeSetFaceSize(mNativeObj, size);
    }

    public void detect(Mat imageGray, MatOfRect faces) {
        nativeDetect(mNativeObj, imageGray.getNativeObjAddr(), faces.getNativeObjAddr());
    }

    public void release() {
        nativeDestroyObject(mNativeObj);
        mNativeObj = 0;
    }
}

