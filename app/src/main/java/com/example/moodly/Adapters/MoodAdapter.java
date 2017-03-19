package com.example.moodly.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moodly.Models.Mood;
import com.example.moodly.R;

import java.util.ArrayList;

/**
 * Created by mliew on 2017-02-25.
 */
/*
* check jkc1-SizeBook's RecordListAdapter
* for references
*
* */

/**
 * Custom adapter to adapt moods onto a listview
 */
public class MoodAdapter extends ArrayAdapter<Mood> {

    private ArrayList<Mood> items;
    private int layoutResourceId;
    private MoodHolder holder;
    private Context context;

    /**
     * Constructor for our MoodAdapter
     * @param context
     * @param layoutResourceId resource id for our single list item
     * @param items ArrayList of moods
     */
    public MoodAdapter(Context context, int layoutResourceId, ArrayList<Mood> items) {
        super(context, layoutResourceId, items);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.items = items;
    }

    /**
     * Gets the view in which to setup the row of our custom list
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        MoodHolder holder = new MoodHolder();
        holder.mood = items.get(position);

        holder.emotion = (TextView) row.findViewById(R.id.mood_emotion);
        holder.date = (TextView) row.findViewById(R.id.mood_date);
        holder.emoji = (ImageView) row.findViewById(R.id.emoji);

        setupItem(holder);

        return row;
    }

    /**
     * Sets up the XML elements in our MoodHolder
     * @param holder
     */
    private void setupItem(MoodHolder holder) {
        String emotionString = toStringEmotion(holder.mood.getEmotion());
        holder.emotion.setText(emotionString);
        holder.date.setText(holder.mood.getDate().toString());
        emotionToEmoji(holder.emoji, holder.mood.getEmotion());
    }

    /**
     * From our emotion enum, draw emoji to imageview
     * @param emotion an enum of emotions
     * @param emoji is the imageview that holds the emoji
     * @return set emoji to a drawable
     */

    private void emotionToEmoji (ImageView emoji, int emotion){
        switch (emotion) {
            case 1:
                emoji.setImageResource(R.drawable.angry);
                break;
            case 2:
                emoji.setImageResource(R.drawable.confused);
                break;
            case 3:
                emoji.setImageResource(R.drawable.disgust);
                break;
            case 4:
                emoji.setImageResource(R.drawable.afraid);
                break;
            case 5:
                emoji.setImageResource(R.drawable.happy);
                break;
            case 6:
                emoji.setImageResource(R.drawable.sad);
                break;
            case 7:
                emoji.setImageResource(R.drawable.shame);
                break;
            case 8:
                emoji.setImageResource(R.drawable.surprise);
                break;
            default:
                break;
        }
    }

    /**
     * From the emotion enum, return the string representation of it
     * @param emotion
     * @return
     */
    private String toStringEmotion(int emotion) {
        switch (emotion) {
            case 1:
                return "Anger";
            case 2:
                return "Confusion";
            case 3:
                return "Disgust";
            case 4:
                return "Fear";
            case 5:
                return "Happiness";
            case 6:
                return "Sadness";
            case 7:
                return "Shame";
            case 8:
                return "Surprise";
            default:
                return "None";
        }
    }


}