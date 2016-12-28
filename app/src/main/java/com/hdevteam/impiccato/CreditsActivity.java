/*
 *
 * Created by HDev (Antonio Siciliani, Cosimo Mollica, Daniele Monaca, Domenico Ferraro, Marco De Caria)
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 10/12/16 15.39
 */

package com.hdevteam.impiccato;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author HDevTeam
 * Attività che mostra i crediti
 */

public class CreditsActivity extends AppCompatActivity {

    private Animation developedBy;
    private Animation hdevpulse;
    private Animation logoanim;

    /*private Animation barAntonio;
    private Animation barCosimo;
    private Animation barDomenico;
    private Animation barMarco;
    private Animation barDaniele;*/

    private ImageView cloud1;
    private ImageView cloud2;
    private ImageView cloud3;
    private ImageView cloud4;
    private Animation moveCloud1;
    private Animation moveCloud2;
    private Animation moveCloud3;
    private Animation moveCloud4;

    private TextView hdev;
    private TextView txtDevelopedBy;
    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        hdev = (TextView) findViewById(R.id.hdev);
        txtDevelopedBy = (TextView) findViewById(R.id.developedBy);
        logo = (ImageView) findViewById(R.id.logo);
        cloud1 = (ImageView) findViewById(R.id.cloud1);
        cloud2 = (ImageView) findViewById(R.id.cloud2);
        cloud3 = (ImageView) findViewById(R.id.cloud3);
        cloud4 = (ImageView) findViewById(R.id.cloud4);

        developedBy = AnimationUtils.loadAnimation(this, R.anim.developed_by);
        hdevpulse = AnimationUtils.loadAnimation(this, R.anim.hdev_pulse);
        logoanim = AnimationUtils.loadAnimation(this, R.anim.anim_logo_credits);
        moveCloud1 = AnimationUtils.loadAnimation(this, R.anim.credits_movecloud1);
        moveCloud2 = AnimationUtils.loadAnimation(this, R.anim.credits_movecloud2);
        moveCloud3 = AnimationUtils.loadAnimation(this, R.anim.credits_movecloud3);
        moveCloud4 = AnimationUtils.loadAnimation(this, R.anim.credits_movecloud4);

        /*barAntonio = AnimationUtils.loadAnimation(this, R.anim.credits_bar_antonio);
        barCosimo = AnimationUtils.loadAnimation(this, R.anim.credits_bar_cosimo);
        barDomenico = AnimationUtils.loadAnimation(this, R.anim.credits_bar_domenico);
        barMarco = AnimationUtils.loadAnimation(this, R.anim.credits_bar_marco);
        barDaniele = AnimationUtils.loadAnimation(this, R.anim.credits_bar_daniele);*/

        logo.setVisibility(View.GONE);
        hdev.setVisibility(View.GONE);
        cloud1.setVisibility(View.GONE);
        cloud2.setVisibility(View.GONE);
        cloud3.setVisibility(View.GONE);
        cloud4.setVisibility(View.GONE);

        animateHdev();
        animateDevelopedBy();
    }

    /*public void animateBar() {
        animateAntonio();
        animateCosimo();
        animateDomenico();
        animateMarco();
        animateDaniele();
    }

    public void animateAntonio() {
        findViewById(R.id.antonio).startAnimation(barAntonio);
        barAntonio.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                findViewById(R.id.daniele).setVisibility(View.GONE);
                findViewById(R.id.antonio).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                findViewById(R.id.antonio).setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void animateCosimo() {
        findViewById(R.id.cosimo).startAnimation(barCosimo);
        barCosimo.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                findViewById(R.id.cosimo).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                findViewById(R.id.cosimo).setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void animateDomenico() {
        findViewById(R.id.domenico).startAnimation(barDomenico);
        barDomenico.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                findViewById(R.id.domenico).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                findViewById(R.id.domenico).setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void animateMarco() {
        findViewById(R.id.marco).startAnimation(barMarco);
        barMarco.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                findViewById(R.id.marco).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                findViewById(R.id.marco).setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void animateDaniele() {
        findViewById(R.id.daniele).startAnimation(barDaniele);
        barDaniele.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                findViewById(R.id.daniele).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                findViewById(R.id.daniele).setVisibility(View.GONE);
                animateBar();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }*/

    public void animateDevelopedBy() {
        txtDevelopedBy.startAnimation(developedBy);
        developedBy.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                txtDevelopedBy.setVisibility(View.GONE);
                findViewById(R.id.btnDonate).setVisibility(View.VISIBLE);
                findViewById(R.id.btnDonate).startAnimation(AnimationUtils.loadAnimation(CreditsActivity.this, R.anim.fade_in));
                animateClouds();
                animateLogo();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void animateLogo() {
        logo.startAnimation(logoanim);
        logoanim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void animateHdev() {
        hdev.setVisibility(View.VISIBLE);
        hdev.startAnimation(hdevpulse);
        hdevpulse.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void animateClouds() {
        cloud1.setVisibility(View.VISIBLE);
        cloud2.setVisibility(View.VISIBLE);
        cloud3.setVisibility(View.VISIBLE);
        cloud4.setVisibility(View.VISIBLE);

        cloud1.startAnimation(moveCloud1);
        cloud2.startAnimation(moveCloud2);
        cloud3.startAnimation(moveCloud3);
        cloud4.startAnimation(moveCloud4);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, HomeActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        this.finish();
    }

    public void btnDonateOnClick(View view) {
        ((android.os.Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(25);
        startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(getResources().getString(R.string.donation_url))));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}