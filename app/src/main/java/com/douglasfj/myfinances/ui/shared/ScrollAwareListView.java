package com.douglasfj.myfinances.ui.shared;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/** Preserves inner scrolling and releases the outer page at the list edges. */
public class ScrollAwareListView extends ListView {
    private final InnerListScrolling scrolling;

    public ScrollAwareListView(Context context) { this(context,null); }
    public ScrollAwareListView(Context context,AttributeSet attrs) { this(context,attrs,android.R.attr.listViewStyle); }
    public ScrollAwareListView(Context context,AttributeSet attrs,int style) {
        super(context,attrs,style);
        scrolling=new InnerListScrolling(context);
    }
    @Override public boolean dispatchTouchEvent(MotionEvent event) {
        boolean handled=super.dispatchTouchEvent(event);
        scrolling.afterTouch(this,event);
        return handled;
    }
}
