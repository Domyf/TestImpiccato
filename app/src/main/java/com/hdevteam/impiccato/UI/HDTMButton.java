package com.hdevteam.impiccato.UI;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;


/**
 * @author HDevTeam
Classe per il Button personalizzato con il font HDevTeam
 */
public class HDTMButton extends Button {

    public HDTMButton(Context context, AttributeSet attrs) {
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
