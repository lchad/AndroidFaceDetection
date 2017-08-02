package com.jupiter.facedetection.util;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {
    private static final String TAG = "FileUtil";
    private static final File parentPath = Environment.getExternalStorageDirectory();
    private static final String DST_FOLDER_NAME = "Jupiter";
    private static String storagePath = "";

    private static String initPath() {
        if (storagePath.equals("")) {
            storagePath = parentPath.getAbsolutePath() + "/" + DST_FOLDER_NAME;
            File f = new File(storagePath);
            if (!f.exists()) {
                f.mkdir();
            }
        }
        return storagePath;
    }

    public static void saveBitmap(Bitmap b) {
        long timeStamp = System.currentTimeMillis();
        String jpegName = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                + timeStamp + ".jpg";

        Log.i(TAG, "saveBitmap:jpegName = " + jpegName);
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
