package com.jupiter.facedetection.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.jupiter.facedetection.R;
import com.jupiter.facedetection.widget.MediaFaceDetectView;

/**
 * Created by lchad on 2017/7/20.
 * android.media.FaceDetector
 * 底层代码:android/external/neven/
 * 只能接受Bitmap 格式的数据.
 * 只能识别双眼距离大于20 像素的人脸像,可以考虑一下这个限制是否可以在 FrameWork 中修改.
 */
public class MediaFaceDetectorActivity extends AppCompatActivity {

    private MediaFaceDetectView mMediaFaceDetectView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_media_face_detect);
        mMediaFaceDetectView = (MediaFaceDetectView) findViewById(R.id.media_face_view);
        mMediaFaceDetectView.init(R.drawable.ldh3);
    }

}
