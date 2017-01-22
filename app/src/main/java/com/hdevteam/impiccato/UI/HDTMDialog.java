package com.hdevteam.impiccato.UI;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;

import com.hdevteam.impiccato.R;

/**
 *  @author HDevTeam
 */

/** La classe estende dialog e permettere di fare un custom dialog definito in hdtmdialog.xml */
public class HDTMDialog extends Dialog {

    private HDTMTextView title;     //Titolo del dialog. Se viene impostato diventa visibile altrimenti è GONE
    private HDTMTextView message;   //Messaggio del dialog. Se viene impostato diventa visibile altrimenti è GONE
    private HDTMButton positiveBtn; //Bottone risposta positiva. Se viene impostato diventa visibile altrimenti è GONE
    private HDTMButton negativeBtn; //Bottone risposta negativa. Se viene impostato diventa visibile altrimenti è GONE
    private HDTMButton neutralBtn;  //Bottone risposta neutrale. Se viene impostato diventa visibile altrimenti è GONE
    private boolean rateUsDialog;

    public HDTMDialog(Context context) {
        super(context, R.style.ImpiccatoTheme_Dialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //Imposta nessun titolo. Verrà usato quello custom.
        setContentView(R.layout.hdtmdialog);
        rateUsDialog = false;
        initLayout();
    }

    public HDTMDialog(Context context, int themeResId) {
        super(context, themeResId);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //Imposta nessun titolo. Verrà usato quello custom.
        setContentView(R.layout.hdtmdialog);
        rateUsDialog = false;
        initLayout();
    }

    public HDTMDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //Imposta nessun titolo. Verrà usato quello custom.
        setContentView(R.layout.hdtmdialog);
        rateUsDialog = false;
        initLayout();
    }

    /** Inizializza il layout. Rende i bottoni, il titolo ed il messaggio non visibile. Diventeranno visibili se singolarmente impostati */
    public void initLayout() {
        title = (HDTMTextView) findViewById(R.id.upperBackground);
        message = (HDTMTextView) findViewById(R.id.message);
        positiveBtn = (HDTMButton) findViewById(R.id.btnShowLeaderboards);
        negativeBtn = (HDTMButton) findViewById(R.id.negativeBtn);
        neutralBtn = (HDTMButton) findViewById(R.id.neutralBtn);
        title.setVisibility(View.GONE);
        message.setVisibility(View.GONE);
        positiveBtn.setVisibility(View.GONE);
        negativeBtn.setVisibility(View.GONE);
        neutralBtn.setVisibility(View.GONE);
    }

    /** Rende il titolo visibile e ne scrive il testo.
     *  Non è possibile eseguire l'override del metodo setText(CharSequence) della classe Dialog.
     *  Per questo motivo questo metodo riceve una stringa e verrà chiamato soltanto se viene passata
     *  una stringa.
     * */
    public void setTitle(String titleText) {
        title.setVisibility(View.VISIBLE);
        title.setText(titleText);
    }

    /** Rende visibile il bottone positivo e ne scrive il testo. Imposta il listener*/
    public void setPositiveBtn(String btnText, View.OnClickListener listener) {
        if (!rateUsDialog)
            neutralBtn.setVisibility(View.GONE);
        positiveBtn.setVisibility(View.VISIBLE);
        positiveBtn.setText(btnText);
        positiveBtn.setOnClickListener(listener);
    }

    /** Rende visibile il bottone negativo e ne scrive il testo. Imposta il listener*/
    public void setNegativeBtn(String btnText, View.OnClickListener listener) {
        if (!rateUsDialog)
            neutralBtn.setVisibility(View.GONE);
        negativeBtn.setVisibility(View.VISIBLE);
        negativeBtn.setText(btnText);
        negativeBtn.setOnClickListener(listener);
    }

    /** Rende visibile il bottone neutrale e ne scrive il testo. Imposta il listener*/
    public void setNeutralBtn(String btnText, View.OnClickListener listener) {
        if (!rateUsDialog) {
            positiveBtn.setVisibility(View.GONE);
            negativeBtn.setVisibility(View.GONE);
        }
        neutralBtn.setVisibility(View.VISIBLE);
        neutralBtn.setText(btnText);
        neutralBtn.setOnClickListener(listener);
    }

    /** Rende visibile il messaggio e ne scrive il testo*/
    public void setMessage(String message) {
        this.message.setVisibility(View.VISIBLE);
        this.message.setText(message);
    }

    /** Chiude il dialog */
    public void close() {
        dismiss();
    }

    public void isRateUs(boolean rateUs) {
        rateUsDialog = rateUs;
    }
}
