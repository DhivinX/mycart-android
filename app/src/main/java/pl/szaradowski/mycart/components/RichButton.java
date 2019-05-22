/*
 * Created by Dominik Szaradowski on 22.05.19 11:20
 * Copyright (c) 2019 . All rights reserved.
 * Website: https://www.szaradowski.pl
 */

package pl.szaradowski.mycart.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import pl.szaradowski.mycart.R;

public class RichButton extends AppCompatButton {
    String font;

    public RichButton(Context context) {
        super(context);
    }

    public RichButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        style(context, attrs);
    }

    public RichButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        style(context, attrs);
    }

    private void style(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RichButton);
        font = a.getString(R.styleable.RichButton_textFont);

        Typeface tf = Typeface.createFromAsset(context.getAssets(),"fonts/" + font + ".ttf");
        setTypeface(tf);
        a.recycle();
    }
}
