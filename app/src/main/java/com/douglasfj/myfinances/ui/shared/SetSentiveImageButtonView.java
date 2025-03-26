package com.douglasfj.myfinances.ui.shared;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.lifecycle.Observer;

import com.douglasfj.myfinances.R;
import com.douglasfj.myfinances.ui.GlobalState;

public class SetSentiveImageButtonView extends AppCompatImageButton {


    public SetSentiveImageButtonView(@NonNull Context context) {
        super(context);
        init();
    }

    public SetSentiveImageButtonView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SetSentiveImageButtonView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.setImageResource(getResourceIDByState(GlobalState.IS_VALUES_VISIBLE.getValue()));
        setOnClickListener(view -> {
            boolean state = !GlobalState.IS_VALUES_VISIBLE.getValue();
            GlobalState.IS_VALUES_VISIBLE.setValue(state);

            this.setImageResource(getResourceIDByState(state));
        });
    }



    private @DrawableRes int getResourceIDByState(boolean state) {
        if (state) return R.drawable.eye;
        else return R.drawable.closedeye;
    }
}
