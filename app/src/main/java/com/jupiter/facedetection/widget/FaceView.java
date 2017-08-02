package com.jupiter.facedetection.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Face;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.jupiter.facedetection.CameraInterface;
import com.jupiter.facedetection.R;
import com.jupiter.facedetection.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lchad on 2017/7/20.
 */
public class FaceView extends android.support.v7.widget.AppCompatImageView {
    private Paint mLinePaint;
    private Face[] mFaces;
    private Matrix mMatrix = new Matrix();
    private RectF mRect = new RectF();
    private Bitmap mHongbao;
    //记录可点击区域的坐标
    private List<Rect> mClickableRecord = new ArrayList<>();

    public FaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
        mHongbao = BitmapFactory.decodeResource(getResources(), R.drawable.ic_hongbao);
        for (int i = 0; i < 10; i++) {
            mClickableRecord.add(new Rect(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE));
        }
    }


    public void setFaces(Face[] faces) {
        this.mFaces = faces;
        invalidate();
    }

    public void clearFaces() {
        mFaces = null;
        invalidate();
    }

    private void resetClickableRect() {
        for (Rect rect : mClickableRecord) {
            rect.set(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mFaces == null || mFaces.length < 1) {
            return;
        }
        boolean isMirror = false;
        int cameraId = CameraInterface.getInstance().getCameraId();
        if (cameraId == CameraInfo.CAMERA_FACING_BACK) {
            isMirror = false;
        } else if (cameraId == CameraInfo.CAMERA_FACING_FRONT) {
            isMirror = true;
        }
        Util.prepareMatrix(mMatrix, isMirror, 90, getWidth(), getHeight());
        canvas.save();
        mMatrix.postRotate(0);
        canvas.rotate(-0);

        resetClickableRect();

        for (int i = 0; i < mFaces.length; i++) {
            Face mFace = mFaces[i];
            mRect.set(mFace.rect);
            mMatrix.mapRect(mRect);
            canvas.drawRoundRect(mRect, 15, 15, mLinePaint);

            if (mHongbao != null) {
                Bitmap scaledBitmap = scaleDown(mHongbao, mRect.width());
                float leftEdge = mRect.left + (mRect.right - mRect.left) / 2 - scaledBitmap.getWidth() / 2;
                float topEdge = mRect.top + (mRect.top - mRect.bottom) * 1.5f;
                mClickableRecord.get(i).set((int) mRect.left, (int) topEdge, (int) mRect.right, (int) mRect.bottom);
                canvas.drawBitmap(scaledBitmap, leftEdge, topEdge, null);
            }
        }
        canvas.restore();
        super.onDraw(canvas);
    }

    private void initPaint() {
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int color = Color.rgb(255, 255, 255);
        mLinePaint.setColor(color);
        mLinePaint.setStyle(Style.STROKE);
        mLinePaint.setStrokeWidth(4f);
        mLinePaint.setAlpha(180);
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize) {
        float ratio = Math.min(
                maxImageSize / realImage.getWidth(),
                maxImageSize / realImage.getHeight());
        int width = Math.round(ratio * realImage.getWidth());
        int height = Math.round(ratio * realImage.getHeight());

        return Bitmap.createScaledBitmap(realImage, width, height, false);
    }

    /**
     * 捕捉点击事件，并通过回调接口传出
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                handlerClickEvent((int) event.getX(), (int) event.getY());
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 如果有多张脸的矩形区域相互交叉呢
     */
    private void handlerClickEvent(int x, int y) {
        //单击之后,有效的脸部区域
        List<Rect> valueFaceRect = new ArrayList<>();
        for (Rect rect : mClickableRecord) {
            if (rect.contains(x, y)) {
                valueFaceRect.add(new Rect(rect));
            }
        }
        if (mSingleTapListener != null && valueFaceRect.size() > 0) {
            mSingleTapListener.onSingleTap(x, y, valueFaceRect);
        }
    }

    public interface SingleTapListener {
        void onSingleTap(float x, float y, List<Rect> rects);
    }

    private SingleTapListener mSingleTapListener;

    public void setSingleTapListener(SingleTapListener singleTapListener) {
        mSingleTapListener = singleTapListener;
    }
}
