package com.hdevteam.impiccato;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashArtActivity extends Activity {

    ImageView cloud1;
    ImageView cloud2;
    ImageView cloud3;
    ImageView cloud4;
    ImageView hider;
    ImageView impiccato;

    Animation moveCloud1;
    Animation moveCloud2;
    Animation moveCloud3;
    Animation moveCloud4;
    Animation movehider;

    boolean skip;

    /**Salta lo splashscreen*/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        startActivity(new Intent(this, HomeActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        skip = true;
        this.finish();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_art);

        skip = false;

        impiccato = (ImageView) findViewById(R.id.impiccato);
        cloud1 = (ImageView) findViewById(R.id.cloud1);
        cloud2 = (ImageView) findViewById(R.id.cloud2);
        cloud3 = (ImageView) findViewById(R.id.cloud3);
        cloud4 = (ImageView) findViewById(R.id.cloud4);
        hider = (ImageView) findViewById(R.id.hider);
        hider.setVisibility(ImageView.INVISIBLE);
        impiccato.setVisibility(ImageView.INVISIBLE);

        moveCloud1 = AnimationUtils.loadAnimation(this, R.anim.movecloud1);
        moveCloud2 = AnimationUtils.loadAnimation(this, R.anim.movecloud2);
        moveCloud3 = AnimationUtils.loadAnimation(this, R.anim.movecloud3);
        moveCloud4 = AnimationUtils.loadAnimation(this, R.anim.movecloud4);
        movehider = AnimationUtils.loadAnimation(this, R.anim.movehider);

/*        cloud1.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.cloud1, null));
        cloud2.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.cloud2, null));
        cloud3.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.cloud3, null));
        cloud4.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.cloud4, null));
        impiccato.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.logoimpiccato, null));*/

        //new LoadAnimation().execute();
        //new LoadDrawable().execute(new Integer(R.drawable.cloud1), new Integer(R.drawable.cloud2), new Integer(R.drawable.cloud3), new Integer(R.drawable.cloud4), new Integer(R.drawable.logoimpiccato));


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
                if (!skip)
                    play();
            }
        }, 4000);
    }

    private void play() {
        startActivity(new Intent(this, HomeActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        this.finish();
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
                    if (!skip)
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


//Vera Splash Art
/*public class SplashArtActivity extends Activity {

    ImageView cloud1;
    ImageView cloud2;
    ImageView cloud3;
    ImageView cloud4;
    ImageView hider;
    ImageView impiccato;

    Animation moveCloud1;
    Animation moveCloud2;
    Animation moveCloud3;
    Animation moveCloud4;
    Animation movehider;

    boolean skip;

    *//**
     * Salta lo splashscreen
     *//*
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        startActivity(new Intent(this, HomeActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        skip = true;
        this.finish();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_art);

        skip = false;

        impiccato = (ImageView) findViewById(R.id.impiccato);
        cloud1 = (ImageView) findViewById(R.id.cloud1);
        cloud2 = (ImageView) findViewById(R.id.cloud2);
        cloud3 = (ImageView) findViewById(R.id.cloud3);
        cloud4 = (ImageView) findViewById(R.id.cloud4);
        hider = (ImageView) findViewById(R.id.hider);
        hider.setVisibility(ImageView.INVISIBLE);
        impiccato.setVisibility(ImageView.INVISIBLE);

        moveCloud1 = AnimationUtils.loadAnimation(this, R.anim.movecloud1);
        moveCloud2 = AnimationUtils.loadAnimation(this, R.anim.movecloud2);
        moveCloud3 = AnimationUtils.loadAnimation(this, R.anim.movecloud3);
        moveCloud4 = AnimationUtils.loadAnimation(this, R.anim.movecloud4);
        movehider = AnimationUtils.loadAnimation(this, R.anim.movehider);

*//*        cloud1.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.cloud1, null));
        cloud2.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.cloud2, null));
        cloud3.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.cloud3, null));
        cloud4.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.cloud4, null));
        impiccato.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.logoimpiccato, null));*//*

        //new LoadAnimation().execute();
        //new LoadDrawable().execute(new Integer(R.drawable.cloud1), new Integer(R.drawable.cloud2), new Integer(R.drawable.cloud3), new Integer(R.drawable.cloud4), new Integer(R.drawable.logoimpiccato));


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
                if (!skip)
                    play();
            }
        }, 4000);
    }

    private void play() {
        startActivity(new Intent(this, HomeActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        this.finish();
    }
}*/
