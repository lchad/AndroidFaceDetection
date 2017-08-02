package com.jupiter.facedetection;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.jupiter.facedetection.ui.CameraAPIActivity;
import com.jupiter.facedetection.util.EventUtil;

/**
 * Created by lchad on 2017/7/20.
 */
public class GoogleFaceDetect implements FaceDetectionListener {
    private static final String TAG = "jupiter";
    private Handler mHandler;
    private Context mContext;

    public GoogleFaceDetect(Context context) {
        mContext = context;
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    @Override
    public void onFaceDetection(Face[] faces, Camera camera) {

        Log.i(TAG, "onFaceDetection...");
        if (faces != null) {

            Toast.makeText(mContext, faces.length + "", Toast.LENGTH_SHORT).show();
            Message m = mHandler.obtainMessage();
            m.what = EventUtil.UPDATE_FACE_RECT;
            m.obj = faces;
            m.sendToTarget();
        }
    }

/*	private Rect getPropUIFaceRect(Rect r){
        Matrix m = new Matrix();
		boolean mirror = false;
		m.setScale(mirror ? -1 : 1, 1);
		Point p = DisplayUtil.getScreenMetrics(mContext);
		int uiWidth = p.x;
		int uiHeight = p.y;
		m.postScale(uiWidth/2000f, uiHeight/2000f);
		int leftNew = (r.left + 1000)*uiWidth/2000;
		int topNew = (r.top + 1000)*uiHeight/2000;
		int rightNew = (r.right + 1000)*uiWidth/2000;
		int bottomNew = (r.bottom + 1000)*uiHeight/2000;
		
		return new Rect(leftNew, topNew, rightNew, bottomNew);
	}*/

}
