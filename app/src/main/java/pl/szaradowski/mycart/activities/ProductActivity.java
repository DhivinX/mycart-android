/*
 * Created by Dominik Szaradowski on 24.05.19 18:31
 * Copyright (c) 2019 . All rights reserved.
 * Website: https://www.szaradowski.pl
 */

package pl.szaradowski.mycart.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.text.HtmlCompat;
import androidx.core.widget.NestedScrollView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import pl.szaradowski.mycart.BuildConfig;
import pl.szaradowski.mycart.R;
import pl.szaradowski.mycart.common.DBManager;
import pl.szaradowski.mycart.common.Product;
import pl.szaradowski.mycart.common.Receipt;
import pl.szaradowski.mycart.common.Utils;
import pl.szaradowski.mycart.components.IconButton;
import pl.szaradowski.mycart.components.RichEditText;
import pl.szaradowski.mycart.components.RichTextView;
import pl.szaradowski.mycart.components.camera.CameraSource;
import pl.szaradowski.mycart.components.camera.CameraSourcePreview;
import pl.szaradowski.mycart.components.camera.GraphicOverlay;
import pl.szaradowski.mycart.components.camera.OcrGraphic;

public class ProductActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    int REQUEST_SPEECH_RECOGNIZER = 3000;

    RichTextView title, previous_product_text;
    ImageView ivPicture;
    IconButton back, camera, menu, save, fav, voice, minCnt, plsCnt;
    RichEditText etName, etPrice, etCnt;
    CoordinatorLayout rootView;
    Bitmap picture = null;
    CardView previous_product;
    AppBarLayout appbar;
    NestedScrollView scrollView;

    long id_receipt = -1;
    long id_product = -1;
    Receipt receipt = null;
    Product product = null;

    SparseArray<TextBlock> items_recognized;

    pl.szaradowski.mycart.components.camera.CameraSource mCameraSource;
    CameraSourcePreview mPreview;
    GraphicOverlay<OcrGraphic> mGraphicOverlay;

    private static final int RC_HANDLE_GMS = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        appbar = findViewById(R.id.appbar);
        title = findViewById(R.id.title);
        back = findViewById(R.id.back);
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
        scrollView = findViewById(R.id.scrollView);
        voice = findViewById(R.id.voice);
        minCnt = findViewById(R.id.minCnt);
        plsCnt = findViewById(R.id.plsCnt);

        minCnt.setWaitTime(0);
        plsCnt.setWaitTime(0);

        mPreview = findViewById(R.id.mPreview);
        mGraphicOverlay = findViewById(R.id.graphicOverlay);

        Intent intent = getIntent();
        id_receipt = intent.getLongExtra("id_receipt", -1);
        id_product = intent.getLongExtra("id_product", -1);

        if(id_receipt == -1) {
            finish();
            return;
        }else {
            receipt = Utils.db.getReceiptById(id_receipt);
        }

        if(id_product == -1){
            product = new Product();
            product.setIdReceipt(receipt.getId());
            product.setTime(System.currentTimeMillis());

            float cnt = 1;
            etCnt.setText(Utils.currency.formatCnt(cnt));
        }else{
            product = Utils.db.getProductById(id_product);

            etName.setText(product.getName());
            etPrice.setText(Utils.currency.format(product.getPrice()));
            etCnt.setText(Utils.currency.formatCnt(product.getCnt()));
            picture = product.getImg(this);

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
        createCameraSource();

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

        mPreview.post(new Runnable() {
            @Override
            public void run() {
                float dp = getResources().getDisplayMetrics().density;
                int width = mPreview.getWidth();
                int height = Math.round((float) mPreview.getHeight() * ((float) 2 / 3));

                CoordinatorLayout.LayoutParams fp = new CoordinatorLayout.LayoutParams(width, height);
                mPreview.setLayoutParams(fp);

                fp = new CoordinatorLayout.LayoutParams(width, height - Math.round(70*dp));
                appbar.setLayoutParams(fp);
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
                            if(bitmap.getWidth() >= bitmap.getHeight()) matrix.postRotate(90);

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
                    String c = etCnt.getText().toString().replace(",", ".");
                    cnt = Float.parseFloat(c);
                }catch (NumberFormatException e){
                    okCnt = false;
                }

                if(price < 0) okPrice = false;
                if(cnt <= 0) okCnt = false;
                if(name.length() == 0) okName = false;

                if(okName){
                    if(okPrice){
                        if(okCnt){
                            product.setName(name);
                            product.setPrice(price);
                            product.setCnt(cnt);

                            if(picture != null) product.setImg(ProductActivity.this, picture);

                            if(id_product == -1) {
                                long id = Utils.db.setProduct(product, DBManager.ACTION_INSERT);
                                product.setId(id);
                            }else{
                                Utils.db.setProduct(product, DBManager.ACTION_UPDATE);
                            }

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

        ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(picture != null) {
                    File file = Utils.saveBitmap("product-"+product.getId()+".jpg", picture);
                    Uri photoURI = FileProvider.getUriForFile(ProductActivity.this, BuildConfig.APPLICATION_ID + ".provider", file);

                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(photoURI, "image/*");
                    startActivity(intent);
                }
            }
        });

        voice.setOnClickListener(new IconButton.OnClickListener() {
            @Override
            public void onClick() {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.product_voice_question));
                startActivityForResult(intent, REQUEST_SPEECH_RECOGNIZER);
            }
        });

        minCnt.setOnClickListener(new IconButton.OnClickListener() {
            @Override
            public void onClick() {
                try {
                    String c = etCnt.getText().toString().replace(",", ".");
                    float cnt = Float.parseFloat(c);
                    cnt -= 1.0f;
                    if (cnt <= 0) cnt = 0.01f;
                    etCnt.setText(Utils.currency.formatCnt(cnt));
                }catch (Exception e){
                    float cnt = 0.01f;
                    etCnt.setText(Utils.currency.formatCnt(cnt));
                }
            }
        });

        plsCnt.setOnClickListener(new IconButton.OnClickListener() {
            @Override
            public void onClick() {
                try {
                    String c = etCnt.getText().toString().replace(",", ".");
                    float cnt = Float.parseFloat(c);
                    cnt += 1.0f;
                    etCnt.setText(Utils.currency.formatCnt(cnt));
                }catch (Exception e){
                    float cnt = 1f;
                    etCnt.setText(Utils.currency.formatCnt(cnt));
                }
            }
        });
    }

    private void findLastProduct(){
        Product p = Utils.db.findPreviousProductFromProduct(product);

        if(p != null){
            previous_product.setVisibility(View.VISIBLE);
            etPrice.setText(Utils.currency.format(p.getPrice()));

            previous_product_text.setText(
                    HtmlCompat.fromHtml(
                            getString(R.string.previous_product, "<b><font color='red'>"+Utils.currency.formatPrice(p.getPrice()))+"</font></b>",
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

                strName = strName.toUpperCase();

                etName.setText(strName);

                picture = bitmap;

                ivPicture.setImageDrawable(cropBitmap(picture));
                ivPicture.setVisibility(View.VISIBLE);

                if(etPrice.getText().length() == 0){
                    appbar.setExpanded(false);

                    etPrice.post(new Runnable() {
                        @Override
                        public void run() {
                            etPrice.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                        }
                    });
                }
            }
        });

        builder.show();
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
            if(id_product != -1){
                Utils.db.setProduct(product, DBManager.ACTION_DELETE);
            }

            finish();
        }

        return false;
    }

    public RoundedBitmapDrawable cropBitmap(Bitmap bitmap) {
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        roundedBitmapDrawable.setCircular(true);
        roundedBitmapDrawable.setAntiAlias(true);

        return roundedBitmapDrawable;
    }

    private void startCameraSource() throws SecurityException {
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext());

        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    @SuppressLint("InlinedApi")
    private void createCameraSource() {
        Context context = getApplicationContext();

        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
            @Override
            public void release() {
                mGraphicOverlay.clear();
            }

            @Override
            public void receiveDetections(Detector.Detections<TextBlock> detections) {
                mGraphicOverlay.clear();
                items_recognized = detections.getDetectedItems();

                SparseArray<TextBlock> items = detections.getDetectedItems();
                for (int i = 0; i < items.size(); ++i) {
                    TextBlock item = items.valueAt(i);
                    OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item);
                    mGraphicOverlay.add(graphic);
                }
            }
        });

        if (!textRecognizer.isOperational()) {
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProductActivity.this);
                builder.setMessage(R.string.vision_notext_storage);

                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(ProductActivity.this);
                builder.setMessage(R.string.vision_notext);

                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
            }
        }

        mCameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1000, 1000)
                .setRequestedFps(24.0f)
                .setFlashMode(null)
                //.setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                .setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SPEECH_RECOGNIZER) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String text = results.get(0);

                text = text.toUpperCase();

                etName.setText(text);

                if(etPrice.getText().length() == 0){
                    appbar.setExpanded(false);

                    etPrice.post(new Runnable() {
                        @Override
                        public void run() {
                            etPrice.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                        }
                    });
                }
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

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(rootView.getWindowToken(),0);

        if (mPreview != null) {
            mPreview.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(rootView.getWindowToken(),0);

        if (mPreview != null) {
            mPreview.release();
        }
    }
}
