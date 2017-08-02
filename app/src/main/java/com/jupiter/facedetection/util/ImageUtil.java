package com.jupiter.facedetection.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class ImageUtil {
    public static Bitmap getRotateBitmap(Bitmap b, float rotateDegree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotateDegree);
        return Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, false);
    }
}
