/*
 *
 * Created by HDev (Antonio Siciliani, Cosimo Mollica, Daniele Monaca, Domenico Ferraro, Marco De Caria)
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 15/01/17 12.29
 */

package com.hdevteam.impiccato.UI;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.games.achievement.Achievement;
import com.hdevteam.impiccato.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author HDevTeam
 * */

public class AchievementsAdapter extends ArrayAdapter<Achievement> {

    private Context context;
    private ArrayList<Achievement> achievements;
    private static LayoutInflater inflater = null;
    private long start = 0;
    private Animation enter;

    public AchievementsAdapter(Context context, ArrayList<Achievement> achievements) {
        super(context, 0, achievements);
        try {
            this.context = context;
            this.achievements = achievements;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        } catch (Exception ex) {

        }
        enter = AnimationUtils.loadAnimation(context, R.anim.googleplay_button_enter);
        enter.setStartOffset(start);
    }

    public int getCount() {
        return achievements.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Classe interna contenente i View del layout
     */
    private static class ViewHolder {
        private RelativeLayout achievement;
        private TextView txtViewAchievementName;
        private TextView txtViewAchievementDescription;
        private TextView txtViewAchievementExpValue;
        private TextView txtViewAchievementUnlockDate;
        private ImageView imgViewAchievementIcon;
    }

    private void setViewHolderViews(ViewHolder viewHolder, View view) {
        viewHolder.achievement = (RelativeLayout) view.findViewById(R.id.achRow);
        viewHolder.txtViewAchievementName = (TextView) view.findViewById(R.id.txtViewAchievementName);
        viewHolder.txtViewAchievementDescription = (TextView) view.findViewById(R.id.txtViewAchievementDescription);
        viewHolder.txtViewAchievementExpValue = (TextView) view.findViewById(R.id.txtViewAchievementExpValue);
        viewHolder.txtViewAchievementUnlockDate = (TextView) view.findViewById(R.id.txtViewAchievementUnlockDate);
        viewHolder.imgViewAchievementIcon = (ImageView) view.findViewById(R.id.imgViewAchievementIcon);
    }

    private void showAchievementData(final ViewHolder holder, final Achievement achievement) {
        String name = achievement.getName();
        if(name.length() > 15)
            holder.txtViewAchievementName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        holder.txtViewAchievementName.setText(name);
        String description = achievement.getDescription();
        if(description.length() > 50)
            holder.txtViewAchievementDescription.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        holder.txtViewAchievementDescription.setText(achievement.getDescription());
        holder.txtViewAchievementExpValue.setText("" + achievement.getXpValue() + " XP");
        holder.txtViewAchievementUnlockDate.setText(getFormattedDate(achievement.getLastUpdatedTimestamp()));
        Log.i("AchievementsActivity", "img uri: " + achievement.getUnlockedImageUri().toString());
        ImageManager.create(context).loadImage(holder.imgViewAchievementIcon, achievement.getUnlockedImageUri());
        holder.achievement.startAnimation(enter);
    }
    /**Ritorna la data passata in millisecondi formattata secondo il Locale*/
    private String getFormattedDate(Long millis){
        if(millis <= 0)
            return "";
        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(new Date(millis));
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;
        try{
            if (convertView == null) {
                view = inflater.inflate(R.layout.achievement_list_layout, null);
                holder = new ViewHolder();
                setViewHolderViews(holder, view);
                view.setTag(holder);
            } else
                holder = (ViewHolder) view.getTag();
                showAchievementData(holder, achievements.get(position));
                start += 500;
                enter.setStartOffset(start);
        } catch (Exception ex){

        }
        return view;
    }
}
