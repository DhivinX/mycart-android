/*
 * Created by Dominik Szaradowski on 22.05.19 11:20
 * Copyright (c) 2019 . All rights reserved.
 * Website: https://www.szaradowski.pl
 */

package pl.szaradowski.mycart.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.widget.AppCompatButton;

import pl.szaradowski.mycart.R;

public class RichButton extends AppCompatButton {
    public interface OnClickListener{
        void onClick();
    }

    String font;
    Context ctx;
    RichButton.OnClickListener onClickListener = null;
    private boolean clickable = true;

    public RichButton(Context context) {
        super(context);
    }

    public RichButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RichButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs){
        ctx = context;

        this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animScale();

                if(onClickListener != null && clickable){
                    clickable = false;
                    onClickListener.onClick();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            clickable = true;
                        }
                    }, 1000);
                }
            }
        });

        style(attrs);
    }

    public void setOnClickListener(RichButton.OnClickListener listener){
        onClickListener = listener;
    }

    private void style(AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.RichButton);
        font = a.getString(R.styleable.RichButton_textFont);

        Typeface tf = Typeface.createFromAsset(ctx.getAssets(),"fonts/" + font + ".ttf");
        setTypeface(tf);
        a.recycle();
    }

    public void animShake(){
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.shake);
        this.startAnimation(a);
    }

    public void animScale(){
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.scale);
        this.startAnimation(a);
    }
}
