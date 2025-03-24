package com.douglasfj.myfinances.ui.shared;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

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
        linePaint.setColor(getCurrentTextColor());
        linePaint.setStrokeWidth(4f);
        linePaint.setAntiAlias(true);
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
            float centerY = getHeight() / 2f;
            canvas.drawLine(0, centerY, getWidth(), centerY, linePaint);
        }
    }
}
