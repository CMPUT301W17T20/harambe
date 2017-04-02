package com.example.moodly.Models;

import java.util.ArrayList;

import io.searchbox.annotations.JestId;

/**
 * Created by mliew on 2017-02-25.
 */

public class User {

    private String name;
    private ArrayList<String> follower;
    private ArrayList<String> following;
    private ArrayList<String> requests;

    @JestId
    private String id;

    public User() {
        follower = new ArrayList<>();
        following = new ArrayList<>();
        requests = new ArrayList<>();
    }

    public User(String myName) {
        name = myName;
        follower = new ArrayList<>();
        following = new ArrayList<>();
        requests = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getFollowers() { return follower; }

    public ArrayList<String> getFollowing() { return following; }

    public ArrayList<String> getRequests() {
        return requests;
    }

    public void setRequests(ArrayList<String> requests) {
        this.requests = requests;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public void addFollower(User user){
        //Bug-proof
        if (!follower.contains(user.getName())){
            //Add my follower
            follower.add(user.getName());

            //Add the other user following
            user.addFollowing(this);
        }
    }

    public void addFollowing(User user){
        //Bug-proof
        if (!following.contains(user.getName())){
            //Add my following
            following.add(user.getName());

            //Add the other user follower
            user.addFollower(this);
        }
    }


    public void addFollowerName(String name){
        //Bug-proof
        if (!follower.contains(name)){
            //Add my following
            follower.add(name);

        }
    }

    public void addFollowingName(String name){
        //Bug-proof
        if (!following.contains(name)){
            //Add to following
            following.add(name);

        }
    }

    public void addRequestName(String name){
        //Bug-proof
        if (!requests.contains(name)){
            //Add to request
            requests.add(name);

        }
    }


    public void removeFollowerName(String name) {
        if (follower.contains(name)){
            // remove name from follower
            int index = follower.indexOf(name);
            follower.remove(index);
        }
    }


    public void removeFollowingName(String name) {
        if (follower.contains(name)){
            // remove name from following
            int index = following.indexOf(name);
            following.remove(index);
        }
    }


    public void removeRequestName(String name) {
        if (requests.contains(name)){
            // remove name from request
            int index = requests.indexOf(name);
            requests.remove(index);
        }
    }


    public void removeFollower(User user) {
        if (follower.contains(user.getName())){
            //Remove my follower
            int index = follower.indexOf(user.getName());
            follower.remove(index);

            //Remove following of the other user
            user.removeFollowing(this);
        }
    }

    public void removeFollowing(User user) {
        if (following.contains(user.getName())){
            //Remove my following
            int index = following.indexOf(user.getName());
            following.remove(index);

            //Remove follower of the other user
            user.removeFollower(this);
        }
    }


}
