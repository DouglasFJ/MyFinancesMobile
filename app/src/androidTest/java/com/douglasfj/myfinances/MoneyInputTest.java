package com.douglasfj.myfinances;

import android.widget.EditText;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.douglasfj.myfinances.ui.MoneyInput;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class MoneyInputTest {
    @Test public void handlesSignsPasteReplacementAndDecimalLimit() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            EditText field = new EditText(InstrumentationRegistry.getInstrumentation().getTargetContext());
            MoneyInput.configure(field);
            field.setText("-12,34");
            field.getText().append("5");
            assertEquals("-12,34",field.getText().toString());
            field.getText().replace(1,2,"9");
            assertEquals("-92,34",field.getText().toString());
            field.getText().delete(0,1);
            assertEquals("92,34",field.getText().toString());
            field.setText("-1.234,56");
            assertEquals("-1234,56",field.getText().toString());
            field.setText("-0.01");
            assertEquals("-0.01",field.getText().toString());
            field.getText().append("-");
            assertEquals("-0.01",field.getText().toString());
        });
    }
}
