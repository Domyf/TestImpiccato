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
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
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
    private Animation movehider;
    private Animation scaleImpiccato;
    private Animation hdeventer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_art);
        init();

        //findViewById(R.id.hdtm).startAnimation(hdeventer);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                animate();
            }
        }, 600);


/*        cloud1.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.cloud1, null));
        cloud2.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.cloud2, null));
        cloud3.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.cloud3, null));
        cloud4.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.cloud4, null));
        impiccato.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.logoimpiccato, null));*/

        //new LoadAnimation().execute();
        //new LoadDrawable().execute(new Integer(R.drawable.cloud1), new Integer(R.drawable.cloud2), new Integer(R.drawable.cloud3), new Integer(R.drawable.cloud4), new Integer(R.drawable.logoimpiccato));

    }

    private void init () {

        impiccato = (ImageView) findViewById(R.id.impiccato);
        cloud1 = (ImageView) findViewById(R.id.cloud1);
        cloud2 = (ImageView) findViewById(R.id.cloud2);
        cloud3 = (ImageView) findViewById(R.id.cloud3);
        cloud4 = (ImageView) findViewById(R.id.cloud4);
        hider = (ImageView) findViewById(R.id.hider);

        //hider.setVisibility(ImageView.GONE);
        cloud1.setVisibility(View.GONE);
        cloud2.setVisibility(View.GONE);
        cloud3.setVisibility(View.GONE);
        cloud4.setVisibility(View.GONE);
        impiccato.setVisibility(View.GONE);

        moveCloud1 = AnimationUtils.loadAnimation(this, R.anim.movecloud1);
        moveCloud2 = AnimationUtils.loadAnimation(this, R.anim.movecloud2);
        moveCloud3 = AnimationUtils.loadAnimation(this, R.anim.movecloud3);
        moveCloud4 = AnimationUtils.loadAnimation(this, R.anim.movecloud4);
        movehider = AnimationUtils.loadAnimation(this, R.anim.movehider);
        scaleImpiccato = AnimationUtils.loadAnimation(this, R.anim.impiccatoanim);
        hdeventer = AnimationUtils.loadAnimation(this, R.anim.hdev_enter);
    }

    private void animate() {
        cloud1.setVisibility(View.VISIBLE);
        cloud2.setVisibility(View.VISIBLE);
        cloud3.setVisibility(View.VISIBLE);
        cloud4.setVisibility(View.VISIBLE);
        //hider.setVisibility(View.VISIBLE);

        cloud1.startAnimation(moveCloud1);
        cloud2.startAnimation(moveCloud2);
        cloud3.startAnimation(moveCloud3);
        cloud4.startAnimation(moveCloud4);
        //hider.startAnimation(movehider);

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

    //Pare funzioni
    private class LoadDrawable extends AsyncTask<Integer, Void, Drawable[]> {
        @Override
        protected Drawable[] doInBackground(Integer... ids) {
            //Loading the drawable in the background
            Drawable[] loadedDrawables = new Drawable[ids.length];
            for (int i=0; i<ids.length; i++)
                loadedDrawables[i] = ResourcesCompat.getDrawable(getResources(), ids[i], null);
            //After the drawable is loaded, onPostExecute is called
            Log.i("doInBackground|Splash", "Ho caricato in background i drawables");

            return loadedDrawables;
        }

        @Override
        protected void onPostExecute(Drawable[] loadedDrawables) {
            //cloud1.setImageDrawable(loadedClouds);
            //cloud1.startAnimation(moveCloud1);
            //for (int i=0; i<loadedClouds.length; i++)
            //    Log.i("onPostExecute", "Fatto! "+loadedClouds[i]);
            hider.setVisibility(ImageView.VISIBLE);
            cloud1.setImageDrawable(loadedDrawables[0]);
            cloud2.setImageDrawable(loadedDrawables[1]);
            cloud3.setImageDrawable(loadedDrawables[2]);
            cloud4.setImageDrawable(loadedDrawables[3]);
            impiccato.setImageDrawable(loadedDrawables[4]);
            cloud1.startAnimation(moveCloud1);
            cloud2.startAnimation(moveCloud2);
            cloud3.startAnimation(moveCloud3);
            cloud4.startAnimation(moveCloud4);
            hider.startAnimation(movehider);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    impiccato.setVisibility(ImageView.VISIBLE);
                    impiccato.startAnimation(AnimationUtils.loadAnimation(SplashArtActivity.this, R.anim.impiccatoanim));
                }
            }, 1500);

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    play();
                }
            }, 4000);
        }

        @Override
        protected void onPreExecute() {
            impiccato = (ImageView) findViewById(R.id.impiccato);
            cloud1 = (ImageView) findViewById(R.id.cloud1);
            cloud2 = (ImageView) findViewById(R.id.cloud2);
            cloud3 = (ImageView) findViewById(R.id.cloud3);
            cloud4 = (ImageView) findViewById(R.id.cloud4);
            hider = (ImageView) findViewById(R.id.hider);
            hider.setVisibility(ImageView.INVISIBLE);
            impiccato.setVisibility(ImageView.INVISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }


    private class LoadAnimation extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... values) {
            /*//Loading the drawable in the background
            Drawable[] loadedDrawables = new Drawable[ids.length];
            for (int i=0; i<ids.length; i++)
                loadedDrawables[i] = ResourcesCompat.getDrawable(getResources(), ids[i], null);
            //After the drawable is loaded, onPostExecute is called
            Log.i("doInBackground", "Ho caricato in background");

            return loadedDrawables;*/
            moveCloud1 = AnimationUtils.loadAnimation(SplashArtActivity.this, R.anim.movecloud1);
            moveCloud2 = AnimationUtils.loadAnimation(SplashArtActivity.this, R.anim.movecloud2);
            moveCloud3 = AnimationUtils.loadAnimation(SplashArtActivity.this, R.anim.movecloud3);
            moveCloud4 = AnimationUtils.loadAnimation(SplashArtActivity.this, R.anim.movecloud4);
            movehider = AnimationUtils.loadAnimation(SplashArtActivity.this, R.anim.movehider);
            return 0;
        }

        @Override
        protected void onPostExecute(Integer value) {
            new LoadDrawable().execute(new Integer(R.drawable.cloud1), new Integer(R.drawable.cloud2), new Integer(R.drawable.cloud3), new Integer(R.drawable.cloud4), new Integer(R.drawable.logoimpiccato));
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}