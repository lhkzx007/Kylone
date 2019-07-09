package com.kylone.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

public class RecyclerView extends android.support.v7.widget.RecyclerView {
    public RecyclerView(Context context) {
        this(context, null);
    }

    public RecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setClipChildren(false);
        setClipToPadding(false);
    }
}
