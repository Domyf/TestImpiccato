/*
 *
 * Created by HDev (Antonio Siciliani, Cosimo Mollica, Daniele Monaca, Domenico Ferraro, Marco De Caria)
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 10/12/16 15.39
 */

package com.hdevteam.impiccato;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashArtActivity extends Activity {

    private ImageView cloud1;
    private ImageView cloud2;
    private ImageView cloud3;
    private ImageView cloud4;
    private ImageView hider;
    private ImageView impiccato;

    private Animation moveCloud1;
    private Animation moveCloud2;
    private Animation moveCloud3;
    private Animation moveCloud4;
    private Animation scaleImpiccato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_art);

        init();

        new Handler().postDelayed(new Runnable() {
            public void run() {
                animate();
            }
        }, 600);
    }

    private void init () {
        impiccato = (ImageView) findViewById(R.id.impiccato);
        cloud1 = (ImageView) findViewById(R.id.cloud1);
        cloud2 = (ImageView) findViewById(R.id.cloud2);
        cloud3 = (ImageView) findViewById(R.id.cloud3);
        cloud4 = (ImageView) findViewById(R.id.cloud4);
        hider = (ImageView) findViewById(R.id.hider);

        cloud1.setVisibility(View.GONE);
        cloud2.setVisibility(View.GONE);
        cloud3.setVisibility(View.GONE);
        cloud4.setVisibility(View.GONE);
        impiccato.setVisibility(View.GONE);

        moveCloud1 = AnimationUtils.loadAnimation(this, R.anim.movecloud1);
        moveCloud2 = AnimationUtils.loadAnimation(this, R.anim.movecloud2);
        moveCloud3 = AnimationUtils.loadAnimation(this, R.anim.movecloud3);
        moveCloud4 = AnimationUtils.loadAnimation(this, R.anim.movecloud4);
        scaleImpiccato = AnimationUtils.loadAnimation(this, R.anim.impiccatoanim);
    }

    private void animate() {
        cloud1.setVisibility(View.VISIBLE);
        cloud2.setVisibility(View.VISIBLE);
        cloud3.setVisibility(View.VISIBLE);
        cloud4.setVisibility(View.VISIBLE);

        cloud1.startAnimation(moveCloud1);
        cloud2.startAnimation(moveCloud2);
        cloud3.startAnimation(moveCloud3);
        cloud4.startAnimation(moveCloud4);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                hideHdtm();
                impiccato.setVisibility(ImageView.VISIBLE);
                impiccato.startAnimation(scaleImpiccato);
            }
        }, 1500);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                impiccato.clearAnimation();
                play();
            }
        }, 4000);
    }

    public void hideHdtm() {
        hider.setVisibility(View.VISIBLE);
    }

    private void play() {
        startActivity(new Intent(this, HomeActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        this.finish();
    }

    @Override
    public void onBackPressed() {

    }
}