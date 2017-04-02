package com.example.moodly.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.moodly.Adapters.MoodAdapter;
import com.example.moodly.Controllers.CommentController;
import com.example.moodly.Controllers.MoodController;
import com.example.moodly.Models.Mood;
import com.example.moodly.R;

import java.util.ArrayList;

import static java.lang.String.valueOf;

/**
 * Created by jkc1 on 2017-03-05.
 */

/**
 * This class is a fragment to display moods from the mood history
 */
public class TabHistory extends TabBase {

    private MoodAdapter adapter;

    /**
     * Gets the current user's mood history from ElasticSearch
     * and sets the views and listeners to update when a change
     * has occured.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return rootView
     * @see #refreshOnline(ArrayList)
     * @see #setViews(LayoutInflater, ViewGroup)
     * @see #setListeners()
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        currentUser = userController.getCurrentUser();
        userList = new ArrayList<>();
        userList.add(currentUser.getName());
        // tries to get moods from elastic search server
        refreshOnline(userList);
        setViews(inflater, container);
        setListeners();

        return rootView;
    }

    /**
     * Sets listeners for the activity
     */
    @Override
    protected void setListeners() {
        // Taken from http://www.learn-android-easily.com/2013/01/adding-check-boxes-in-dialog.html 3/26/2017
        final CharSequence[] filter_choices = {"Most Recent Week","Emotional State","Text"};
        final ArrayList<String> selected_filter = new ArrayList<>();
        displayMoodList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                // why do we need this?
                final Mood mood = moodList.get(position);
                AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                adb.setMessage("Selecting mood to");
                adb.setCancelable(true);
                adb.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MoodController.getInstance().deleteMood(position);
                        refreshOffline();

                    }
                });
                adb.setNegativeButton("View/Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), ViewMood.class);
                        // 2 means edit
                        intent.putExtra("MOOD_POSITION", position);
                        MoodController.getInstance().setMood(mood);
                        startActivityForResult(intent, 0);
                    }
                });
                adb.show();
                return false;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MoodController.getInstance().setMood(new Mood());
                Intent intent = new Intent(getActivity(), ViewMood.class);
                // 1 means add
                intent.putExtra("MOOD_POSITION", -1);
                startActivityForResult(intent, 0);

            }
        });

        FloatingActionButton filter = (FloatingActionButton) rootView.findViewById(R.id.filterButton);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Select a filter");
                builder.setMultiChoiceItems(filter_choices, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked){
                            selected_filter.add(filter_choices[which].toString());
                            System.out.println(filter_choices[which].toString());
                        }
                        else if(selected_filter.contains(filter_choices[which].toString())){
                            selected_filter.remove(filter_choices[which].toString());
                        }
                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (selected_filter.size() > 1){
                            Toast.makeText(getContext(),"Only one filter can be chosen at a time!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                        else {
                            if (selected_filter.contains("Most Recent Week")) {
                                Toast.makeText(getContext(), "Most Recent Week", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            } else if (selected_filter.contains("Emotional State")) {
                                //Toast.makeText(getContext(),"Emotional State",Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                getFilterEmotion();
                            } else if (selected_filter.contains("Text")) {
                                //Toast.makeText(getContext(),"Text",Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                getFilterText();
                            } else {
                                Toast.makeText(getContext(), "No filter selected!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        selected_filter.clear();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog = builder.create();
                dialog.show();
            }
        });

    }

    /**
     * On the result of adding or editing moods, refreshes the mood list
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        refreshOffline();
    }

    /**
     * Sets the views for the activity
     * @param inflater the layout inflater
     * @param container the view group
     */
    @Override
    protected void setViews(LayoutInflater inflater, ViewGroup container) {
        rootView = inflater.inflate(R.layout.mood_history, container, false);
        displayMoodList = (ListView) rootView.findViewById(R.id.display_mood_list);
        adapter = new MoodAdapter(getActivity(), R.layout.mood_list_item, moodList);
        displayMoodList.setAdapter(adapter);

    }

    /**
     * Gets the latest mood list from the controller and refreshes adapters
     */
    @Override
    protected void refreshOffline() {
        moodList = MoodController.getInstance().getHistoryMoods();
        adapter = new MoodAdapter(getActivity(), R.layout.mood_list_item, moodList);
        displayMoodList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    protected void getFilterEmotion(){
        final CharSequence[] emotion = {"Anger","Confusion","Disgust","Fear","Happiness","Sadness","Shame","Surprise"};
        final ArrayList<String> selected_emotion = new ArrayList<>();
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select a filter");
        builder.setMultiChoiceItems(emotion, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked){
                    selected_emotion.add(emotion[which].toString());
                }
                else if(selected_emotion.contains(emotion[which].toString())){
                    selected_emotion.remove(emotion[which].toString());
                }
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (selected_emotion.size() > 1){
                    Toast.makeText(getContext(),"Only one emotion can be chosen at a time!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                else {
                    Toast.makeText(getContext(), selected_emotion.get(0), Toast.LENGTH_SHORT).show();
                }
                selected_emotion.clear();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    protected void getFilterText(){
        AlertDialog.Builder textBuilder = new AlertDialog.Builder(getContext());
        textBuilder.setTitle("Search for text");
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        textBuilder.setView(input);
        textBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String filterText = input.getText().toString();
                Toast.makeText(getContext(), filterText, Toast.LENGTH_SHORT).show();
            }
        });
        textBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        textBuilder.show();
    }
}



