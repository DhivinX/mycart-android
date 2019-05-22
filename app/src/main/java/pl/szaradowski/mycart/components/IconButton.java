/*
 * Created by Dominik Szaradowski on 22.05.19 08:56
 * Copyright (c) 2019 . All rights reserved.
 * Last modified 22.05.19 08:56
 * Website: https://www.szaradowski.pl
 */

package pl.szaradowski.mycart.components;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import pl.szaradowski.mycart.R;

public class IconButton extends AppCompatImageView {
    public interface OnClickListener{
        void onClick();
    }

    Context ctx;
    IconButton.OnClickListener onClickListener = null;

    public IconButton(Context context) {
        super(context);
        ctx = context;

        init();
    }

    public IconButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        ctx = context;

        init();
    }

    public IconButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ctx = context;

        init();
    }

    public void init(){
        this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animShake();

                if(onClickListener != null){
                    onClickListener.onClick();
                }
            }
        });
    }

    public void setOnClickListener(IconButton.OnClickListener listener){
        onClickListener = listener;
    }

    public void animShake(){
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.shake);
        this.startAnimation(a);
    }

    public void animScale(){
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.scale_background);
        this.startAnimation(a);
    }
}
