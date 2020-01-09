/*
 * Created by Dominik Szaradowski on 22.05.19 11:20
 * Copyright (c) 2019 . All rights reserved.
 * Website: https://www.szaradowski.pl
 */

package pl.szaradowski.mycart.components;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import pl.szaradowski.mycart.R;

public class RichTextView extends AppCompatTextView {
    String font;

    public RichTextView(Context context) {
        super(context);
    }

    public RichTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        style(context, attrs);
    }

    public RichTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        style(context, attrs);
    }

    private void style(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RichTextView);
        font = a.getString(R.styleable.RichTextView_textFont);

        Typeface tf = Typeface.createFromAsset(context.getAssets(),"fonts/" + font + ".ttf");
        setTypeface(tf);
        a.recycle();
    }
}
