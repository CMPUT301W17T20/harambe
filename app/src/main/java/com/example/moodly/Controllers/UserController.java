package com.example.moodly.Controllers;

import android.os.AsyncTask;
import android.util.Log;

import com.example.moodly.Models.User;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

/**
 * Created by jkc1 on 2017-03-11.
 */


/**
 * User controller that allows access to users on elastic search
 * @author Jacky Chung
 */
public class UserController extends ElasticSearchController {

    private static UserController instance = null;
    private static User currentUser;
    private static ArrayList<User> following;
    private static ArrayList<User> followers;

    private UserController() {
        following = new ArrayList<>();
        followers = new ArrayList<>();
    }


    /**
     * Gets an instance of the user controller
     * @return the controller
     */
    public static UserController getInstance() {
        if(instance == null) {
            instance = new UserController();
        }

        return instance;
    }


    public ArrayList<User> getFollowers () {
        return followers;
    }
    /* ---------- Controller Functions ---------- */

    /**
     * Used to create a new use, not implemented yet
     */
    public void createUser(String name) {
        User u = new User(name);
        AddUserTask addUserTask = new AddUserTask();
        addUserTask.execute(u);
        currentUser = u;
    }

    public void setCurrentUser(String name){
        UserController.GetUserTask getUserTask = new UserController.GetUserTask();
        getUserTask.execute(name);

        try {
            currentUser = getUserTask.get();
        } catch (Exception e) {
            Log.i("Error", "Cannot get current user out of async object");
        }
    }

    public ArrayList<String> searchUsers(String searchText){
        UserController.SearchUsersTask searchUsersTask = new UserController.SearchUsersTask();
        searchUsersTask.execute(searchText);

        ArrayList<User> userList = new ArrayList<User>();
        try {
            userList = searchUsersTask.get();
        } catch (Exception e) {
            Log.i("Error", "Cannot get users out of async object");
        }

        ArrayList<String> stringList = new ArrayList<String>();
        for (User user: userList) {
            String name = user.getName();
            // if it is not our own name and we have not already followed them
            if(!(name.equals(currentUser.getName())) && (!currentUser.getFollowing().contains(name))) {
                stringList.add(name);
            }
        }
        return stringList;

    }

    /**
     * Gets the user that logged in the app
     * @return current user
     */
    public User getCurrentUser() {

        if (currentUser == null) {
            UserController.GetUserTask getUserTask = new UserController.GetUserTask();
            getUserTask.execute();

            try {
                currentUser = getUserTask.get();

            } catch (Exception e) {
                Log.i("Error", "Cannot get current user out of async object");
            }
        }

        return currentUser;
    }


    /* ---------- Following users ---------- */

    /**
     * Makes a follow request
     * @param names the list of people who you want to follow
     * @return true for success, false for failure
     */
    public boolean makeRequest(ArrayList<String> names) {
        // get user we want to follow
        for (String name: names) {
            UserController.GetUserTask getUserTask = new UserController.GetUserTask();
            getUserTask.execute(name);
            User toFollow;
            // add own user name to request list of person we want to follow
            try {
                toFollow = getUserTask.get();
            } catch (Exception e) {
                Log.i("Error", "Cannot get user out of async object");
                return false;
            }

            // if you have not already made a request or you are following them
            if((!toFollow.getRequests().contains(name)) && (!currentUser.getFollowing().contains(name))) {
                // update the request list
                toFollow.addRequestName(currentUser.getName());

                // update on elastic search
                AddUserTask addUserTask = new AddUserTask();
                addUserTask.execute(toFollow);

            }

        }

        return true;
    }

    /**
     * Accepts a follow request
     * @param names is the list of people who wants to follow you
     * @return true for success, false for failure
     */
    public boolean acceptRequest(ArrayList<String> names) {


        for (String name: names) {
            UserController.GetUserTask getUserTask = new UserController.GetUserTask();

            // get user we allow to follow us
            getUserTask.execute(name);
            User follower;

            try {
                follower = getUserTask.get();
            } catch (Exception e) {
                Log.i("Error", "Cannot get user out of async object");
                return false;
            }
            // add own user name to following list of person we want to follow
            follower.addFollowingName(currentUser.getName());


            // update follower on elastic search
            AddUserTask addUserTask = new AddUserTask();
            addUserTask.execute(follower);

            // remove from own request list, add to follower
            currentUser.removeRequestName(name);
            currentUser.addFollowerName(name);

        }
        // update self on elastic search
        AddUserTask addSelfTask = new AddUserTask();
        addSelfTask.execute(currentUser);

        return true;
    }

