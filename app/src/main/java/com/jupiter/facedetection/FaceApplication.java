package com.jupiter.facedetection;

import android.app.Application;

/**
 * Created by lchad on 2017/7/26.
 */

public class FaceApplication extends Application {
    private static FaceApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static FaceApplication getInstance() {
        return instance;
    }
}
