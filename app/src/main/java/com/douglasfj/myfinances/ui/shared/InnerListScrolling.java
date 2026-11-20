package com.douglasfj.myfinances.ui.shared;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/** Lets an inner list scroll first, handing the drag to its parent at either edge. */
final class InnerListScrolling {
    private final int slop;
    private float lastY;
    private boolean dragging;

    InnerListScrolling(android.content.Context context) {
        slop=ViewConfiguration.get(context).getScaledTouchSlop();
    }

    // Run after the list handles the event: AbsListView can also change interception.
    void afterTouch(View view,MotionEvent event) {
        switch(event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastY=event.getY(); dragging=false;
                view.getParent().requestDisallowInterceptTouchEvent(
                        view.canScrollVertically(-1) || view.canScrollVertically(1));
                break;
            case MotionEvent.ACTION_MOVE:
                float distance=lastY-event.getY();
                if(!dragging && Math.abs(distance)>slop) dragging=true;
                if(dragging && distance!=0) {
                    view.getParent().requestDisallowInterceptTouchEvent(
                            view.canScrollVertically(distance>0 ? 1 : -1));
                    lastY=event.getY();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                dragging=false;
                view.getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
    }
}
