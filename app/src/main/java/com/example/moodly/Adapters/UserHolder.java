package com.example.moodly.Adapters;

import android.widget.CheckBox;

import com.example.moodly.Models.Mood;
import com.example.moodly.Models.User;

import java.util.ArrayList;

/**
 * Created by mliew on 2017-02-25.
 */

public class UserHolder {
    User user;

    // Figure out how to use for adapter?
    CheckBox checkBox;
    ArrayList<Mood> moodList = new ArrayList<>();
}
