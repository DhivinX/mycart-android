/*
 * Created by Dominik Szaradowski on 25.05.19 18:43
 * Copyright (c) 2019 . All rights reserved.
 * Website: https://www.szaradowski.pl
 */

package pl.szaradowski.mycart.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import pl.szaradowski.mycart.R;

public class RichEditText extends AppCompatEditText {
    String font;

    public RichEditText(Context context) {
        super(context);

        Typeface tf = Typeface.createFromAsset(context.getAssets(),"fonts/Font-Regular.ttf");
        setTypeface(tf);
    }

    public RichEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        style(context, attrs);
    }

    public RichEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        style(context, attrs);
    }

    private void style(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RichEditText);
        font = a.getString(R.styleable.RichEditText_textFont);

        Typeface tf = Typeface.createFromAsset(context.getAssets(),"fonts/" + font + ".ttf");
        setTypeface(tf);
        a.recycle();
    }
}
