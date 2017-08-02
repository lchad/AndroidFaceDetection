package com.jupiter.facedetection.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.jupiter.facedetection.R;


/**
 * Created by lchad on 2017/7/18.
 */
public class HomeActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION = 233;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        findViewById(R.id.open_cv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, OpenCVActivity.class));
            }
        });

        findViewById(R.id.camera_api).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, CameraAPIActivity.class));
            }
        });

        findViewById(R.id.media).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, MediaFaceDetectorActivity.class));
            }
        });

        findViewById(R.id.vision).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, VisionFaceTrackerActivity.class));
            }
        });

        findViewById(R.id.vision_face_outline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, FaceOutlineActivity.class));
            }
        });

        boolean hasPermission = checkSinglePermission(Manifest.permission.CAMERA);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{
                            Manifest.permission.CAMERA},
                    REQUEST_PERMISSION);
        }
    }

    /**
     * 检查每个单项权限是否授予
     *
     * @param permission 权限名字.
     * @return 结果
     */
    private boolean checkSinglePermission(String permission) {
        return ContextCompat.checkSelfPermission(HomeActivity.this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(HomeActivity.this, "Camera is required, please grant the permission in settings.", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
