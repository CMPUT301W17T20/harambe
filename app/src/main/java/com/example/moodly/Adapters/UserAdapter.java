package com.example.moodly.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moodly.Adapters.MoodHolder;
import com.example.moodly.Models.User;
import com.example.moodly.R;

import java.util.ArrayList;

/**
 * Created by mliew on 2017-02-25.
 */

public class UserAdapter extends ArrayAdapter<User> {

    private ArrayList<User> following = new ArrayList<>();
    private ArrayList<User> follower = new ArrayList<>();

    private String username;

    private ArrayList<User> items;
    private int layoutResourceId;
    private UserHolder holder;
    private Context context;

    /**
     * Constructor for our UserAdapter
     * @param context
     * @param layoutResourceId resource id for our single list item
     * @param items ArrayList of moods
     */
    public UserAdapter(Context context, int layoutResourceId, ArrayList<User> items) {
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

        holder = new UserHolder();
        holder.user = items.get(position);

        setupItem(holder);

        return row;
    }

    /**
     * Sets up the XML elements in our MoodHolder
     * @param holder
     */
    private void setupItem(UserHolder holder) {
        ///////////TODO what to show here???
    }
}
