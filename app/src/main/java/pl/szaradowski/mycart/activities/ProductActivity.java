/*
 * Created by Dominik Szaradowski on 24.05.19 18:31
 * Copyright (c) 2019 . All rights reserved.
 * Website: https://www.szaradowski.pl
 */

package pl.szaradowski.mycart.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

import pl.szaradowski.mycart.R;
import pl.szaradowski.mycart.common.Product;
import pl.szaradowski.mycart.common.Receipt;
import pl.szaradowski.mycart.components.IconButton;
import pl.szaradowski.mycart.components.RichTextView;

public class ProductActivity extends AppCompatActivity {
    RichTextView title;
    IconButton back, camera;
    CameraSource mCameraSource;
    SurfaceView mCameraView;
    TextView mTextView;

    int receipt_id = -1;
    int product_id = -1;
    Receipt receipt = null;
    Product product = null;

    SparseArray<TextBlock> items_recognized;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        title = findViewById(R.id.title);
        back = findViewById(R.id.back);
        mCameraView = findViewById(R.id.mCameraView);
        mTextView = findViewById(R.id.mTextView);
        camera = findViewById(R.id.camera);

        Intent intent = getIntent();
        receipt_id = intent.getIntExtra("receipt_id", -1);
        product_id = intent.getIntExtra("product_id", -1);

        if(receipt_id == -1) {
            finish();
            return;
        }else {
            receipt = Receipt.getById(receipt_id);
        }

        if(product_id == -1){
            product = new Product();
            product.setName("test");

            receipt.addProduct(product);
        }else{
            product = receipt.getProduct(product_id);

            if(product == null) {
                finish();
                return;
            }
        }

        title.setText(getString(R.string.product_title));
        startCameraSource();

        back.setOnClickListener(new IconButton.OnClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });

        mCameraView.post(new Runnable() {
            @Override
            public void run() {
                int width = mCameraView.getWidth();
                int height = mCameraView.getHeight();

                int marginTop = (height - width) / 2;

                FrameLayout.LayoutParams fp = new FrameLayout.LayoutParams(width, width);
                fp.setMargins(0, marginTop, 0, 0);
                mCameraView.setLayoutParams(fp);
            }
        });

        camera.setOnClickListener(new IconButton.OnClickListener() {
            @Override
            public void onClick() {
                mCameraSource.takePicture(new CameraSource.ShutterCallback() {
                    @Override
                    public void onShutter() {

                    }
                }, new CameraSource.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                        showDetections();
                    }
                });
            }
        });
    }

    private void showDetections(){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(ProductActivity.this);
        builderSingle.setIcon(R.drawable.icon_change_dark);
        builderSingle.setTitle(R.string.product_dialog_ocr_title);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ProductActivity.this, android.R.layout.select_dialog_item);

        if (items_recognized.size() != 0 ){
            for(int i = 0; i < items_recognized.size(); i++){
                TextBlock item = items_recognized.valueAt(i);
                arrayAdapter.add(item.getValue());
            }
        }

        builderSingle.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);

            }
        });

        builderSingle.show();
    }

    private void startCameraSource(){
        final TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if(textRecognizer.isOperational()){
            mCameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1000, 1000)
                    .setAutoFocusEnabled(true)
                    .setRequestedFps(30.0f)
                    .build();

            mCameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(ProductActivity.this, new String[]{Manifest.permission.CAMERA}, 33);
                            return;
                        }

                        mCameraSource.start(mCameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    mCameraSource.stop();
                }
            });

            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {
                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    items_recognized = detections.getDetectedItems();
                }
            });
        }
    }

}
