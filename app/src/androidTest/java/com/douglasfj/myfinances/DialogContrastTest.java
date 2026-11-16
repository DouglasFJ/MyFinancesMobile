package com.douglasfj.myfinances;

import android.content.res.Configuration;
import android.util.TypedValue;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.graphics.ColorUtils;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.douglasfj.myfinances.ui.MainActivity;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class DialogContrastTest {
    @Test public void dialogActionsHaveReadableContrastInLightAndDarkThemes() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                for (int mode : new int[]{Configuration.UI_MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_YES}) {
                    ContextThemeWrapper context = new ContextThemeWrapper(activity, R.style.Theme_MyFinances);
                    Configuration config = new Configuration(activity.getResources().getConfiguration());
                    config.uiMode = (config.uiMode & ~Configuration.UI_MODE_NIGHT_MASK) | mode;
                    context.applyOverrideConfiguration(config);
                    AlertDialog dialog = new AlertDialog.Builder(context).setTitle("Confirmar")
                            .setMessage("Verificação de legibilidade")
                            .setPositiveButton("Selecionar", null).setNegativeButton("Cancelar", null)
                            .setNeutralButton("Fechar", null).create();
                    dialog.show();
                    try {
                        for (int which : new int[]{AlertDialog.BUTTON_POSITIVE, AlertDialog.BUTTON_NEGATIVE, AlertDialog.BUTTON_NEUTRAL}) {
                            Button button = dialog.getButton(which);
                            TypedValue background = new TypedValue();
                            assertTrue(button.getContext().getTheme().resolveAttribute(android.R.attr.colorBackgroundFloating, background, true));
                            int color = background.resourceId == 0 ? background.data : button.getContext().getColor(background.resourceId);
                            assertTrue("Texto do botão precisa de contraste 4,5:1 no modo " + mode +
                                            "; texto=" + Integer.toHexString(button.getCurrentTextColor()) + "; fundo=" + Integer.toHexString(color),
                                    ColorUtils.calculateContrast(button.getCurrentTextColor(), color) >= 4.5);
                        }
                    } finally { dialog.dismiss(); }
                }
            });
        }
    }
}
