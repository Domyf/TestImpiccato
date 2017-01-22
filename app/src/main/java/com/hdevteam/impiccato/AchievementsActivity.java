/*
 *
 * Created by HDev (Antonio Siciliani, Cosimo Mollica, Daniele Monaca, Domenico Ferraro, Marco De Caria)
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 15/01/17 13.41
 */

package com.hdevteam.impiccato;

import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Game;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.achievement.Achievement;
import com.google.android.gms.games.achievement.AchievementBuffer;
import com.google.android.gms.games.achievement.Achievements;
import com.hdevteam.impiccato.UI.AchievementsAdapter;

import java.util.ArrayList;

public class AchievementsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient googleApiClient;
    ListView listViewAchievements;
    ArrayAdapter adapter;
    ArrayList<Achievement> achievementList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);
        listViewAchievements = (ListView) findViewById(R.id.listViewAchievements);
        adapter = new ArrayAdapter(this, R.layout.achievement_list_layout, achievementList);
        listViewAchievements.setAdapter(adapter);
        buildApiClient();
        loadAchievements();
    }

    /**Builda e connette GoogleApiClient*/
    private void buildApiClient(){
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();
        googleApiClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**Scarica la lista degli achievements dai server di Google*/
    private void loadAchievements() {
        PendingResult<Achievements.LoadAchievementsResult> pendingResult = Games.Achievements.load(googleApiClient, true);
        pendingResult.setResultCallback(new ResultCallback<Achievements.LoadAchievementsResult>() {
            @Override
            public void onResult(@NonNull Achievements.LoadAchievementsResult loadAchievementsResult) {
                Game game = Games.GamesMetadata.getCurrentGame(googleApiClient);
                showAchievements(loadAchievementsResult, game);
            }
        });
    }

    /**Mostra gli achievemetns scaricati all'interno di una ListView*/
    private void showAchievements(Achievements.LoadAchievementsResult loadAchievementsResult, Game game){
        AchievementBuffer buffer = loadAchievementsResult.getAchievements();
        for (Achievement achievement : buffer) {
            achievementList.add(achievement);
            adapter.notifyDataSetChanged();
        }
    }

    private void setAchievementView() {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("GameActivity", "Connesso a Google Play Games");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("GameActivity", "Connessione sospesa... Provo a riconnettere");
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("GameActivity", "Connection failed: " + connectionResult.getErrorMessage());
        if(connectionResult.hasResolution()){
            try{
                connectionResult.startResolutionForResult(this, 1);
                return;
            }catch (IntentSender.SendIntentException ex){
                googleApiClient.connect();
                return;
            }
        }
        else
            Log.e("GameActivity", "Errore PlayGames: " + connectionResult.getErrorMessage() + "(" + connectionResult.getErrorCode() + ")");
    }

    @Override
    public void onBackPressed() {
        /*startActivity(new Intent(this, HomeActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);*/
        this.finish();
    }
}
