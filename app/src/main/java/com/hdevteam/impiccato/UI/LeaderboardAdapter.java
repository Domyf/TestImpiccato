/*
 *
 * Created by HDev (Antonio Siciliani, Cosimo Mollica, Daniele Monaca, Domenico Ferraro, Marco De Caria)
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 15/01/17 21.27
 */

package com.hdevteam.impiccato.UI;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.games.leaderboard.LeaderboardScore;


import java.util.ArrayList;

/**
 * @author HDevTeam
 */

public class LeaderboardAdapter extends ArrayAdapter<LeaderboardScore> {

    private Context context;
    private ArrayList<LeaderboardScore> leaderboardScores;
    private static LayoutInflater inflater = null;

    public LeaderboardAdapter(Context context, ArrayList<LeaderboardScore> leaderboardScores){
        super(context, 0,leaderboardScores);
        try {
            this.context = context;
            this.leaderboardScores = leaderboardScores;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        } catch (Exception ex) {

        }
    }

    public int getCount() {
        return leaderboardScores.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**Ritorna la view da visualizzare come riga della ListView*/
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        return view;
    }

    /**Classe che contiene le view dell'adapter*/
    private static class ViewHolder{
        private TextView txtViewScore;
        private TextView txtViewPlayerName;
        private ImageView imageViewPlayerPicture;
    }

}
