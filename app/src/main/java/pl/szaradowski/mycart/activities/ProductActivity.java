/*
 * Created by Dominik Szaradowski on 24.05.19 18:31
 * Copyright (c) 2019 . All rights reserved.
 * Website: https://www.szaradowski.pl
 */

package pl.szaradowski.mycart.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.text.HtmlCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.dynamite.DynamiteModule;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.List;

import pl.szaradowski.mycart.R;
import pl.szaradowski.mycart.common.Product;
import pl.szaradowski.mycart.common.Receipt;
import pl.szaradowski.mycart.common.Utils;
import pl.szaradowski.mycart.components.IconButton;
import pl.szaradowski.mycart.components.RichEditText;
import pl.szaradowski.mycart.components.RichTextView;

public class ProductActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    RichTextView title, previous_product_text;
    ImageView ivPicture;
    IconButton back, camera, menu, save, fav;
    CameraSource mCameraSource;
    SurfaceView mCameraView;
    RichEditText etName, etPrice, etCnt;
    CoordinatorLayout rootView;
    Bitmap picture = null;
    CardView previous_product;

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
        camera = findViewById(R.id.camera);
        menu = findViewById(R.id.menu);
        save = findViewById(R.id.save);
        fav = findViewById(R.id.fav);
        etName = findViewById(R.id.etName);
        etPrice = findViewById(R.id.etPrice);
        etCnt = findViewById(R.id.etCnt);
        rootView = findViewById(R.id.rootView);
        ivPicture = findViewById(R.id.ivPicture);
        previous_product = findViewById(R.id.previous_product);
        previous_product_text = findViewById(R.id.previous_product_text);

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
            product.setName("No name");
            product.setReceiptId(receipt_id);
            product.setTime(System.currentTimeMillis());

            float cnt = 1;
            etCnt.setText(cnt+"");
        }else{
            product = receipt.getProduct(product_id);

            etName.setText(product.getName());
            etPrice.setText(String.format(Utils.locale, "%.2f", product.getPrice()));
            etCnt.setText(product.getCnt()+"");

            if(product.getImg(this) != null){
                ivPicture.setImageDrawable(cropBitmap(product.getImg(this)));
                ivPicture.setVisibility(View.VISIBLE);
            }

            findLastProduct();

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

        menu.setOnClickListener(new IconButton.OnClickListener() {
            @Override
            public void onClick() {
                showMenu();
            }
        });

        mCameraView.post(new Runnable() {
            @Override
            public void run() {
                int width = mCameraView.getWidth();
                int height = mCameraView.getHeight();

                int marginTop = (height - width) / 2;

                CoordinatorLayout.LayoutParams fp = new CoordinatorLayout.LayoutParams(width, width);
                fp.setMargins(0, marginTop, 0, 0);
                mCameraView.setLayoutParams(fp);
            }
        });

        camera.setOnClickListener(new IconButton.OnClickListener() {
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
                            if(bitmap.getWidth() > bitmap.getHeight()) matrix.postRotate(90);

                            bitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true);
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                            sendBitmapAndShowDetections(bitmap);
                        }
                    });
                }
            }
        });

        save.setOnClickListener(new IconButton.OnClickListener() {
            @Override
            public void onClick() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rootView.getWindowToken(),0);

                String name = etName.getText().toString();
                float price = 0;
                float cnt = 0;

                boolean okPrice = true;
                boolean okCnt = true;
                boolean okName = true;

                try{
                    String p = etPrice.getText().toString().replace(",", ".");
                    price = Float.parseFloat(p);
                }catch (NumberFormatException e){
                    okPrice = false;
                }

                try{
                    cnt = Float.parseFloat(etCnt.getText().toString());
                }catch (NumberFormatException e){
                    okCnt = false;
                }

                if(price < 0) okPrice = false;
                if(cnt <= 0) okCnt = false;
                if(name.length() == 0) okName = false;

                if(okName){
                    if(okPrice){
                        if(okCnt){
                            if(product_id == -1) {
                                receipt.addProduct(product);
                            }

                            product.setName(name);
                            product.setPrice(price);
                            product.setCnt(cnt);

                            if(picture != null) product.setImg(ProductActivity.this, picture);
                            finish();
                        }else Snackbar.make(rootView, R.string.error_okcnt, Snackbar.LENGTH_SHORT).show();
                    }else Snackbar.make(rootView, R.string.error_okprice, Snackbar.LENGTH_SHORT).show();
                }else Snackbar.make(rootView, R.string.error_okname, Snackbar.LENGTH_SHORT).show();
            }
        });

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                final String name = s.toString();

                if(name.length() > 0) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            product.setName(name);

                            findLastProduct();
                        }
                    });
                }
            }
        });

        etCnt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    save.callOnClick();
                }
                return false;
            }
        });
    }

    private void findLastProduct(){
        Product p = Receipt.getLastProductFrom(product);

        if(p != null){
            previous_product.setVisibility(View.VISIBLE);
            etPrice.setText(String.format(Utils.locale, "%.2f", p.getPrice()));

            previous_product_text.setText(
                    HtmlCompat.fromHtml(
                            getString(R.string.previous_product, "<b><font color='red'>"+String.format(Utils.locale, "%.2f", p.getPrice()), p.getCurrency())+"</font></b>",
                            HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
            );
        }else{
            previous_product.setVisibility(View.GONE);
        }
    }

    private void sendBitmapAndShowDetections(final Bitmap bitmap){
        AlertDialog.Builder builder = new AlertDialog.Builder(ProductActivity.this);
        builder.setIcon(R.drawable.icon_change_dark);
        builder.setTitle(R.string.product_dialog_ocr_title);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ProductActivity.this, android.R.layout.select_dialog_item);

        if (items_recognized.size() != 0 ){
            for(int i = 0; i < items_recognized.size(); i++){
                TextBlock item = items_recognized.valueAt(i);
                arrayAdapter.add(item.getValue());
            }
        }

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton(R.string.save_only_photo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                picture = bitmap;

                ivPicture.setImageDrawable(cropBitmap(picture));
                ivPicture.setVisibility(View.VISIBLE);

                dialog.dismiss();
            }
        });

        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);

                strName = strName.replaceAll("\n", " ");

                etName.setText(strName);

                picture = bitmap;

                ivPicture.setImageDrawable(cropBitmap(picture));
                ivPicture.setVisibility(View.VISIBLE);

                if(etPrice.getText().length() == 0){
                    etPrice.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                }
            }
        });

        builder.show();
    }

    private void startCameraSource(){
        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if(status == ConnectionResult.SUCCESS){
            final TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

            try{
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;

                mCameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                        .setFacing(CameraSource.CAMERA_FACING_BACK)
                        .setRequestedPreviewSize(width, width)
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
            }catch (Exception e){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startCameraSource();
                    }
                }, 5000);
            }
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(ProductActivity.this);
            builder.setTitle("Proszę zaaktualizować Usługi GooglePlay.");

            builder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.gms")));
                    dialog.dismiss();
                    finish();
                }
            });

            builder.show();
        }
    }

    public void showMenu(){
        PopupMenu popup = new PopupMenu(ProductActivity.this, menu);
        popup.setOnMenuItemClickListener(ProductActivity.this);

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_product, popup.getMenu());

        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        String action = (String) menuItem.getTitleCondensed();

        if(action.equals("delete")){
            if(product_id != -1){
                receipt.removeProduct(ProductActivity.this, product);
            }

            finish();
        }

        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(rootView.getWindowToken(),0);
    }

    public RoundedBitmapDrawable cropBitmap(Bitmap bitmap) {
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        roundedBitmapDrawable.setCircular(true);
        roundedBitmapDrawable.setAntiAlias(true);

        return roundedBitmapDrawable;
    }
}
