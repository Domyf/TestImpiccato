package com.hdevteam.impiccato.UI;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.hdevteam.impiccato.R;

/**
 * Created by Domenico on 24/11/2016.
 */

public class HDTMDialog extends Dialog {
    private Button positive;
    private Button negative;
    private HDTMTextView title;

    public HDTMDialog(Context context) {
        super(context);
        initialize(context);
    }

    protected HDTMDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initialize(context);
    }

    public HDTMDialog(Context context, int theme) {
        super(context, theme);
        initialize(context);
    }

    private void initialize(Context c) {
        setContentView(R.layout.hdtmdialog);
        positive = (Button)findViewById(R.id.positiveBtn);
        negative = (Button)findViewById(R.id.negativeBtn);
        title = (HDTMTextView)findViewById(R.id.title);

        positive.setVisibility(View.GONE);
        negative.setVisibility(View.GONE);
    }

    public void setPositiveButton(String buttonText, View.OnClickListener listener) {
        positive.setText(buttonText);
        positive.setOnClickListener(listener);
        positive.setVisibility(View.VISIBLE);
    }

    public void setNegativeButton(String buttonText, View.OnClickListener listener) {
        negative.setText(buttonText);
        negative.setOnClickListener(listener);
        negative.setVisibility(View.VISIBLE);
    }

    public void setTitle(String titleText) {
        title.setText(titleText);
    }
}