    /**
     * Declines a follow request
     * @param names the list of people who's follow request are declined
     * @return true for success, false for failure
     */
    public boolean declineRequest(ArrayList<String> names) {

        for (String name : names) {
            // remove from own request list
            currentUser.removeRequestName(name);

        }
        // update self on elastic search
        AddUserTask addSelfTask = new AddUserTask();
        addSelfTask.execute(currentUser);

        return true;
    }


    /* ---------- Elastic Search Requests ---------- */

    /**
     * Async tasks that gets the list of users from elastic search
     */
    private static class SearchUsersTask extends AsyncTask<String, Void, ArrayList<User>> {

        @Override
        protected ArrayList<User> doInBackground(String... search_parameters) {
            verifySettings();

            ArrayList<User> userList = new ArrayList<User>();

            String query =
                    "{ \n" +
                            "\t\"from\" : 0, \"size\" : 50," +
                            "\n\"query\" : {\n" +
                            "    \"match_phrase_prefix\" : { \"name\" : \"" + search_parameters[0] +
                            "\"     }\n " +
                            "    }\n" +
                            " } ";

            // TODO Build the query
            Search search = new Search.Builder(query)
                    .addIndex("cmput301w17t20")
                    .addType("user")
                    .build();

            try {
                // get the results of our query
                SearchResult result = client.execute(search);
                if(result.isSucceeded()) {
                    // hits
                    List<SearchResult.Hit<User, Void>> foundUsers = result.getHits(User.class);

                    for(int i = 0; i < foundUsers.size(); i++) {
                        User temp = foundUsers.get(i).source;

                        userList.add(temp);
                    }

                } else {
                    Log.i("Error", "Search query failed to find any moods that matched");
                }
            }
            catch (Exception e) {
                Log.i("Error", "Something went wrong when we tried to communicate with the elasticsearch server!");
            }
            return userList;
        }
    }


    /* ---------- Elastic Search Requests ---------- */

    /**
     * Async task that gets the current user's user object from elastic search
     */
    private static class GetUserTask extends AsyncTask<String, Void, User> {

        @Override
        protected User doInBackground(String... search_parameters) {
            verifySettings();

            if (search_parameters.length == 0) {return null;}

            String query =
            "{ \n" +
                    "\t\"terminate_after\" : 1," +
                    "\n\"query\" : {\n" +
                    "    \"match\" : { \"name\" : \"" + search_parameters[0] +
                    "\"     }\n " +
                    "    }\n" +
                    " } ";

            // TODO Build the query
            System.out.println(query);
            Search search = new Search.Builder(query)
                    .addIndex("cmput301w17t20")
                    .addType("user")
                    .build();


            User temp = null;
            try {
                // get the results of our query
                SearchResult result = client.execute(search);
                if(result.isSucceeded()) {
                    // hits
                    List<SearchResult.Hit<User, Void>> foundUsers = result.getHits(User.class);
                    temp = foundUsers.get(0).source;

                } else {
                    Log.i("Error", "Search query failed to find any users that matched");
                }
            }
            catch (Exception e) {

            }

            return temp;
        }
    }

    private static class AddUserTask extends AsyncTask<User, Void, Void> {

        @Override
        protected Void doInBackground(User... users){
            verifySettings();

            for(User user : users) {

                Index index = new Index.Builder(user).index("cmput301w17t20").type("user").build();

                try {
                    DocumentResult result = client.execute(index);
                    if (result.isSucceeded()) {
                        if (user.getId() == null) {
                            user.setId(result.getId());
                            // dont think we have a userlist ?!?
//                            if(moodHistoryList.get(0).getId() == null) {
//                                moodHistoryList.get(0).setId(result.getId());
//                            }

                        }


                    } else {
                        Log.i("Error", "Elasticsearch was not able to add the mood");
                    }
                    // where is the client?
                }
                catch (Exception e) {
                    Log.i("Error", "The application failed to build and send the mood");
                }

            }

            return null;
        }
    }

}
