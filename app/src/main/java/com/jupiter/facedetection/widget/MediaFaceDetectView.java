package com.jupiter.facedetection.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by lchad on 2017/7/20.
 */

public class MediaFaceDetectView extends View {
    //两眼之间的距离
    float mEyesDistance;
    //实际检测到的人脸数
    int mNumberOfFaceDetected;
    Bitmap mBitmap;
    private int mImageWidth;
    private int mImageHeight;
    //最大检测的人脸数
    private int mMaxNumberOfFace = 5;
    //人脸识别类的实例
    private FaceDetector mFaceDetect;
    //存储多张人脸的数组变量
    private FaceDetector.Face[] mFaces;
    private Paint mPaint;
    private PointF mPointF = new PointF();

    public MediaFaceDetectView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(int drawable) {
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(3);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;  //必须为565
        mBitmap = BitmapFactory.decodeResource(getResources(), drawable, options);
        mImageWidth = mBitmap.getWidth();
        mImageHeight = mBitmap.getHeight();
        mFaces = new FaceDetector.Face[mMaxNumberOfFace];
        mFaceDetect = new FaceDetector(mImageWidth, mImageHeight, mMaxNumberOfFace);
        mNumberOfFaceDetected = mFaceDetect.findFaces(mBitmap, mFaces);
        Log.e("jupiter", "mNumberOfFaceDetected is " + mNumberOfFaceDetected);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mFaces == null) {
            return;
        }
        canvas.drawBitmap(mBitmap, 0, 0, null);

        for (int i = 0; i < mNumberOfFaceDetected; i++) {
            FaceDetector.Face face = mFaces[i];
            face.getMidPoint(mPointF);
            mEyesDistance = face.eyesDistance();
            canvas.drawRect(
                    (int) (mPointF.x - mEyesDistance),
                    (int) (mPointF.y - mEyesDistance),
                    (int) (mPointF.x + mEyesDistance),
                    (int) (mPointF.y + mEyesDistance),
                    mPaint);
        }
    }
}
