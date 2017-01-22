/*
 *
 * Created by HDev (Antonio Siciliani, Cosimo Mollica, Daniele Monaca, Domenico Ferraro, Marco De Caria)
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 17/01/17 13.56
 */

package com.hdevteam.impiccato;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.leaderboard.Leaderboard;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardScoreBuffer;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient googleApiClient;
    private final short MAX_RESULTS = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboards);
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();
        googleApiClient.connect();
    }

    /**Scarica la classifica con l'ID passato per argomento. Bisogna richiamare questo metodo per visualizzare la classifica
     * @param leaderboardID l'ID della classifica da caricare (gli ID si trovano in strings.xml)
     * @param collection tipo di classifica(social o all). Il valore si trova nella classe LeaderboardVariant(COLLECTION_SOCIAL o COLLECTION_ALL)(è static)
     * @param timeSpan range di tempo (quotidiano, settimanale o generale). Il valore si trova nella classe LeaderboardVariant(TIME_SPAN_DAILY-WEEKLY-ALL)(è static)*/
    private void loadLeaderboard(String leaderboardID, int collection, int timeSpan){
        PendingResult<Leaderboards.LoadScoresResult> pendingScores = Games.Leaderboards.loadPlayerCenteredScores(googleApiClient, leaderboardID, timeSpan, collection, MAX_RESULTS, false);
        pendingScores.setResultCallback(new ResultCallback<Leaderboards.LoadScoresResult>(){
            @Override
            public void onResult(@NonNull Leaderboards.LoadScoresResult loadScoresResult) {
                List<LeaderboardScore> leaderboardsScores = getScores(loadScoresResult);
                showLeaderboard(leaderboardsScores);
            }
        });
    }

    /**Crea l'ArrayList contenente i risultati scaricati dai server di google*/
    private List<LeaderboardScore> getScores(Leaderboards.LoadScoresResult loadScoresResult){
        LeaderboardScoreBuffer buffer = loadScoresResult.getScores();
        List<LeaderboardScore> leaderboardScores = new ArrayList<>();
        int leaderboardScoreCount = buffer.getCount();
        for(int i=0; i < leaderboardScoreCount; i++)
            leaderboardScores.add(buffer.get(i));
        return leaderboardScores;
    }

    /**Mostra la classifica nella UI
     * @param leaderboardScores lista dei risultati della classifica*/
    private void showLeaderboard(List<LeaderboardScore> leaderboardScores){
        //TODO implementare la visualizzazione degli score
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
