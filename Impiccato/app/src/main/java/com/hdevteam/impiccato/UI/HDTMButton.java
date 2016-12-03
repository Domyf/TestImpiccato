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
    }

}
