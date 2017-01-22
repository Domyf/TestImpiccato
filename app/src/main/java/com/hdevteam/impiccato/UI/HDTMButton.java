package com.hdevteam.impiccato.UI;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Button;


/**
 * @author HDevTeam
Classe per il Button personalizzato con il font HDevTeam
 */
public class HDTMButton extends Button {

    private int paddingBottom;

    public HDTMButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(Typeface.createFromAsset(context.getAssets(), "HDevTMPencil.ttf"));
        paddingBottom = getPaddingBottom();

        //Compatibilità con le api che non hanno textAllCaps
        String txt = (String) getText();
        if (!txt.isEmpty())
            setText(txt.toUpperCase());
    }

    public void setText(String text) {
        this.setText((CharSequence)text.toUpperCase());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isPressed())
            setPadding(0, 0, 0, paddingBottom);
        if (event.getAction() == MotionEvent.ACTION_DOWN)
            setPadding(0, 0, 0, 0);
        if (event.getAction() == MotionEvent.ACTION_UP)
            setPadding(0, 0, 0, paddingBottom);
        return super.onTouchEvent(event);
    }
}
