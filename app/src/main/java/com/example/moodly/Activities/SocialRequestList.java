package com.example.moodly.Activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.moodly.Controllers.UserController;
import com.example.moodly.Models.User;
import com.example.moodly.R;

import java.util.ArrayList;

/**
 * Created by Victor on 2017-03-08.
 * Fragment to be displayed on SocialBase
 * Loads list of Followers of current User from ElasticSearch
 *
 * @see SocialBase
 */


public class SocialRequestList extends Fragment implements View.OnClickListener {

    protected UserController userController = UserController.getInstance();
    protected User currentUser;
    protected ArrayList<String> userList;

    private View rootView;
    private Button acceptRequestButton;
    private Button declineRequestButton;
    protected ListView displayUserList;
    protected ArrayAdapter<String> adapter;

    // PLACEHOLDER
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        refreshOnline();
        userList = currentUser.getRequests();
        setViews(inflater, container);

        return rootView;
    }

    protected void setViews(LayoutInflater inflater, ViewGroup container) {

        rootView = inflater.inflate(R.layout.social_request_list, container, false);

        displayUserList = (ListView) rootView.findViewById(R.id.display_request_list);
        // Multi-item selection
        displayUserList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        acceptRequestButton = (Button) rootView.findViewById(R.id.accept_request_button);
        acceptRequestButton.setOnClickListener(this);

        declineRequestButton = (Button) rootView.findViewById(R.id.decline_request_button);
        declineRequestButton.setOnClickListener(this);

        adapter = new ArrayAdapter<String>(getActivity(), R.layout.user_list_item, userList);
        displayUserList.setAdapter(adapter);

    }

    // Sync up offline and online
    protected void refreshOnline() {
        // add synchronization elements for part 5 here?
        currentUser = userController.getCurrentUser();
        //followers = userController.getFollowers();

    }

    @Override
    public void onClick(View v) {


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

//        ArrayList<String> outputStrArr = new ArrayList<String>();
//
//        for (int i = 0; i < selectedItems.size(); i++) {
//            outputStrArr.add(selectedItems.get(i));
//        }

        boolean check;
        switch (v.getId()) {
            case R.id.accept_request_button:
                userController.acceptRequest(selectedItems);
                for (String name: selectedItems) {
                    userList.remove(name);
                }
                adapter = new ArrayAdapter<String>(getActivity(), R.layout.user_list_item, userList);
                displayUserList.setAdapter(adapter);

                // code to accept requests
                break;
            case R.id.decline_request_button:
                userController.declineRequest(selectedItems);
                for (String name: selectedItems) {
                    userList.remove(name);
                }
                adapter = new ArrayAdapter<String>(getActivity(), R.layout.user_list_item, userList);
                displayUserList.setAdapter(adapter);

                // code to decline requests
                break;
            default:
                break;
        }

    }

}
