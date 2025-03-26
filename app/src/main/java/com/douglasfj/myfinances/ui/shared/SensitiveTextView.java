package com.douglasfj.myfinances.ui.shared;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.LifecycleOwner;

import com.douglasfj.myfinances.ui.GlobalState;

public class SensitiveTextView extends AppCompatTextView {
    private boolean isSensitiveVisible = true;
    private Paint linePaint;

    public SensitiveTextView(Context context) {
        super(context);
        init();
    }

    public SensitiveTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SensitiveTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //get color
        linePaint = new Paint();
        linePaint.setStrokeWidth(4f);
        linePaint.setAntiAlias(true);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        GlobalState.IS_VALUES_VISIBLE.observeForever(this::setSensitiveVisible);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        GlobalState.IS_VALUES_VISIBLE.removeObserver(this::setSensitiveVisible);
    }

    public void setSensitiveVisible(boolean isVisible) {
        this.isSensitiveVisible = isVisible;
        invalidate();
    }

    public boolean isSensitiveVisible() {
        return isSensitiveVisible;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isSensitiveVisible) {
            super.onDraw(canvas);
        } else {
            // Draw a horizontal line instead of the text
            linePaint.setColor(getCurrentTextColor());
            float centerY = getHeight() / 2f;
            canvas.drawLine(0, centerY, getWidth(), centerY, linePaint);
        }
    }
}
