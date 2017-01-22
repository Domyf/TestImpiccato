package com.hdevteam.impiccato.UI;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
@author HDevTeam
Classe per la TextView personalizzata con il font HDevTeam
 */
public class HDTMTextView extends TextView {
    public HDTMTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "HDevTMPencil.ttf"));
        //Compatibilità con le api che non hanno textAllCaps
        String txt = (String) getText();
        if (!txt.isEmpty())
            setText(txt.toUpperCase());
    }

    public void setText(String text) {
        this.setText((CharSequence)text.toUpperCase());
    }
}
