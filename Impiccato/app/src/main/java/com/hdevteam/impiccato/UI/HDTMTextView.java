package com.hdevteam.impiccato.UI;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
@author HDevTeam
Classe per la TextView personalizzata con il font HDevTeam
 */
public class HDTMTextView extends TextView {
    public HDTMTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "HDevTMPencil.ttf"));
    }
}
