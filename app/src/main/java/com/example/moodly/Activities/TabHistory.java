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
import com.example.moodly.Controllers.MoodController;
import com.example.moodly.Models.Mood;
import com.example.moodly.R;

import java.util.ArrayList;

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
        moodController.clearEmotion();
        moodController.clearFilterText();
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
        displayMoodList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
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
                intent.putExtra("MOOD_POSITION", -1);
                startActivityForResult(intent, 0);
            }
        });
        final CharSequence[] filter_choices = {"Anger","Confusion","Disgust","Fear","Happiness","Sadness","Shame","Surprise"};
        final CharSequence[] recentWeekChoice = {"In Recent Week"};
        final ArrayList<Integer> selectedEmotion = new ArrayList<>();
        final ArrayList<Boolean> recentWeek = new ArrayList<>();
        FloatingActionButton filter = (FloatingActionButton) rootView.findViewById(R.id.filterButton);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedEmotion.clear();
                recentWeek.clear();
                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Select filter(s)");
                builder.setMultiChoiceItems(filter_choices, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        // offset of + 1 as the emotion starts with 1
                        int emotion = which + 1;
                        if (isChecked){
                            selectedEmotion.add(emotion);
                        }
                        else if(selectedEmotion.contains(emotion)){
                            selectedEmotion.remove(Integer.valueOf(emotion));
                        }
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moodController.setFilterEmotion(selectedEmotion);
                        getFilterText();
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

        FloatingActionButton refresh = (FloatingActionButton) rootView.findViewById(R.id.refreshButton);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moodController.setFilterRecent(false);
                moodController.clearEmotion();
                moodController.clearFilterText();
                moodList = moodController.getMoodList(userList, true);
                adapter = new MoodAdapter(getActivity(), R.layout.mood_list_item, moodList);
                displayMoodList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });

        loadMore = (Button)rootView.findViewById(R.id.moreMoods);
        loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moodList = moodController.getMoodList(userList, false);
                adapter = new MoodAdapter(getActivity(), R.layout.mood_list_item, moodList);
                displayMoodList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
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
        moodList = moodController.getMoodList(userList, true);
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

    protected void getFilterRecent() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Show Only Moods From Recent Week?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                moodController.setFilterRecent(true);
                moodList = moodController.getMoodList(userList, true);
                adapter = new MoodAdapter(getActivity(), R.layout.mood_list_item, moodList);
                displayMoodList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                moodController.setFilterRecent(false);
                moodList = moodController.getMoodList(userList, true);
                adapter = new MoodAdapter(getActivity(), R.layout.mood_list_item, moodList);
                displayMoodList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                dialog.cancel();
            }
        });
        builder.show();
    }

    protected void getFilterText(){
        AlertDialog.Builder textBuilder = new AlertDialog.Builder(getContext());
        textBuilder.setTitle("Search by Reason text ?");
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        textBuilder.setView(input);
        textBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String filterText = input.getText().toString();
                Toast.makeText(getContext(), filterText, Toast.LENGTH_SHORT).show();
                moodController.setFilterText(filterText);
                getFilterRecent();
            }
        });
        textBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getFilterRecent();
                dialog.cancel();
            }
        });
        textBuilder.show();
    }

}



