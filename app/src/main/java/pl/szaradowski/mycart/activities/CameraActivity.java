package pl.szaradowski.mycart.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

import pl.szaradowski.mycart.R;
import pl.szaradowski.mycart.common.DBManager;
import pl.szaradowski.mycart.common.Receipt;
import pl.szaradowski.mycart.common.Utils;
import pl.szaradowski.mycart.components.IconButton;
import pl.szaradowski.mycart.components.RichTextView;
import pl.szaradowski.mycart.components.camera.CameraSource;
import pl.szaradowski.mycart.components.camera.CameraSourcePreview;

public class CameraActivity extends AppCompatActivity {
    RichTextView title;
    IconButton menu, back, takephoto;
    CameraSourcePreview preview;
    pl.szaradowski.mycart.components.camera.CameraSource mCameraSource;

    long id_receipt = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        menu = findViewById(R.id.menu);
        back = findViewById(R.id.back);
        takephoto = findViewById(R.id.takephoto);
        preview = findViewById(R.id.preview);
        title = findViewById(R.id.title);

        createCameraSource();

        Intent intent = getIntent();
        id_receipt = intent.getLongExtra("id_receipt", -1);

        menu.setVisibility(View.GONE);
        title.setText(getString(R.string.receipt_photo_title));

        back.setOnClickListener(new IconButton.OnClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });

        takephoto.setOnClickListener(new IconButton.OnClickListener() {
            @Override
            public void onClick() {
                if(mCameraSource != null) {
                    mCameraSource.takePicture(new CameraSource.ShutterCallback() {
                        @Override
                        public void onShutter() {

                        }
                    }, new CameraSource.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                            Matrix matrix = new Matrix();
                            if(bitmap.getWidth() >= bitmap.getHeight()) matrix.postRotate(90);

                            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, true);
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                            if(id_receipt != -1){
                                Receipt receipt = Utils.db.getReceiptById(id_receipt);
                                receipt.setImg(CameraActivity.this, bitmap);
                                Utils.db.setReceipt(receipt, DBManager.ACTION_UPDATE);
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }

    private void createCameraSource() {
        TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();

        mCameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setRequestedFps(24.0f)
                .setFlashMode(null)
                .setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)
                .build();
    }

    private void startCameraSource() throws SecurityException {
        if (mCameraSource != null) {
            try {
                preview.start(mCameraSource, null);
            } catch (IOException e) {
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (preview != null) {
            preview.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (preview != null) {
            preview.release();
        }
    }
}
