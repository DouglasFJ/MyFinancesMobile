package com.douglasfj.myfinances;

import android.content.Context;
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
public class ThemePaletteTest {
    private Context context(Context base, int mode, int theme) {
        ContextThemeWrapper context = new ContextThemeWrapper(base, theme);
        Configuration configuration = new Configuration(base.getResources().getConfiguration());
        configuration.uiMode = (configuration.uiMode & ~Configuration.UI_MODE_NIGHT_MASK) | mode;
        context.applyOverrideConfiguration(configuration);
        return context;
    }

    @Test public void allFourPalettesMirrorEveryShadeInNightMode() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                Context day = context(activity, Configuration.UI_MODE_NIGHT_NO, R.style.Theme_MyFinances);
                Context night = context(activity, Configuration.UI_MODE_NIGHT_YES, R.style.Theme_MyFinances);
                int[] shades = {50,100,200,300,400,500,600,700,800,900,950};
                for (String palette : new String[]{"amber","emerald","red","neutral"}) {
                    for (int index=0; index<shades.length; index++) {
                        int dark = night.getResources().getIdentifier(palette+"_"+shades[index],"color",activity.getPackageName());
                        int light = day.getResources().getIdentifier(palette+"_"+shades[shades.length-1-index],"color",activity.getPackageName());
                        assertNotEquals(0,dark);
                        assertNotEquals(0,light);
                        assertEquals(palette+"_"+shades[index],day.getColor(light),night.getColor(dark));
                    }
                }
                for (Context themed : new Context[]{day,night}) {
                    TypedValue actionBar = new TypedValue();
                    assertTrue(themed.getTheme().resolveAttribute(androidx.appcompat.R.attr.windowActionBar,actionBar,true));
                    assertEquals("Both modes must omit the app name action bar",0,actionBar.data);
                }
            });
        }
    }

    @Test public void entryAndAmberDialogActionsRemainReadableInBothModes() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                for (int mode : new int[]{Configuration.UI_MODE_NIGHT_NO,Configuration.UI_MODE_NIGHT_YES}) {
                    for (int theme : new int[]{R.style.ThemeOverlay_MyFinances_Entry_Red,
                            R.style.ThemeOverlay_MyFinances_Entry_Emerald,R.style.ThemeOverlay_MyFinances_Amber}) {
                        Context base = context(activity,mode,R.style.Theme_MyFinances);
                        Context scoped = new ContextThemeWrapper(base,theme);
                        AlertDialog dialog = new AlertDialog.Builder(scoped).setTitle("Lançamento")
                                .setPositiveButton("Salvar",null).setNegativeButton("Cancelar",null).create();
                        dialog.show();
                        try {
                            for (int which : new int[]{AlertDialog.BUTTON_POSITIVE,AlertDialog.BUTTON_NEGATIVE}) {
                                Button button = dialog.getButton(which);
                                TypedValue background = new TypedValue();
                                assertTrue(dialog.getContext().getTheme().resolveAttribute(android.R.attr.colorBackgroundFloating,background,true));
                                int color = background.resourceId==0 ? background.data : dialog.getContext().getColor(background.resourceId);
                                android.graphics.drawable.Drawable actualBackground = dialog.getWindow().getDecorView().getBackground();
                                assertTrue(actualBackground instanceof android.graphics.drawable.ColorDrawable);
                                assertEquals("The visible dialog background must also use the palette",color,
                                        ((android.graphics.drawable.ColorDrawable)actualBackground).getColor());
                                assertTrue("Dialog action contrast, mode="+mode+", theme="+theme,
                                        ColorUtils.calculateContrast(button.getCurrentTextColor(),color)>=4.5);
                            }
                        } finally { dialog.dismiss(); }
                    }
                }
            });
        }
    }
}
