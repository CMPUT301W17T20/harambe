package com.example.moodly.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.moodly.Controllers.UserController;
import com.example.moodly.Models.User;
import com.example.moodly.R;

import java.util.ArrayList;

/**
 * Created by Victor on 2017-03-07.
 * Fragment that allows user to search for other users and request to follow them
 * NOT YET IMPLEMENTED/COMPLETED
 *
 * @see SocialBase
 */

/**
 * SocialUserSearch implements the ability for an user to
 * search other user's based on certain criteria.
 */
public class SocialUserSearch extends Fragment implements View.OnClickListener {

    protected UserController userController = UserController.getInstance();
    protected User currentUser;
    protected ArrayList<String> userList;

    private View rootView;
    private Button searchUserButton;
    private Button sendRequestButton;
    protected ListView displayUserList;
    protected ArrayAdapter<String> adapter;

    // PLACEHOLDER
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        refreshOnline();

        // REPLACE WITH ELASTICSEARCH QUERY TO FIND USERS
        userList = new ArrayList<String>();


        setViews(inflater, container);


        return rootView;
    }

    protected void setViews(LayoutInflater inflater, ViewGroup container) {

        rootView = inflater.inflate(R.layout.social_search, container, false);

        displayUserList = (ListView) rootView.findViewById(R.id.display_search_list);
        // Multi-item selection
        displayUserList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        searchUserButton = (Button) rootView.findViewById(R.id.search_button);
        searchUserButton.setOnClickListener(this);
        // CODE TO BE ADDED
        // reset userList here?
        // Should initial search be empty?

        sendRequestButton = (Button) rootView.findViewById(R.id.send_request_button);
        sendRequestButton.setOnClickListener(this);


        adapter = new ArrayAdapter<String>(getActivity(), R.layout.user_list_item, userList);
        displayUserList.setAdapter(adapter);

    }

    // Sync up offline and online
    protected void refreshOnline() {
        // add synchronization elements for part 5 here?
        currentUser = userController.getCurrentUser();
        //followers = userController.getFollowers();

    }

    // Code taken from http://theopentutorials.com/tutorials/android/listview/android-multiple-selection-listview/
    // 2017-03-26 20:53:59

    @Override
    public void onClick(View v) {
        // Taken from http://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
        InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getView().getRootView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        // Create SparseBooleanArray to check selected items
        SparseBooleanArray checked = displayUserList.getCheckedItemPositions();
        // Create Array list of username strings
        ArrayList<String> selectedItems = new ArrayList<String>();
        for (int i = 0; i < checked.size(); i++) {
            // get position in adapter
            int position = checked.keyAt(i);
            // add user if checked/TRUE
            if (checked.valueAt(i))
                selectedItems.add(adapter.getItem(position));
        }

        ArrayList<String> outputStrArr = new ArrayList<String>();

        for (int i = 0; i < selectedItems.size(); i++) {
            outputStrArr.add(selectedItems.get(i));
        }

        // DO SOMETHING WITH outputStrArr

        boolean check;
        switch (v.getId()) {
            case R.id.search_button:
                EditText searchView = (EditText) rootView.findViewById(R.id.search_text);
                String searchString = searchView.getText().toString();
                userList = userController.searchUsers(searchString);
                adapter = new ArrayAdapter<>(getActivity(), R.layout.user_list_item, userList);
                displayUserList.setAdapter(adapter);
                break;
            case R.id.send_request_button:
                userController.makeRequest(outputStrArr);
                break;
            default:
                break;
        }

    }

}

